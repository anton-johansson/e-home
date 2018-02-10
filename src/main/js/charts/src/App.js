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
import Chart from './Chart';

export default class App extends Component {
  constructor(props) {
    super(props);
    this.reloadData = this.reloadData.bind(this);
    this.state = {
      title: '',
      yAxisLabel: ''
    };

    var path = this.getPath();
    fetch(path + 'meta.json')
        .then(response => response.json())
        .then(data => this.setState(data));
  }

  reloadData(callback) {
    var path = this.getPath();
    fetch(path + 'data.json')
        .then(response => response.json())
        .then(data => callback(this.processData(data)));
  }

  getPath() {
    var path = window.location.pathname;
    if (!path.endsWith('/')) {
      path = path + '/';
    }
    return path;
  }

  processData(data) {
    return data.map(set => {
      set.borderColor = set.color;
      set.fill = false;
      set.data = set.dataPoints.map(point => {
        point.x = point.time;
        point.y = point.value;
        delete point.time;
        delete point.value;
        return point;
      });
      delete set.color;
      delete set.dataPoints;
      return set;
    });
  }

  render() {
    return (
      <Chart title={this.state.title} yAxisLabel={this.state.yAxisLabel} interval={5} reloadDataFunction={this.reloadData} />
    );
  }
}
