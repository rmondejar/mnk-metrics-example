## Metrics

### Setup

First we need a JDK, easy to install with SDKMAN!

```sdk install java 17.0.2-tem```


```
brew install prometheus
vi /usr/local/etc/prometheus.yml
  - job_name: "micronaut"
    metrics_path: "/prometheus"
    scrape_interval: 5s
    static_configs:
    - targets: ["127.0.0.1:8080"]
```

Last but least, Grafana

```
brew install grafana
```

### Run

```
./gradlew run
```

```
brew services start prometheus
open http://localhost:9090/targets
open http://localhost:9090/graph
```

We need to run Grafana, and our service
```
brew services start grafana
open http://localhost:3000 
    (user: admin, pwd: admin)

```



Some testing 

```
curl http://localhost:8080/metrics/rest
```

```json
{"names":["executor","executor.active", "...","rest.ping","...","system.load.average.1m"]}
```

```
curl http://localhost:8080/metrics/rest.ping
```

```json
{"name":"rest.ping", 
  "measurements":[{"statistic":"COUNT","value":2.0}],
  "availableTags":[
    {"tag":"controller","values":["index"]},
    {"tag":"action","values":["ping"]}
  ]
}
```

```
curl http://localhost:8080/prometheus
```

```
# HELP rest_ping_total  
# TYPE rest_ping_total counter
rest_ping_total{action="ping",controller="index",} 9.0
```