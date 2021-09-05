FROM maven:3.5-jdk-11-slim AS MAVEN_TOOL_CHAIN
WORKDIR /tmp/
COPY pom.xml /tmp/
RUN mvn dependency:go-offline
COPY src /tmp/src/
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim AS base-runtime
COPY --from=MAVEN_TOOL_CHAIN /tmp/target/app-spring-backend-0.0.1-SNAPSHOT.jar /tmp/app.jar
ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]
