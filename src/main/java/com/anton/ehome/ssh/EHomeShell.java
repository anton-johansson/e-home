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

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.sshd.common.SshException;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Provides the SSH shell for the E-Home server.
 */
class EHomeShell implements Command
{
    private static final Logger LOG = LoggerFactory.getLogger(EHomeShell.class);
    private static final int END_OF_TEXT = 3;
    private static final int END_OF_TRANSMISSION = 4;
    private static final int LINE_FEED = 10;
    private static final int CARRIAGE_RETURN = 13;
    private static final int ESCAPE = 27;
    private static final int BACKSPACE = 127;

    // CSOFF
    private static final List<Integer> ECHO_BYTES = asList(
             32,  33,  34,  35,  36,  37,  38,  39,  40,  41,  42,  43,  44,  45,  46,  47, // Space!"#$%&'()*+,-./
             58,  59,  60,  61,  62,  63,  64,  91,  92,  93,  94,  95,  96, 123, 124, 125, 126, // :;<=>?@[\]^_`{|}~
             48,  49,  50,  51,  52,  53,  54,  55,  56,  57, // 0-9
             65,  66,  67,  68,  69,  70,  71,  72,  73,  74,  75,  76,  77,  78,  79,  80,  81,  82,  83,  84,  85,  86,  87,  88,  89,  90, // A-Z
             97,  98,  99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 // a-z
    );
    // CSON

    private static final List<Character> ANSI_ESCAPE_ENDINGS = asList('A', 'B', 'C', 'D', 'm', '\u001B');

    private final Thread thread;
    private final WelcomeTextProvider welcomeTextProvider;
    private InputStream input;
    private OutputStream output;
    private ExitCallback exitCallback;

    // Current shell state
    private final StringBuilder currentInput = new StringBuilder();
    private int cursorLocation;
    private boolean lastCommandSuccess = true;

    @Inject
    EHomeShell(WelcomeTextProvider welcomeTextProvider)
    {
        this.welcomeTextProvider = welcomeTextProvider;
        this.thread = new Thread(this::thread, "ssh-thread");
    }

    @Override
    public void start(Environment environment) throws IOException
    {
        environment.addSignalListener(signal ->
        {
            LOG.info("Received signal: {}({})", signal.name(), signal.getNumeric());
        });
        String user = environment.getEnv().get(Environment.ENV_USER);

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
                    handleAnsiEscapeSequence(escapeSequence);
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
                    send("\r\nBye!\r\n");
                    exitCallback.onExit(0);
                    return;
                }
                else if (character == CARRIAGE_RETURN)
                {
                    send("\r\n\r\n");
                    displayPrompt();
                    currentInput.setLength(0);
                    cursorLocation = 0;
                    logCurrentCommand();
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
            }
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

    private void handleAnsiEscapeSequence(String escapeSequence) throws IOException
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
        LOG.info("Current command: {}", command);
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
}
