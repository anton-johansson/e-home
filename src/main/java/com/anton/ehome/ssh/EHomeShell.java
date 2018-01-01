/**
 * Copyright 2018 Anton Johansson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anton.ehome.ssh;

import static com.anton.ehome.utils.ReflectionUtils.writeField;
import static com.anton.ehome.utils.StringUtils.getMutualStart;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isWhitespace;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.apache.commons.lang3.StringUtils.substringAfter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;

import org.apache.sshd.common.SshException;
import org.apache.sshd.common.channel.WindowClosedException;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.dao.IUserDao;
import com.anton.ehome.ssh.cmd.common.CommandMetaData;
import com.anton.ehome.ssh.cmd.common.CommandOptionMetaData;
import com.anton.ehome.ssh.cmd.common.ICommand;
import com.anton.ehome.ssh.cmd.common.ICommunicator;
import com.anton.ehome.ssh.cmd.execption.DisconnectException;
import com.anton.ehome.ssh.cmd.execption.UnknownOptionException;
import com.google.inject.Inject;

/**
 * Provides the SSH shell for the E-Home server.
 */
class EHomeShell implements Command
{
    private static final Logger LOG = LoggerFactory.getLogger(EHomeShell.class);
    private static final int END_OF_TEXT = 3;
    private static final int END_OF_TRANSMISSION = 4;
    private static final int TAB = 9;
    private static final int LINE_FEED = 10;
    private static final int CARRIAGE_RETURN = 13;
    private static final int ESCAPE = 27;
    private static final int BACKSPACE = 127;
    private static final List<Integer> ECHO_BYTES = asList(
            32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, // Space!"#$%&'()*+,-./
            58, 59, 60, 61, 62, 63, 64, 91, 92, 93, 94, 95, 96, 123, 124, 125, 126, // :;<=>?@[\]^_`{|}~
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57, // 0-9
            65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, // A-Z
            97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 // a-z
    );
    private static final List<Character> ANSI_ESCAPE_ENDINGS = asList('A', 'B', 'C', 'D', 'm', '\u001B');
    private static final int COMMAND_HISTORY_SIZE = 10;

    private final List<String> commandHistory = new ArrayList<>(COMMAND_HISTORY_SIZE);
    private final WelcomeTextProvider welcomeTextProvider;
    private final Map<String, CommandMetaData> commands;
    private final IUserDao userDao;
    private final Thread thread;
    private InputStream input;
    private OutputStream output;
    private ExitCallback exitCallback;

    // Current shell state
    private String user;
    private final StringBuilder currentInput = new StringBuilder();
    private int cursorLocation;
    private boolean lastCommandSuccess = true;
    private int commandHistoryIndex = -1;

    @Inject
    EHomeShell(WelcomeTextProvider welcomeTextProvider, Map<String, CommandMetaData> commands, IUserDao userDao)
    {
        this.welcomeTextProvider = welcomeTextProvider;
        this.commands = commands;
        this.userDao = userDao;
        this.thread = new Thread(this::thread, "ssh-thread");
    }

    @Override
    public void start(Environment environment) throws IOException
    {
        environment.addSignalListener(signal ->
        {
            LOG.info("Received signal: {}({})", signal.name(), signal.getNumeric());
        });
        user = environment.getEnv().get(Environment.ENV_USER);
        List<String> commandHistory = userDao.getCommandHistory(user);
        this.commandHistory.addAll(commandHistory);

        String welcomeText = welcomeTextProvider.getWelcomeText().replace("${user}", user);
        send(welcomeText + "\r\n");
        displayPrompt();

        thread.start();
    }

    private void displayPrompt() throws IOException
    {
        String data = new StringBuilder()
                .append(lastCommandSuccess ? "\u001B[32m" : "\u001B[31m")
                .append("\u279C  ")
                .append("\u001B[0m")
                .toString();
        send(data);
    }

