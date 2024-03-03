package ru.mindils.jb.sync.service;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.service.entity.VacancyFilter;
import ru.mindils.jb.service.util.HibernateUtil;

public class VacancyFilterService {

  private static final String VACANCY_FILTER_DEFAULT = "DEFAULT";

  @Getter
  private static final VacancyFilterService instance = new VacancyFilterService();

  private VacancyFilterService() {
  }

  public List<Map<String, String>> getDefaultFilter() {
    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {
      VacancyFilter vacancyFilter = session
          .createQuery("from VacancyFilter where code = :filter", VacancyFilter.class)
          .setParameter("filter", VACANCY_FILTER_DEFAULT)
          .uniqueResult();

      return vacancyFilter.getParams()
          .stream()
          .map(param -> Map.of(param.getParamName(), param.getParamValue()))
          .toList();
    }
  }


}
