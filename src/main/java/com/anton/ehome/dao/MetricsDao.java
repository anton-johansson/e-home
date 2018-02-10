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

import static java.time.ZoneOffset.UTC;

import java.time.LocalDateTime;
import java.util.List;

import org.influxdb.InfluxDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anton.ehome.domain.Metric;
import com.google.inject.Inject;

/**
 * Default implementation of {@link IMetricsDao}.
 */
class MetricsDao extends AbstractDao implements IMetricsDao
{
    private static final Logger LOG = LoggerFactory.getLogger(MetricsDao.class);
    private static final String METRICS_QUERY = "SELECT time, value FROM metric WHERE nodeId = [nodeId] AND time >= '[from]' AND time <= '[to]'";

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

    @Override
    public List<Metric> getMetrics(byte nodeId, LocalDateTime from, LocalDateTime to)
    {
        String query = METRICS_QUERY
                .replace("[nodeId]", String.valueOf(nodeId))
                .replace("[from]", from.atZone(UTC).toString())
                .replace("[to]", to.atZone(UTC).toString());

        return selectMany(query, Metric.class);
    }
}
