## Spring Boot Demo

Requires Java 17

You will require an `application-local.yaml` file. It will look something like this:

```yaml
firetail:
  apikey: "PS-02....441b09761c3"
  url: "https://your-apiapi.logging.eu-north-west-99.sandbox.firetail.app"
```

Firstly, build the Firetail-Java-Library

```bash
# Build the firetail library
cd ..
./gradlew build publishToMavenLocal
# Run the example
cd examples
./gradlew bootRun
curl http://localhost:8080/hello
```

You can then login to the [FireTail app](https://www.sandbox.firetail.app/) and see your logs

## Open API documentation

This example uses OpenAPI v3

 1. http://localhost:8080/api-docs
 2. http://localhost:8080/swagger-ui.html