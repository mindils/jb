package ru.mindils.jb;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.service.util.HibernateUtil;

public class MainService {

    public static void main(String[] args) {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
                Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            // Create a new object
            session.getTransaction().commit();
        }
    }
}