    private void thread()
    {
        try
        {
            while (true)
            {
                char character = (char) input.read();

                DataState state = new DataState();
                state.setCancelledHistoryCycling(true);

                if (ECHO_BYTES.contains((int) character))
                {
                    String afterCursor = currentInput.substring(cursorLocation);
                    currentInput.insert(cursorLocation++, character);
                    String value = String.valueOf(character);
                    if (!afterCursor.isEmpty())
                    {
                        value = value + afterCursor + "\u001B[" + afterCursor.length() + "D";
                    }
                    send(value);
                    logCurrentCommand();
                }
                else if (character == ESCAPE)
                {
                    String escapeSequence = "\u001B";
                    while (input.available() > 0)
                    {
                        character = (char) input.read();
                        escapeSequence += character;
                        if (ANSI_ESCAPE_ENDINGS.contains(character))
                        {
                            break;
                        }
                    }
                    handleAnsiEscapeSequence(escapeSequence, state);
                }
                else if (character == END_OF_TEXT)
                {
                    lastCommandSuccess = false;
                    currentInput.setLength(0);
                    cursorLocation = 0;
                    send("\r\n\r\n");
                    displayPrompt();
                    logCurrentCommand();
                }
                else if (character == END_OF_TRANSMISSION)
                {
                    send("\r\nGood-bye!\r\n");
                    exitCallback.onExit(0);
                    return;
                }
                else if (character == CARRIAGE_RETURN)
                {
                    addCommandToHistory(currentInput.toString());
                    StringTokenizer tokenizer = new StringTokenizer(currentInput.toString());
                    currentInput.setLength(0);
                    cursorLocation = 0;
                    if (tokenizer.hasMoreTokens())
                    {
                        executeCommand(tokenizer);
                    }
                    send("\r\n\r\n");
                    displayPrompt();
                }
                else if (character == LINE_FEED)
                {
                    LOG.trace("Ignoring line feed");
                }
                else if (character == BACKSPACE)
                {
                    if (cursorLocation > 0)
                    {
                        String afterCaret = currentInput.toString().substring(cursorLocation) + " ";
                        String data = "\u001B[1D" + afterCaret + "\u001B[" + afterCaret.length() + "D";
                        send(data);
                        currentInput.replace(--cursorLocation, cursorLocation + 1, "");
                        logCurrentCommand();
                    }
                }
                else if (character == TAB)
                {
                    StringTokenizer tokenizer = new StringTokenizer(currentInput.toString());
                    int tokens = tokenizer.countTokens();
                    if (tokens == 1 && !isPreviousCharacterWhitespace())
                    {
                        autocompleteFirst();
                    }
                    else if (tokens > 1)
                    {
                        String command = tokenizer.nextToken();
                        String data = substringAfter(currentInput.toString(), command);
                        Optional.ofNullable(commands.get(command)).ifPresent(metaData -> autocompleteCommand(metaData, data));
                    }
                }
                else
                {
                    LOG.trace("Unhandled character received: {}", character);
                    state.setCancelledHistoryCycling(false);
                }

                if (commandHistoryIndex >= 0 && state.isCancelledHistoryCycling())
                {
                    commandHistoryIndex = -1;
                }
            }
        }
        catch (WindowClosedException e)
        {
            LOG.info("The client closed the connection");
        }
        catch (SshException | InterruptedIOException e)
        {
            LOG.warn("The SSH connection was forcibly shut down", e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void addCommandToHistory(String command)
    {
        if (!commandHistory.isEmpty() && command.equals(commandHistory.get(0)))
        {
            return;
        }

        if (commandHistory.size() == COMMAND_HISTORY_SIZE)
        {
            commandHistory.remove(commandHistory.size() - 1);
        }
        commandHistory.add(0, command);
        userDao.addCommand(user, command);
    }

    private boolean isPreviousCharacterWhitespace()
    {
        return cursorLocation > 0 && isWhitespace(currentInput.toString().substring(cursorLocation - 1, cursorLocation));
    }

    private void executeCommand(StringTokenizer tokenizer) throws IOException
    {
        LOG.info("Executing command: {}", currentInput.toString());

        String commandName = tokenizer.nextToken();
        CommandMetaData metaData = commands.get(commandName);
        if (metaData == null)
        {
            LOG.info("Command was not found");
            send("\r\ncommand not found: " + commandName);
            lastCommandSuccess = false;
        }
        else
        {
            Communicator communicator = new Communicator();
            ICommand command = metaData.getConstructor().get();

            lastCommandSuccess = false;
            try
            {
                parseOptionsAndArguments(tokenizer, command, metaData);
                command.execute(communicator);
                lastCommandSuccess = true;
            }
            catch (UnknownOptionException e)
            {
                LOG.debug("An unknown option was used: " + e.getOptionName());
                send("\r\n" + e.getMessage());
            }
            catch (DisconnectException e)
            {
                LOG.info("Command requested disconnect");
                exitCallback.onExit(0);
            }
            catch (Exception e)
            {
                LOG.error("Unhandled exception occurred while executing the command", e);
                send("\r\nUnknown error occurred: " + e.getMessage());
            }
        }
    }

    private void parseOptionsAndArguments(StringTokenizer tokenizer, ICommand command, CommandMetaData metaData) throws UnknownOptionException
    {
        while (tokenizer.hasMoreTokens())
        {
            String token = tokenizer.nextToken();
            if (token.startsWith("--"))
            {
                String optionName = token.substring(2);
                CommandOptionMetaData optionMetaData = metaData.getOptions()
                        .stream()
                        .filter(meta -> meta.getName().equals(optionName))
                        .findAny()
                        .orElseThrow(() -> new UnknownOptionException(optionName));

                if (optionMetaData.isAcceptsValue())
                {
                    throw new UnsupportedOperationException("Options with values are not yet implemented");
                }
                else
                {
                    Object value = optionMetaData.getConverter().apply("true");
                    writeField(optionMetaData.getField(), command, value);
                }
            }
            else
            {
                throw new UnsupportedOperationException("Arguments are not yet implemented");
            }
        }
    }

    private void autocompleteFirst() throws IOException
    {
        String input = currentInput.toString().trim();
        List<String> matchingCommands = commands.keySet()
                .stream()
                .filter(key -> key.startsWith(input))
                .collect(toList());

        if (matchingCommands.isEmpty())
        {
            LOG.debug("There are no matching commands to auto-complete");
        }
        else if (matchingCommands.size() == 1)
        {
            String command = matchingCommands.get(0) + " ";
            if (input.length() < command.length())
            {
                String data = command.substring(input.length());
                currentInput.append(data);
                cursorLocation += data.length();
                send(data);
                logCurrentCommand();
            }
        }
        else
        {
            String mutualStart = getMutualStart(matchingCommands, input.length());
            if (isBlank(mutualStart))
            {
                LOG.warn("Cycling through auto-completion values are not yet supported");
            }
            else
            {
                currentInput.append(mutualStart);
                cursorLocation += mutualStart.length();
                send(mutualStart);
                logCurrentCommand();
            }
        }
    }

    private void autocompleteCommand(CommandMetaData metaData, String data)
    {
        LOG.warn("Autocompleting commands aren't implemented ({} with data: {})", metaData.getName(), data);
    }

    private void handleAnsiEscapeSequence(String escapeSequence, DataState state) throws IOException
    {
        // RIGHT
        if ("\u001B[C".equals(escapeSequence))
        {
            if (cursorLocation < currentInput.length())
            {
                cursorLocation++;
                send(escapeSequence);
                logCurrentCommand();
            }
        }

        // LEFT
        else if ("\u001B[D".equals(escapeSequence))
        {
            if (cursorLocation > 0)
            {
                cursorLocation--;
                send(escapeSequence);
                logCurrentCommand();
            }
        }

        // CTRL + RIGHT
        else if ("\u001B[1;5C".equals(escapeSequence))
        {
            if (cursorLocation < currentInput.length())
            {
                int newCursorLocation = SshUtils.getRightJumpPosition(currentInput.toString(), cursorLocation);
                int diff = newCursorLocation - cursorLocation;
                cursorLocation = newCursorLocation;
                send("\u001B[" + diff + "C");
                logCurrentCommand();
            }
        }

        // CTRL + LEFT
        else if ("\u001B[1;5D".equals(escapeSequence))
        {
            if (cursorLocation > 0)
            {
                int newCursorLocation = SshUtils.getLeftJumpPosition(currentInput.toString(), cursorLocation);
                int diff = cursorLocation - newCursorLocation;
                cursorLocation = newCursorLocation;
                send("\u001B[" + diff + "D");
                logCurrentCommand();
            }
        }

        // HOME
        else if ("\u001B[H".equals(escapeSequence))
        {
            if (cursorLocation > 0)
            {
                int newCursorLocation = 0;
                int diff = cursorLocation - newCursorLocation;
                cursorLocation = newCursorLocation;
                send("\u001B[" + diff + "D");
                logCurrentCommand();
            }
        }

        // END
        else if ("\u001B[F".equals(escapeSequence))
        {
            int newCursorLocation = currentInput.length();
            if (cursorLocation < newCursorLocation)
            {
                int diff = newCursorLocation - cursorLocation;
                cursorLocation = newCursorLocation;
                send("\u001B[" + diff + "C");
                logCurrentCommand();
            }
        }

        // DEL
        else if ("\u001B[3~".equals(escapeSequence))
        {
            if (cursorLocation < currentInput.length())
            {
                String valueAfter = currentInput.toString().substring(cursorLocation + 1) + " ";
                currentInput.deleteCharAt(cursorLocation);
                send(valueAfter + "\u001B[" + valueAfter.length() + "D");
                logCurrentCommand();
            }
        }

        // UP
        else if ("\u001B[A".equals(escapeSequence))
        {
            state.setCancelledHistoryCycling(false);
            if (!commandHistory.isEmpty())
            {
                commandHistoryCycleUp();
            }
        }

        // DOWN
        else if ("\u001B[B".equals(escapeSequence))
        {
            state.setCancelledHistoryCycling(false);
            if (commandHistoryIndex >= 0)
            {
                commandHistoryCycleDown();
            }
        }
    }

    private void commandHistoryCycleUp() throws IOException
    {
        int previous = commandHistoryIndex;
        if (!isCyclingCommandHistory())
        {
            if (currentInput.length() == 0)
            {
                commandHistoryIndex = 0;
            }
        }
        else if (commandHistoryIndex < commandHistory.size() - 1)
        {
            commandHistoryIndex++;
        }

        if (commandHistoryIndex != previous)
        {
            String oldInput = currentInput.toString();
            String newInput = commandHistory.get(commandHistoryIndex);
            commandHistoryCycleImpl(oldInput, newInput);
        }
    }

    private void commandHistoryCycleImpl(String oldInput, String newInput) throws IOException
    {
        StringBuilder data = new StringBuilder();
        if (cursorLocation != 0)
        {
            data.append("\u001B[" + cursorLocation + "D");
        }
        data.append(newInput);
        int charactersToErase = oldInput.length() - newInput.length();
        if (charactersToErase > 0)
        {
            data.append(repeat(' ', charactersToErase));
            data.append("\u001B[" + charactersToErase + "D");
        }
        send(data.toString());

        currentInput.setLength(0);
        currentInput.append(newInput);
        cursorLocation = newInput.length();
        logCurrentCommand();
    }

    private void commandHistoryCycleDown() throws IOException
    {
        commandHistoryIndex--;

        String oldInput = currentInput.toString();
        String newInput = commandHistoryIndex >= 0 ? commandHistory.get(commandHistoryIndex) : "";
        commandHistoryCycleImpl(oldInput, newInput);
    }

    private boolean isCyclingCommandHistory()
    {
        return commandHistoryIndex >= 0;
    }

    private void logCurrentCommand()
    {
        String command = "";
        for (int i = 0; i < currentInput.length(); i++)
        {
            if (i == cursorLocation)
            {
                command += "[" + currentInput.charAt(i) + "]";
            }
            else
            {
                command += currentInput.charAt(i);
            }
        }
        if (cursorLocation == currentInput.length())
        {
            command += "[]";
        }
        LOG.trace("Current command: {}", command);
    }

    private void send(String output) throws IOException
    {
        this.output.write(output.getBytes());
        this.output.flush();
    }

    @Override
    public void destroy() throws Exception
    {
        LOG.info("Destroying the shell");
        thread.interrupt();
    }

    @Override
    public void setInputStream(InputStream input)
    {
        this.input = input;
    }

    @Override
    public void setOutputStream(OutputStream output)
    {
        this.output = output;
    }

    @Override
    public void setErrorStream(OutputStream err)
    {
    }

    @Override
    public void setExitCallback(ExitCallback callback)
    {
        this.exitCallback = callback;
    }

    /**
     * Default implementation of {@link ICommunicator}.
     */
    private class Communicator implements ICommunicator
    {
        @Override
        public ICommunicator write(String output) throws IOException
        {
            send(output);
            return this;
        }

        @Override
        public ICommunicator newLine() throws IOException
        {
            send("\r\n");
            return this;
        }
    }
}
