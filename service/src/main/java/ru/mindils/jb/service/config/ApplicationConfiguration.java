package ru.mindils.jb.service.config;

import jakarta.persistence.EntityManager;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerInfo;
import ru.mindils.jb.service.entity.User;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.entity.VacancyFilterParams;
import ru.mindils.jb.service.entity.VacancyInfo;

@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "ru.mindils.jb")
public class ApplicationConfiguration {

    @Bean(destroyMethod = "close")
    public SessionFactory sessionFactory(StandardServiceRegistry registry) {
        MetadataSources sources =
                new MetadataSources(registry)
                        .addAnnotatedClass(Employer.class)
                        .addAnnotatedClass(EmployerInfo.class)
                        .addAnnotatedClass(User.class)
                        .addAnnotatedClass(Vacancy.class)
                        .addAnnotatedClass(VacancyFilter.class)
                        .addAnnotatedClass(VacancyFilterParams.class)
                        .addAnnotatedClass(VacancyInfo.class);

        Metadata metadata =
                sources.getMetadataBuilder()
                        .applyPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy())
                        .build();

        return metadata.getSessionFactoryBuilder().build();
    }

    @Bean
    @Profile("!test")
    private StandardServiceRegistry getStandardServiceRegistry(
            @Value("${spring.datasource.driver-class-name}") String driver,
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.jpa.show-sql}") String showSql,
            @Value("${spring.jpa.properties.hibernate.format_sql}") String formatSql,
            @Value("${spring.jpa.properties.hibernate.current_session_context_class}")
                    String sessionContextClass) {
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

        // Настройки подключения к БД
        Map<String, Object> settings = new HashMap<>();
        settings.put(AvailableSettings.JAKARTA_JDBC_DRIVER, driver);
        settings.put(AvailableSettings.JAKARTA_JDBC_URL, url);
        settings.put(AvailableSettings.JAKARTA_JDBC_USER, user);
        settings.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, password);

        settings.put(AvailableSettings.SHOW_SQL, showSql);
        settings.put(AvailableSettings.FORMAT_SQL, formatSql);
        settings.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, sessionContextClass);

        registryBuilder.applySettings(settings);
        return registryBuilder.build();
    }

    @Bean
    public EntityManager entityManager(SessionFactory sessionFactory) {
        return (EntityManager)
                Proxy.newProxyInstance(
                        SessionFactory.class.getClassLoader(),
                        new Class[] {EntityManager.class},
                        (proxy, method, args) ->
                                method.invoke(sessionFactory.getCurrentSession(), args));
    }
}
