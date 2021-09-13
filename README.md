# App Spring Backend

### Технологический стек
* OpenJDK 11/Spring 5/Maven
* Spring Boot 2.3 services
* XSD-based classes generation (JAXB), Lombok
* PostgreSQL Pro as RDBMS; JPA/Hibernate+JOOQ, Liquibase migrations
* Клиент-серверное взаимодействие через REST

### Запуск приложения
В ./docker-data/app-spring запустите скрипт ./start-stack.sh по завершению работы скрипта доступно приложения по адресу http://localhost:8080.
Swagger - http://localhost:8080/api/swagger-ui/ . После авторизации скопируйте токен и добавьте вместе с Bearer в поле Authorize
