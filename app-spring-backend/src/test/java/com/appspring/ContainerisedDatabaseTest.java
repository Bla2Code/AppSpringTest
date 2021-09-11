package com.appspring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import com.appspring.util.PostgresqlContainer;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class ContainerisedDatabaseTest {

    public static PostgreSQLContainer<?> postgreSQLContainer = PostgresqlContainer.getInstance();
    public static AtomicInteger usages = new AtomicInteger();

    @BeforeAll
    synchronized static void setup() {
        log.info("Try run PostgreSQL test container. Usages: " + usages.getAndIncrement());
        if (!postgreSQLContainer.isRunning()) {
            postgreSQLContainer.start();
        }
    }

    @AfterAll
    synchronized static void done() {
        if (usages.decrementAndGet() < 1) {
            log.info("Try stop PostgreSQL test container.");
            postgreSQLContainer.stop();
        } else {
            log.info("PostgreSQL test container will be stopped with another test class. Now usages: " + usages.get());
        }
    }

}
