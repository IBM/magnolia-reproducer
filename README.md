## Minimal Magnolia reproducer project

Generated from [Magnolia's project and module archetypes](https://docs.magnolia-cms.com/product-docs/6.3/developing/development-how-tos/how-to-use-magnolia-maven-archetypes/), and then added a JUnit5 integration-test with [IBM iX' MagnoliaTomcatExtension](https://github.com/IBM/magkit-test/tree/main/magkit-test-server#using-the-junit5-extension).

That test is supposed to make Magnolia bugs reproducable for Magnolia support, with as little project code as possible.

Build results in a webapp that can be deployed e.g. to Tomcat.

### Building
From the top-level Maven project:

```
mvn --settings <your-magnolia-mvn-settings> package
```

This results in a WAR file + exploded WAR folder in `reproducer-webapp/target`.

In order to build a Docker OCI image with Tomcat to run the webapp, `cd` to `reproducer-webapp` and issue:

```
podman build --build-arg SOURCE_WAR_EXPLODED=target/reproducer-webapp-1.0-SNAPSHOT/ -t magnolia-reproducer .
```

### Running the webapp
#### As Docker (OCI) image
To run the Docker image, issue:

```
podman run -it -p 8080:8080 magnolia-reproducer
```

### As integration test in Maven
To run the `AuthorTomcatTest` as a Maven integration-test:

```
mvn --settings <your-magnolia-mvn-settings> verify
```