# SpringBoot based Kotlin Standalone WebApp Skeleton

This branch uses Tomcat as web-server and exposes REST and GraphQL web services.

## Build Project
```
mvn clean package
```

## Run Application
```
java -jar target/kotlin-springboot-skeleton-1.0.0-SNAPSHOT.jar
```

## GraphQL playground on current schema
```
http://localhost:8080/graphiql
```

Query Users and ask for Id and Name for each:
```
{
  users {
    id
    name
  }
}
```
