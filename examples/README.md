## Spring Boot Demo

Requires Java 17

You will require an `application-local.yml` file. It will look something like this:

```yaml
firetail:
  apikey: "PS-02....441b09761c3"
  url: "https://your-apiapi.logging.eu-north-west-99.sandbox.firetail.app"
  ## Cache control before dispatching logs to API
  buffer:
    # Millis
    interval: 100000
    # Max capacity
    capacity: 5

```

Firstly, build the Firetail-Java-Library

```bash
# Build the firetail library
cd ..
./gradlew build publishToMavenLocal
# Run the example
cd examples
./gradlew bootRun
# By default, you'll want to hit this endpoint 5 times before the logs are dispatched
# Otherwise hit it < 5 and wait for 10 seconds
curl http://localhost:8080/hello
```

You can then login to the [FireTail app](https://www.sandbox.firetail.app/) and see your logs

## Open API documentation

This example uses OpenAPI v3

 1. http://localhost:8080/api-docs
 2. http://localhost:8080/swagger-ui.html