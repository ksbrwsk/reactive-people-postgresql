# reactive-talk-202012

#### Demo code for my Reactive Spring Talk 12/2020.

**Prerequisites:**

* [Java 15](https://adoptopenjdk.net/)
* [Apache Maven](https:http://maven.apache.org/)
* [Docker](https://www.docker.com/)
* Optional [Postman](https://www.postman.com/)

If you have Docker installed, grab an PostgreSQL image from Docker Hub and run the image.
```bash
docker pull bitnami/postgresql:12
docker run --name postgresql -e POSTGRESQL_PASSWORD=password123 -e POSTGRES_DB=spring -p 5432:5432 bitnami/postgresql:12
```
The PostgreSQL database schema is located under
```bash
reactive-talk-202012/src/main/resources/schema.sql
```

Application properties can be configured in

```bash
reactive-talk-202012/src/main/resources/application.properties
```

#### How to build and run

Type

```bash
mvn package
mvn spring-boot:run
```

to build and run the application.

Use curl, httpie etc. to call the HTTP Endpoints, or use Postman, a collection file is located under
```bash
reactive-talk-202012.postman_collection.json
```

**Themes:**

* Spring Webflux
* Router Functions/Handler Functions
* R2DBC Database Connectivity
* Unit Testing
* Integration Testing with Testcontainers

####TODO: create Docker Image