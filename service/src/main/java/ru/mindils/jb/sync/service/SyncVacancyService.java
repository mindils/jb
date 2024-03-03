package ru.mindils.jb.sync.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.sync.dto.BriefVacancyDto;
import ru.mindils.jb.sync.dto.DetailedEmployerDto;
import ru.mindils.jb.sync.dto.VacancyListResponseDto;
import ru.mindils.jb.sync.dto.DetailedVacancyDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.sync.mapper.EmployerMapper;
import ru.mindils.jb.sync.mapper.VacancyMapper;
import ru.mindils.jb.service.util.HibernateUtil;

public class SyncVacancyService {

  private static final String DEFAULT_FILTER_PERIOD = "30";
  private static final String DEFAULT_ITEMS_PER_PAGE = "100";
  private static final SyncVacancyService INSTANCE = new SyncVacancyService();
  private final VacancyClientService vacancyApiClientService = VacancyClientService.getInstance();
  private final VacancyFilterService vacancyFilterService = VacancyFilterService.getInstance();
  private final VacancyMapper vacancyMapper = VacancyMapper.INSTANCE;
  private final EmployerMapper employerMapper = EmployerMapper.INSTANCE;

  private SyncVacancyService() {
  }

  public static SyncVacancyService getInstance() {
    return INSTANCE;
  }

  @SneakyThrows
  public void syncEmployerDetailsAll() {
    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {

      // вакансий может быть много, поэтому заполняем пачками
      while (true) {
        List<Employer> employers = session.createQuery(
                "select e from Employer e where e.detailed = false", Employer.class)
            .setFirstResult(0)
            .setMaxResults(10)
            .getResultList();

        if (employers.isEmpty()) {
          break;
        }

        employers.forEach(employer -> {
          syncEmployerById(employer.getId());
        });

        // спим, чтобы не нагружать hh
        // TODO: попробовать переделать на scheduler когда добавим spring
        //  employers.forEach(employer -> {
        //    scheduler.schedule(() -> syncEmployerById(employer), 1, TimeUnit.SECONDS);
        //  });
        Thread.sleep(1000);
      }

    }
  }

  @SneakyThrows
  public void syncVacancyDetailsAll() {

    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {

      while (true) {
        List<Vacancy> vacancies = session.createQuery(
                "select e from Vacancy e where e.detailed = false", Vacancy.class)
            .setFirstResult(0)
            .setMaxResults(100)
            .getResultList();

        if (vacancies.isEmpty()) {
          break;
        }

        vacancies.forEach(vacancy -> {
          syncVacancyById(vacancy.getId());
        });

        // спим, чтобы не нагружать hh
        // TODO: попробовать переделать на scheduler когда добавим spring
        //  vacancies.forEach(vacancy -> {
        //    scheduler.schedule(() -> syncVacancyById(vacancy), 1, TimeUnit.SECONDS);
        //  });
        Thread.sleep(1000);
      }

    }

  }

  public void syncVacancyByDefaultFilterAll() {
    syncVacancyByDefaultFilterAll(DEFAULT_FILTER_PERIOD);
  }

  @SneakyThrows
  public void syncVacancyByDefaultFilterAll(String period) {
    List<Map<String, String>> defaultFilter = vacancyFilterService.getDefaultFilter();

    int page = 0;

    VacancyListResponseDto vacancyListResponseDto;

    do {
      List<Map<String, String>> updatedFilter = new ArrayList<>(defaultFilter);

      updatedFilter.add(Map.of("period", period));
      updatedFilter.add(Map.of("per_page", DEFAULT_ITEMS_PER_PAGE));
      updatedFilter.add(Map.of("page", String.valueOf(page)));

      // задержка, чтобы не грузить hh.ru
      Thread.sleep(1000);

      vacancyListResponseDto = syncVacancyFilter(updatedFilter);
      page = vacancyListResponseDto.getPage() + 1;

    } while (vacancyListResponseDto.getPage() < vacancyListResponseDto.getPages() - 1);
  }

