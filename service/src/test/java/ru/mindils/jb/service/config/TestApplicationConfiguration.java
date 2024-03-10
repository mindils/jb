package ru.mindils.jb.service.config;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
@Profile("test")
public class TestApplicationConfiguration {

    @Bean
    @Profile("test")
    public StandardServiceRegistry getStandardServiceRegistry() {
        PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.2");

        postgres.start();

        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        // Настройки подключения к БД
        Map<String, Object> settings = new HashMap<>();
        settings.put(AvailableSettings.JAKARTA_JDBC_DRIVER, "org.postgresql.Driver");
        settings.put(AvailableSettings.JAKARTA_JDBC_URL, postgres.getJdbcUrl());
        settings.put(AvailableSettings.JAKARTA_JDBC_USER, postgres.getUsername());
        settings.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, postgres.getPassword());

        settings.put(AvailableSettings.SHOW_SQL, "true");
        settings.put(AvailableSettings.FORMAT_SQL, "true");
        settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(AvailableSettings.HBM2DDL_AUTO, "create");

        registryBuilder.applySettings(settings);
        return registryBuilder.build();
    }
}
