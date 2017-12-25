# E-Home

[![Build Status](https://img.shields.io/travis/anton-johansson/e-home/master.svg)](https://travis-ci.org/anton-johansson/e-home)
[![Codecov](https://img.shields.io/codecov/c/github/anton-johansson/e-home.svg)](https://codecov.io/gh/anton-johansson/e-home)
[![License](https://img.shields.io/hexpm/l/plug.svg?maxAge=2592000)](https://raw.githubusercontent.com/anton-johansson/e-home/master/LICENSE)

An E-Home controller server.


## Prerequisites

 * The application requires the [RXTX](http://rxtx.qbang.org) native library to run. This can be installed on a Debian-based system using `sudo apt install librxtx-java`. Hopefully this dependency will be replaced with [jSerialComm](http://fazecast.github.io/jSerialComm) which does not require separately installed natives. See [this issue](https://github.com/whizzosoftware/WZWave/issues/11) for more information.

 * It requires an [InfluxDB](https://www.influxdata.com) instance running. Use the following to run it as a Docker container:

```sh
docker run \
    --detach \
    --name influxdb \
    --restart always \
    --publish 8086:8086 \
    --volume /home/anton/influxdb-data:/var/lib/influxdb \
    influxdb:1.4.2
```


## License

Apache License © [Anton Johansson](https://github.com/anton-johansson)
