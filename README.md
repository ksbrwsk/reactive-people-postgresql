# reactive-talk-202012

#### Demo code for my Reactive Spring Talk 12/2020.

![Java CI with Maven](https://github.com/ksbrwsk/reactive-talk-202012/workflows/Java%20CI%20with%20Maven/badge.svg)

**Prerequisites:**

* [Java 21](https://openjdk.net/)
* [Apache Maven](https:http://maven.apache.org/)
* [Docker](https://www.docker.com/)
* Optional [Postman](https://www.postman.com/)

**Themes:**

* Spring Webflux
* Router Functions/Handler Functions
* R2DBC Database Connectivity
* Unit Testing
* Integration Testing with Testcontainers
* Docker build/compose
* OpenAPI / Swagger API Documentation
* Spring RestDocs documentation

If you have Docker installed, grab a PostgreSQL image from Docker Hub and run the image.
```bash
docker pull postgres:15.4-alpine
docker run --name postgresql -e POSTGRESQL_PASSWORD=password123 -e POSTGRES_DB=spring -p 5432:5432 postgres:15.4-alpine
```
Or else, grab the app image via 
```bash
docker pull ksabrwsk/reactive-talk-202012:1.0.0
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

to build and run the application on your local environment.

To run the app as a Docker Container type the following commands to build
an run the image:
```bash
docker build ./ -t reactive-talk-202012
docker-compose up --force-recreate
```

Use curl, httpie etc. to call the HTTP Endpoints, or use Postman, a collection file is located under
```bash
reactive-talk-202012.postman_collection.json
```

Point your browser to
```bash
http://localhost:8080/swagger-ui.html
```
to try out the API or see the documentation. 

Point your browser to
```bash
http://localhost:8080/docs/index.html
```
to read the RestDOCs API documentation. 
