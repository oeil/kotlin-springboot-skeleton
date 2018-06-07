# SpringBoot based Kotlin Standalone WebApp Skeleton

This branch uses Netty as web-server with reactive (async) paradigm.

- Spring Boot (Embedded Web Server + REST)
- Jackson
- Logback

Those branches provide skeletons for Spring Boot apps based on different flavors:
- [netty-reactive-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/netty-reactive-webserver) Netty (embedded) - Reactive REST Web Services
- [tomcat-rest-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/tomcat-rest-webserver) Tomcat (embedded) - Servlet based REST Web Services
- [undertow-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/undertow-webserver) Undertow (embedded) - Servlet based REST Web Services

- [tomcat-graphql-rest-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/tomcat-graphql-rest-webserver) Tomcat (embedded) - GraphQL and REST Web Services


## Build Project
```
mvn clean package
```

## Run Application
```
java -jar target/kotlin-springboot-skeleton-1.0.0-SNAPSHOT.jar
```
