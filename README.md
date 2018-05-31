# SpringBoot based Kotlin Standalone WebApp Skeleton

- Spring Boot (Embedded Web Server + REST)
- Jackson
- Logback

Different branches provide different Spring Boot implemntation models
- [netty-reactive-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/netty-reactive-webserver) Netty - Reactive based Web Server implementation
- [undertow-webserver](https://github.com/oeil/kotlin-springboot-skeleton/tree/undertow-webserver) Undertow - Servlet based Web Server implementation

## Build Project
```
mvn clean package
```

## Run Application
```
java -jar target/kotlin-springboot-skeleton-1.0.0-SNAPSHOT.jar
```
