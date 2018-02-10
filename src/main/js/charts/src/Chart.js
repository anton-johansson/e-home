/*
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
import React, {Component} from 'react';
import ReactTimeout from 'react-timeout';
import PropTypes from 'prop-types';
import {Line} from 'react-chartjs-2';

class Chart extends Component {
  constructor(props) {
    super(props);
    this.state = {data: []};
  }

  componentDidMount() {
    this.updateData();
    this.schedule();
  }

  schedule() {
    this.props.setTimeout(() => {
      this.updateData();
      this.schedule();
    }, this.props.interval * 1000);
  }

  updateData() {
    this.props.reloadDataFunction(data => {
      this.setState(previous => ({data: data}));
    });
  }

  reloadData() {
    return {
      datasets: this.state.data
    };
  }

  getOptions() {
    return {
      responsive: true,
      title: {
        display: true,
        text: this.props.title
      },
      elements: {
        line: {
          borderWidth: 1,
          tension: 0
        },
        point: {
          radius: 0,
          hoverRadius: 5,
          hitRadius: 5
        }
      },
      legend: {
        display: true,
        position: 'top'
      },
      scales: {
        xAxes: [
          {
            type: 'time',
            display: true,
            scaleLabel: {
              display: true,
              labelString: 'Time'
            },
            ticks: {
              major: {
                fontStyle: 'bold'
              }
            }
          }
        ],
        yAxes: [
          {
            display: true,
            scaleLabel: {
              display: true,
              labelString: this.props.yAxisLabel
            },
            ticks: {
              beginAtZero: true
            }
          }
        ]
      }
    };
  }

  render() {
    return (
      <Line data={this.reloadData()} options={this.getOptions()} />
    );
  }
}

Chart.propTypes = {
  title: PropTypes.string.isRequired,
  yAxisLabel: PropTypes.string.isRequired,
  interval: PropTypes.number.isRequired,
  reloadDataFunction: PropTypes.func.isRequired
};

export default ReactTimeout(Chart);
