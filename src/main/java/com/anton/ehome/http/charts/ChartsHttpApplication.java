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

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.anton.ehome.conf.Chart;
import com.anton.ehome.conf.ChartDataSet;
import com.anton.ehome.conf.IConfigService;
import com.anton.ehome.dao.IMetricsDao;
import com.anton.ehome.domain.Metric;
import com.anton.ehome.http.common.AbstractHttpApplication;
import com.anton.ehome.utils.JsonUtils;
import com.google.inject.Inject;

/**
 * Handles charts.
 */
class ChartsHttpApplication extends AbstractHttpApplication
{
    private static final int CHART_HOURS = 12;

    private final IConfigService configService;
    private final IMetricsDao metricsDao;

    @Inject
    ChartsHttpApplication(IConfigService configService, IMetricsDao metricsDao)
    {
        super("charts");
        this.configService = configService;
        this.metricsDao = metricsDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String path = getActualPath(request);
        if (isBlank(path))
        {
            path = "/";
        }
        if (isStaticResource(path))
        {
            proxyToResource("/html-charts" + path, response);
            return;
        }
        Optional<Chart> optionalChart = getChart(path);
        if (!optionalChart.isPresent())
        {
            response.sendError(BAD_REQUEST_400);
            return;
        }
        Chart chart = optionalChart.get();

        path = path.substring(chart.getName().length() + 1);
        if (isBlank(path) || "/".equals(path))
        {
            path = "/index.html";
        }
        if ("/data.json".equals(path))
        {
            List<DataSet> dataSets = chart.getDataSets()
                    .stream()
                    .map(this::toDataSet)
                    .collect(toList());

            JsonUtils.write(dataSets, response.getOutputStream());
        }
        else if ("/meta.json".equals(path))
        {
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", chart.getTitle());
            meta.put("yAxisLabel", chart.getYAxisLabel());

            JsonUtils.write(meta, response.getOutputStream());
        }
        else
        {
            proxyToResource("/html-charts" + path, response);
        }
    }

    private DataSet toDataSet(ChartDataSet config)
    {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusHours(CHART_HOURS);

        List<Metric> dataPoints = metricsDao.getMetrics((byte) config.getDeviceId(), from, to);

        DataSet dataSet = new DataSet();
        dataSet.setColor(config.getColor());
        dataSet.setLabel(config.getLabel());
        dataSet.setDataPoints(dataPoints);
        return dataSet;
    }

    private boolean isStaticResource(String path)
    {
        return path.equals("/asset-manifest.json")
            || path.equals("/server-worker.js")
            || path.startsWith("/static/");
    }

    private Optional<Chart> getChart(String path)
    {
        int index = StringUtils.indexOf(path, "/", 1);
        String chartName = index < 0 ? path.substring(1) : path.substring(1, index);
        return configService.getCurrentConfig()
                .getCharts()
                .stream()
                .filter(chart -> chartName.equals(chart.getName()))
                .findAny();
    }
}
