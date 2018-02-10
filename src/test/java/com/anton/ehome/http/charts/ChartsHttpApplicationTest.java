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
package com.anton.ehome.http.charts;

import static java.util.Arrays.asList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;
import org.junit.Test;
import org.mockito.Mock;

import com.anton.ehome.common.AbstractTest;
import com.anton.ehome.conf.Chart;
import com.anton.ehome.conf.ChartDataSet;
import com.anton.ehome.conf.Config;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.dao.IMetricsDao;
import com.anton.ehome.domain.Metric;

/**
 * Unit tests of {@link ChartsHttpApplication}.
 */
public class ChartsHttpApplicationTest extends AbstractTest
{
    private @Mock IConfigService configService;
    private @Mock IMetricsDao metricsDao;
    private Server server;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        ChartsHttpApplication servlet = new ChartsHttpApplication(configService, metricsDao);

        ServletHandler handler = new ServletHandler();

        ServletHolder holder = new ServletHolder();
        holder.setServlet(servlet);
        holder.setName("charts");

        ServletMapping mapping = new ServletMapping();
        mapping.setPathSpec("/charts/*");
        mapping.setServletName("charts");

        handler.addServlet(holder);
        handler.addServletMapping(mapping);

        server = new Server(1337);
        server.setHandler(handler);
        server.start();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        server.stop();
    }

    @Override
    protected void initMocks()
    {
        ChartDataSet dataSet = new ChartDataSet();
        dataSet.setColor("green");
        dataSet.setLabel("Washing machine");
        dataSet.setDeviceId(1);

        Chart chart = new Chart();
        chart.setName("electrics");
        chart.setTitle("Electrics");
        chart.setYAxisLabel("Watts");
        chart.setDataSets(asList(dataSet));

        Config config = new Config();
        config.setCharts(asList(chart));

        when(configService.getCurrentConfig()).thenReturn(config);

        Metric metric1 = new Metric();
        metric1.setTime(LocalDateTime.of(2018, 2, 18, 21, 58, 12).toInstant(ZoneOffset.UTC));
        metric1.setValue(12.3);

        Metric metric2 = new Metric();
        metric1.setTime(LocalDateTime.of(2018, 2, 18, 21, 58, 51).toInstant(ZoneOffset.UTC));
        metric2.setValue(66.6);

        when(metricsDao.getMetrics(eq((byte) 1), any(), any())).thenReturn(asList(metric1, metric2));
    }

    @Test
    public void testGettingMeta() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/charts/electrics/meta.json").openConnection();
        int responseCode = connection.getResponseCode();
        String body = IOUtils.toString(connection.getInputStream(), "UTF-8");

        assertEquals(200, responseCode);
        assertEquals("{\"yAxisLabel\":\"Watts\",\"title\":\"Electrics\"}", body);
    }

    @Test
    public void testGettingData() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/charts/electrics/data.json").openConnection();
        int responseCode = connection.getResponseCode();
        String body = IOUtils.toString(connection.getInputStream(), "UTF-8");

        assertEquals(200, responseCode);
        assertEquals("[{\"label\":\"Washing machine\",\"color\":\"green\",\"dataPoints\":[{\"time\":\"2018-02-18T21:58:51Z\",\"value\":12.3},{\"value\":66.6}]}]", body);
    }

    @Test
    public void testUnknownChart() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/charts/unknown-chart").openConnection();
        int responseCode = connection.getResponseCode();

        assertEquals(400, responseCode);
    }

    @Test
    public void testGettingIndexPage() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:1337/charts/electrics").openConnection();
        int responseCode = connection.getResponseCode();
        String body = IOUtils.toString(connection.getInputStream(), "UTF-8");

        assertEquals(200, responseCode);
        assertEquals("<!DOCTYPE html><html><head><meta charset=\"utf-8\"/><title>Hello world</title></head><body><noscript>You need to enable JavaScript to run this application.</noscript><div id=\"root\"></div><script type=\"text/javascript\" src=\"./static/js/main.c51a02b9.js\"></script></body></html>", body);
    }
}
