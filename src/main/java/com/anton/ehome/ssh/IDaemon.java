package com.anton.ehome.ssh;

/**
 * Provides a common interface for all daemons.
 */
public interface IDaemon
{
    /**
     * Starts this daemon.
     *
     * @return Returns whether or not the daemon could be started.
     */
    boolean start();

    /**
     * Stops this daemon.
     */
    void stop();
}