  /**
   * TODO: переписать, тут надо чтобы мы получали вакансии где нет рейтинга и обновляли его
   *   в данном случае может быть не создана запись в vacancy_info и мы не обновим рейтинг
   */
  public void syncVacancyAiRatingsAll() {
    try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        Session session = sessionFactory.openSession()) {

      while (true) {
        List<VacancyInfo> vacancies = session.createQuery(
                "select e from VacancyInfo e join e.vacancy where e.aiApproved is null",
                VacancyInfo.class)
            .setFirstResult(0)
            .setMaxResults(100)
            .getResultList();

        if (vacancies.isEmpty()) {
          break;
        }

        // тут можно без сна, т.к. наш сервер )
        vacancies.forEach(this::syncVacancyAiRating);
      }
    }
  }

  public void syncEmployerById(String id) {
    DetailedEmployerDto objects = vacancyApiClientService.loadEmployerById(id);

    SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    Session session = sessionFactory.openSession();

    try {
      session.beginTransaction();

      // Workaround:
      // на hh почему-то есть компании c id null, сохраняем в бд под id = 0
      // при получении деталей этой компании по id = 0 возникает ошибка
      // тут проверяем если мне hh вернул id = null просто помечаем, что детали загружены
      if (objects.getId() == null) {
        Employer employer = session.get(Employer.class, "0");
        employer.setDetailed(true);
        session.merge(employer);
      } else {
        Employer vacancy = session.get(Employer.class, id);
        Employer employerMap = session.merge(employerMapper.map(objects, vacancy));
        session.merge(employerMap);
      }

      session.getTransaction().commit();
    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
      sessionFactory.close();
    }
  }


  public void syncVacancyById(String id) {
    DetailedVacancyDto vacancyDto = vacancyApiClientService.loadVacancyById(id);

    SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    Session session = sessionFactory.openSession();

    try {
      session.beginTransaction();
      Vacancy vacancy = session.get(Vacancy.class, id);

      // Workaround:
      // по некоторым вакансиям возвращает 404
      // просто помечаем, что загружены детали
      if (vacancyDto.getId() == null) {
        vacancy.setDetailed(true);
        session.merge(vacancy);
      } else {
        Vacancy mapVacancy = session.merge(vacancyMapper.map(vacancyDto, vacancy));

        // TODO: тут если работодатель уже загружен с деталями то не нужно его обновлять
        //  затирается поле detailed
        session.merge(mapVacancy.getEmployer());
        session.merge(mapVacancy);

      }
      session.getTransaction().commit();

    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
      sessionFactory.close();
    }
  }

  public void syncVacancyAiRating(VacancyInfo vacancyInfo) {
    String sb = vacancyInfo.getVacancy().getName()
        + " "
        + vacancyInfo.getVacancy().getKeySkills();
    String ratingAi = vacancyApiClientService.loadAIRatingByText(sb);

    SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    Session session = sessionFactory.openSession();

    try {
      session.beginTransaction();
      vacancyInfo.setAiApproved(new BigDecimal(ratingAi));

      session.merge(vacancyInfo);

      session.getTransaction().commit();

    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
      sessionFactory.close();
    }
  }

  private VacancyListResponseDto syncVacancyFilter(List<Map<String, String>> defaultFilter) {
    VacancyListResponseDto vacancyListResponseDto = vacancyApiClientService.loadVacancies(
        defaultFilter);

    List<String> vacancyIds = vacancyListResponseDto.getItems()
        .stream()
        .map(BriefVacancyDto::getId)
        .toList();

    SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
    Session session = sessionFactory.openSession();

    try {
      session.beginTransaction();

      // Получаем все которые ранее были загружены, чтобы обновить
      Map<String, Vacancy> vacancyMaps = session.createQuery(
              "select e from Vacancy e where e.id in :ids",
              Vacancy.class)
          .setParameter("ids", vacancyIds)
          .getResultList()
          .stream()
          .collect(Collectors.toMap(Vacancy::getId, Function.identity()));

      vacancyListResponseDto.getItems()
          .forEach(vacancyDto -> {
            Vacancy vacancy;
            if (vacancyMaps.containsKey(vacancyDto.getId())) {
              vacancy = vacancyMapper.map(vacancyDto, vacancyMaps.get(vacancyDto.getId()));
            } else {
              vacancy = vacancyMapper.map(vacancyDto);
            }

            // TODO: тут если работодатель уже загружен с деталями то не нужно его обновлять
            session.merge(vacancy.getEmployer());
            session.merge(vacancy);
          });

      session.getTransaction().commit();
      return vacancyListResponseDto;
    } catch (Exception e) {
      session.getTransaction().rollback();
      throw e;
    } finally {
      session.close();
      sessionFactory.close();
    }
  }


}
