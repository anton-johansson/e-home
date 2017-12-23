# E-Home

An E-Home controller server.


## Prerequisites

 * The application requires the RXTX native library to run. This can be installed on a Debian-based system using `sudo apt install librxtx-java`.

 * It requires an InfluxDB instance running. Use the following to run it as a Docker container:

```sh
docker run \
    --detach \
    --name influxdb \
    --publish 8086:8086 \
    --volume /home/anton/influxdb-data:/var/lib/influxdb \
    influxdb:1.4.2
```


## License

Apache License © [Anton Johansson](https://github.com/anton-johansson)
