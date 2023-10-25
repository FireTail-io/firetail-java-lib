Spring Boot Demo

Requires Java 17

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