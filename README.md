# SpringBoot based Kotlin Standalone WebApp Skeleton

This branch uses Tomcat as web-server and exposes REST and GraphQL web services.

:fire: support Hazelcast for datastore (clustering out-of-the box)

## Build Project
```
mvn clean package
```

## Run Application
```
java -jar -Dport=8080 -Dstore=hazelcast target/kotlin-springboot-skeleton-1.0.0-SNAPSHOT.jar
```

## Run Application & generate initial data (100 offices, 100 users, 10 clock in/out actions per user
```
java -jar -Dport=8080 -Dstore=hazelcast -DgenData=offices:100|users:100|actions:10 target/kotlin-springboot-skeleton-1.0.0-SNAPSHOT.jar
```

## GraphQL Playground on current schema (powered by GraphiQL)
```
http://localhost:8080/graphiql
```
![Image of Graphiql](./graphiql-sample01.png)


# GraphQL usage examples on provided Schema
### Query Users and ask for Id and Name for each:
```
{
  users {
    id
    name
  }
}
```

### Query Users and ask for Id and Name for each, with Paging (first page of 5 rows):
```
{
  users(paging:{offset:0, limit:5}) {
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

### Generate Data : 10 offices, 10 users, 10 clock in&out actions per user
```
mutation {
  genData(offices: 10, users: 10, clockActions: 10)
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
