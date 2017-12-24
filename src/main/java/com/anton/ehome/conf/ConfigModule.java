package com.anton.ehome.conf;

import com.anton.ehome.dao.IConfigDao;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Contains IOC bindings for the configurations.
 */
public class ConfigModule extends AbstractModule
{
    @Override
    protected void configure()
    {
    }

    /**
     * Provides the latest configuration in the database.
     */
    @Provides
    private Config configuration(IConfigDao configDao)
    {
        return configDao.getCurrentConfig();
    }
}
