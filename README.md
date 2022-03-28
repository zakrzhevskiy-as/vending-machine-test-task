Vending Machine
===============
[![GitHub](https://img.shields.io/github/license/defaultuser1000/vending-machine-test-task?label=license)](https://github.com/defaultuser1000/vending-machine-test-task/blob/master/LICENSE.md)
[![Heroku](https://pyheroku-badge.herokuapp.com/?app=vending-machine-prod&path=/vending-machine/login)](https://vending-machine-prod.herokuapp.com/vending-machine)

The "Vending Machine" is a web application designed to test candidates for the position
of a manual and/or automated testing specialist.

## Tech stack

* Spring Boot 2.6.1
    * Spring Data JPA
    * Spring Security
    * Spring Web
    * Spring Batch
    * Spring Boot Test
* Swagger 3.0.0
* React 17.0.2
* Ant Design 4.17.2
* Liquibase 4.8.0
* PostgreSQL 42.3.1
* Allure 2.13.0

## Requirements

* JDK 1.8.0_251 or above
* Maven 3.6.1 or above
* PostgreSQL 11.4

## Test project

To verify that application works as expected run following maven command:<br>
`mvn clean test -Dspring.profiles.active=test`

After test run executed you can build Allure report with maven command:<br>
`mvn allure:serve`

## Build and Run

In order to build and run the application you should follow next steps (or at your convenience 🙃)
1. Build using Maven<br>
   mvn clean package -DskipTests
2. Set environment variables<br>
    * TEST_TASK_DB_URL - _spring.datasource.url_ property value (**required**)
    * TEST_TASK_DB_USER - _spring.datasource.username_ property value (**required**)
    * TEST_TASK_DB_PASSWORD - _spring.datasource.password_ property value (**required**)
3. Run the built jar application<br>
   java -jar test-task-<version>.jar 
   
License
---
Code and documentation are available according to the MIT License (see [LICENSE](https://github.com/defaultuser1000/vending-machine-test-task/blob/master/LICENSE.md)).