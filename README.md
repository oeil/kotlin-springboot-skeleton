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

## GraphQL Playground on current schema (powered by GraphiQL)
```
http://localhost:8080/graphiql
```
![Image of Graphiql](./graphiql-sample01.png)


# GraphQL samples on provided Schema
### Query Users and ask for Id and Name for each:
```
{
  users {
    id
    name
  }
}
```
### Query Users with associated ClockActions (including associated Office name per ClockAction):
```
users {
    id
    name
    clockActions {
      id
      type
      desc
      timestamp
      office {
        name
      }
    }
  }
```
### Query User for Id=1 with associated ClockActions (including associated Office name per ClockAction):
```
users(id: 1) {
    id
    name
    clockActions {
      id
      type
      desc
      timestamp
      office {
        name
      }
    }
  }
```

### Add new user name="oeil" and return its generated id
```
mutation {
  user(name: "oeil") {
    id
  }
}
```

### Add office user name="Nancy" and return its generated id and name
```
mutation {
  office(name: "Nancy") {
    id
    name
  }
}
```

### Clock User In for userId=1 and officeId=1
```
mutation {
  clockIn(userId: 1, officeId: 1) {
    id
    type
    timestamp
    desc
    office {
      name
    }
  }
}
```
### Clock User Out for userId=1 and officeId=1
```
mutation {
  clockOut(userId: 1, officeId: 1) {
    id
    type
    timestamp
    desc
    office {
      name
    }
  }
}
```
