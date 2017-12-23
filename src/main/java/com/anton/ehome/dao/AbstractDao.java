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
package com.anton.ehome.dao;

import static com.anton.ehome.dao.InfluxUtils.DATABASE_NAME;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.List;
import java.util.Optional;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBException;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBResultMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines an abstract skeleton for performing persistance operations against the {@link InfluxDB} database.
 */
abstract class AbstractDao
{
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDao.class);
    private static final InfluxDBResultMapper MAPPER = new InfluxDBResultMapper();

    private final InfluxDB influx;

    protected AbstractDao(InfluxDB influx)
    {
        this.influx = influx;
    }

    /**
     * Starts executing an insert statement.
     */
    protected final InsertBuilder insert()
    {
        return new InsertBuilder();
    }

    /**
     * Performs a query against the {@link InfluxDB}.
     *
     * @param query The query to perform.
     * @return The result.
     */
    protected final <T> Optional<T> selectOne(String query, Class<T> clazz)
    {
        LOG.debug("Executing query: " + query);

        QueryResult result = influx.query(new Query(query, DATABASE_NAME));
        List<T> objects = MAPPER.toPOJO(result, clazz);
        if (objects.size() > 1)
        {
            LOG.error("The query generated more than one result");
            throw new InfluxDBException("The query generated more than one result");
        }
        else if (objects.isEmpty())
        {
            LOG.trace("No object was found using the given query");
            return Optional.empty();
        }
        else
        {
            LOG.trace("The query generated exactly one object, as expected");
            return Optional.of(objects.get(0));
        }
    }

    /**
     * Builds an insert statement.
     */
    protected class InsertBuilder
    {
        private InsertBuilder()
        {
        }

        /**
         * Sets measurement of the insert.
         *
         * @param measurement The measurement to use.
         * @return Returns the builder.
         */
        protected InsertMeasurementBuilder measurement(String measurement)
        {
            return new InsertMeasurementBuilder(measurement);
        }
    }

    /**
     * Builds an insert statement.
     */
    protected class InsertMeasurementBuilder
    {
        private final String measurement;
        private final Builder builder;

        private InsertMeasurementBuilder(String measurement)
        {
            this.measurement = measurement;
            builder = Point.measurement(measurement).time(System.currentTimeMillis(), MILLISECONDS);
        }

        /**
         * Adds a {@link String}-value field.
         *
         * @param field The name of the field.
         * @param value The value of the field.
         * @return Returns the builder.
         */
        protected InsertMeasurementBuilder field(String field, String value)
        {
            builder.addField(field, value);
            return this;
        }

        /**
         * Executes the insert statement.
         */
        protected void execute()
        {
            LOG.debug("Executing insert statement for measurement '{}'", measurement);
            influx.write(DATABASE_NAME, null, builder.build());
        }
    }
}
