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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

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
     * Performs a query against the {@link InfluxDB} to select one single item.
     *
     * @param query The query to perform.
     * @param clazz The class to convert the result too.
     * @return The result.
     */
    protected final <T> Optional<T> selectOne(String query, Class<T> clazz)
    {
        List<T> objects = selectMany(query, clazz);
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
     * Performs a query against the {@link InfluxDB} and returns multiple results.
     *
     * @param query The query to perform.
     * @param clazz The class to convert results to.
     * @return Returns the list of items found.
     */
    protected final <T> List<T> selectMany(String query, Class<T> clazz)
    {
        LOG.debug("Executing query: {}", query);
        QueryResult result = influx.query(new Query(query, DATABASE_NAME));
        return MAPPER.toPOJO(result, clazz);
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
        private final Set<String> fields = new HashSet<>();
        private final String measurement;
        private final long time;
        private final Builder builder;
        private String retentionPolicy;

        private InsertMeasurementBuilder(String measurement)
        {
            this.measurement = measurement;
            this.time = System.currentTimeMillis();
            builder = Point.measurement(measurement).time(time, MILLISECONDS);
        }

        /**
         * Sets the retention policy of the data that is inserted.
         *
         * @param retentionPolicy The retention policy to use.
         * @return Returns the builder.
         */
        protected InsertMeasurementBuilder retentionPolicy(String retentionPolicy)
        {
            if (this.retentionPolicy != null)
            {
                throw new IllegalStateException("retentionPolicy can't be set twice");
            }
            this.retentionPolicy = retentionPolicy;
            return this;
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
            return field(field, value, false);
        }

        /**
         * Adds a {@link String}-value field.
         *
         * @param field The name of the field.
         * @param value The value of the field.
         * @param indexed Whether or not this field should be indexed.
         * @return Returns the builder.
         */
        protected InsertMeasurementBuilder field(String field, String value, boolean indexed)
        {
            if (!fields.add(field))
            {
                throw new IllegalArgumentException("The field '" + field + "' is already added");
            }

            if (indexed)
            {
                builder.tag(field, value);
            }
            builder.addField(field, value);
            return this;
        }

        /**
         * Adds a {@link String}-value field, using a function that accepts the time of insertion.
         *
         * @param field The name of the field.
         * @param function The function that supplies the value, using the time field.
         * @param indexed Whether or not this field should be indexed.
         * @return Returns the builder.
         */
        protected InsertMeasurementBuilder field(String field, Function<Long, String> function, boolean indexed)
        {
            String value = function.apply(time);
            return field(field, value, indexed);
        }

        /**
         * Executes the insert statement.
         */
        protected void execute()
        {
            LOG.debug("Executing insert statement for measurement '{}'", measurement);
            influx.write(DATABASE_NAME, retentionPolicy, builder.build());
        }
    }
}
