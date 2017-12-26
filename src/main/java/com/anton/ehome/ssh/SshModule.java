package com.anton.ehome.ssh;

import org.apache.sshd.server.Command;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Contains IOC bindings for the SSH module.
 */
public class SshModule extends AbstractModule
{
    @Override
    protected void configure()
    {
        bind(IDaemon.class).to(SshDaemon.class).in(Singleton.class);
        bind(WelcomeTextProvider.class).in(Singleton.class);
        bind(Command.class).to(EHomeShell.class);
    }
}
