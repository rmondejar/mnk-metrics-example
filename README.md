## Testing Micronaut Metrics

Guidelines to test our microservice locally

### Setup

First we need a JDK, easy to install with SDKMAN!

```sdk install java 17.0.2-tem```

Second, we should run a Prometheus instance, for example

```brew install prometheus```

And add our microservice to the configuration:
```
vi /usr/local/etc/prometheus.yml

  - job_name: "micronaut"
    metrics_path: "/prometheus"
    scrape_interval: 5s
    static_configs:
    - targets: ["127.0.0.1:8080"]
```

Last but least, install Grafana, for example

```brew install grafana```

### Verify

First we should test our microservice

```./gradlew test```

Expecting a proper result:
```
    ✔ Metrics Endpoint Successful Test
    ✔ Prometheus Endpoint Successful Test
    ✔ Ping Endpoint Successful Test
    ✔ Custom Metrics Successful Test
```
We can also run and check our endpoints too

```./gradlew run```

Ping endpoint

```
curl http://localhost:8080/ping/hello
curl http://localhost:8080/ping/world
...
```

Metrics endpoint
```
curl http://localhost:8080/metrics
```
Result:
```json
{"names":[
  "executor",
  "executor.active",
  "(...)",
  "rest.ping",
  "(...)",
  "system.load.average.1m"
]}
```
Our custom metric endpoint
```
curl http://localhost:8080/metrics/rest.ping
```
Result:
```json
{"name":"rest.ping", 
  "measurements":[{"statistic":"COUNT","value":2.0}],
  "availableTags":[
    {"tag":"controller","values":["index"]},
    {"tag":"action","values":["ping"]}
  ]
}
```
Prometheus endpoint
```
curl http://localhost:8080/prometheus
```
Result:
```
(...)
# HELP rest_ping_total  
# TYPE rest_ping_total counter
rest_ping_total{action="ping",controller="index",} 2.0
(...)
```

### Monitoring

Now we are ready to run everything and visualize our data,

#### Microservice
If it is not alive, let's run again our microservice:

```./gradlew run```

#### Prometheus
Then, let's start our prometheus and check the targets
```
brew services start prometheus
open http://localhost:9090/targets
```
Our ping service should be there and UP:
`http://127.0.0.1:8080/prometheus`

Next is to check some metrics in the graph tab
```
open http://localhost:9090/graph
    * search jvm_threads_states_threads)
    * search rest_ping_total)
```
#### Grafana
Finally, we need to run Grafana locally too
```
brew services start grafana
open http://localhost:3000 
    * set user / pwd to admin
```

Next, let's create a new data source for Prometheus
```
open http://localhost:3000/datasources/new
    * set URL to http://localhost:9090
```

Then, first to visualize http successful calls to our microservice
we need to create a board, and a new panel with the following query

```
open http://localhost:3000/dashboard/new
    * create new board
    * add panel
    * set title to "HTTP Server Success"
    * metrics browser > sum without (instance,job,exception) 
        (increase(http_server_requests_seconds_count{status=~"2.."}[1m]))
```

Lastly to visualize our custom metric counter, we need a new panel
and add another query

```
open http://localhost:3000/dashboard/new
    * add panel
    * set title to "Ping Counter"
    * metrics browser > sum(increase(rest_ping_total[1m]))
```
