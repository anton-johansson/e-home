/**
 * Copyright 2017 Anton Johansson
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
package com.anton.ehome.dao;

import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Default implementation of {@link IMetricsDao}.
 */
class MetricsDao extends AbstractDao implements IMetricsDao
{
    private static final Logger LOG = LoggerFactory.getLogger(MetricsDao.class);

    @Inject
    MetricsDao(InfluxDB influx)
    {
        super(influx);
    }

    @Override
    public void save(byte nodeId, double value)
    {
        LOG.debug("Saving metric for node {} with value: {}", nodeId, value);
        insert().measurement("metric")
                .field("nodeId", nodeId, true)
                .field("value", value)
                .execute();
    }
}
