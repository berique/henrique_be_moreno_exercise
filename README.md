# Roles API

## Summary

This project is a Java SpringBoot microservice that spins up a REST API, called Roles API, to improve upon the
capabilities of the Users API and the Teams API.

# How to run

## Docker

**Requirements**

- docker
- docker-compose

The solution can be executed with Docker and docker-compose. Since the build process needs to download all Maven
dependencies it can take some time to start.

```shell
docker-compose up --build
```

This will execute tests, build the jar, build the docker image and start the container. The first execution will take
longer since all maven dependencies will be downloaded. But next executions will run significantly faster as
dependencies are downloaded in a separate layer, thus cached. The `--build` triggers the Docker image build if there is
any change in the source code.

## Build from source

**Requirements**

- maven
- java 11

To `build`, `test`, `package`, `report` and `run` execute the command:

```shell
mvn clean package verify spring-boot:run
```

To check the coverage open `target/site/jacoco/index.html`


# How to develop

## Spotless

To check for any code style issue

```shell
mvn spotless:check
```

To apply the fixes for code style issues

```shell
mvn spotless:apply
```

---

# Notes

* The tests must be completed because the Role has an inconstant use in Membership.
* Business rule validations should be at domains and controllers, just lightweight validations.
* Role is uncertain at Membership because sometimes it's required in the code and sometimes not at the tests.chi
* Apply Hexagonal Architecture or Clean Architecture.
* As a Software engineer
    * Should have more atomic commits and short live pull requests.
    * Should have talked with somebody from business (PO, APM, PM, ...)  to check the domains and discard what is necessary.
