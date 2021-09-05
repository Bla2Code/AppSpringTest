# App Spring Backend

## Технологический стек
* OpenJDK 11/Spring 5/Maven
* Spring Boot 2.3 services
* XSD-based classes generation (JAXB), Lombok
* PostgreSQL Pro as RDBMS; JPA/Hibernate+JOOQ, Liquibase migrations
* Клиент-серверное взаимодействие через REST


### Liquibase migrations guide
В локальном PostgreSQL имеем схему `app-spring-diff` в базе `appspring` доступную для записи от `appspring`.
После совершения правок сущностей запускаем `./mvnw exec:exec@createMigrationDiff`
В файле `src/main/resources/db/db_last_diff.xml` имеем разницу между предыдущей редакцией и текущей.
Выбираем то, что надо и добавляем в свой changeset.
