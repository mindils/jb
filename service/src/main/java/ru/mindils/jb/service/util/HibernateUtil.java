package ru.mindils.jb.service.util;

import org.hibernate.cfg.Configuration;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;

@UtilityClass
public class HibernateUtil {

  public static SessionFactory buildSessionFactory() {
    Configuration configuration = buildConfiguration();
    configuration.configure();

    return configuration.buildSessionFactory();
  }

  public static Configuration buildConfiguration() {
    Configuration configuration = new Configuration();
    configuration.setPhysicalNamingStrategy(new CamelCaseToUnderscoresNamingStrategy());
    return configuration;
  }

}
