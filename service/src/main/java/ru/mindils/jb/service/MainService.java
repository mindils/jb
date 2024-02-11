package ru.mindils.jb.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.common.UtilityClass;
import ru.mindils.jb.service.util.HibernateUtil;

public class MainService {

  public static void main(String[] args) {
    String original = "Hello, World!";
    String transformed = UtilityClass.transformString(original);
    System.out.println(transformed);

    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {
      session.beginTransaction();
      // Create a new object
      session.getTransaction().commit();
    }
  }

}