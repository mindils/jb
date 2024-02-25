package ru.mindils.jb.service.util;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.Salary;
import ru.mindils.jb.service.entity.Vacancy;
import ru.mindils.jb.service.entity.VacancyInfo;
import ru.mindils.jb.service.entity.VacancyStatusEnum;

@UtilityClass
public class TestDataImporter {

  private static final String[] CITIES = {"Москва", "Санкт-Петербург", "Новосибирск",
      "Екатеринбург", "Казань"};
  private static final String[] COMPANIES = {"ООО Рога и Копыта", "ЗАО Инновации",
      "ОАО СтройМонтаж", "ИП Петров", "ТОО Разработка"};
  private static int employerCounter = 0;
  private static int vacancyCounter = 0;

  public void importData(SessionFactory sessionFactory) {
    @Cleanup Session session = sessionFactory.openSession();
    session.beginTransaction();

    List<Employer> employers = generateEmployers();
    List<Vacancy> vacancies = new ArrayList<>();

    // 3 новые вакансии с aiApproved > 0.7
    vacancies.addAll(
        generateVacancies(employers, VacancyStatusEnum.NEW, true, 3)
    );

    // 1 одобренная вакансия с aiApproved > 0.7
    vacancies.addAll(
        generateVacancies(employers, VacancyStatusEnum.APPROVED, true, 1)
    );

    // 1 отклоненная вакансия с aiApproved > 0.7
    vacancies.addAll(
        generateVacancies(employers, VacancyStatusEnum.DECLINED, true, 1)
    );

    // 1 новая вакансия с aiApproved < 0.7
    vacancies.addAll(
        generateVacancies(employers, VacancyStatusEnum.NEW, false, 1)
    );

    // 1 новая вакансия без зарплаты с aiApproved < 0.7
    vacancies.addAll(
        generateVacancies(employers, VacancyStatusEnum.NEW, false, 1, true)
    );

    // 1 отклоненная вакансия без зарплаты с aiApproved > 0.7
    vacancies.addAll(generateVacancies(
        employers, VacancyStatusEnum.DECLINED, false, 1, true)
    );

    employers.forEach(session::persist);
    vacancies.forEach(session::persist);

    session.getTransaction().commit();
  }

  private static List<Employer> generateEmployers() {
    List<Employer> employers = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      Employer employer = Employer.builder()
          .id(UUID.randomUUID().toString())
          .name(COMPANIES[i])
          .trusted(i % 2 == 0)
          .description("Описание работодателя " + i)
          .detailed(true)
          .createdAt(Instant.now())
          .build();
      employers.add(employer);
    }
    return employers;
  }

  private static List<Vacancy> generateVacancies(List<Employer> employers, VacancyStatusEnum status,
      boolean highAiApproved, int count) {
    return generateVacancies(employers, status, highAiApproved, count, false);
  }

  private static List<Vacancy> generateVacancies(List<Employer> employers, VacancyStatusEnum status,
      boolean highAiApproved, int count, boolean noSalary) {
    List<Vacancy> vacancies = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      Employer employer = employers.get(employerCounter % employers.size());
      employerCounter++;
      BigDecimal aiApproved =
          highAiApproved ? BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.71, 1.0))
              : BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 0.69));
      Salary salary = noSalary ? null : Salary.builder()
          .from(ThreadLocalRandom.current().nextInt(101000, 299999))
          .to(ThreadLocalRandom.current().nextInt(101000, 299999))
          .currency("RUR")
          .gross(true)
          .build();

      Vacancy vacancy = Vacancy.builder()
          .id(UUID.randomUUID().toString())
          .name("Разработчик Java #" + vacancyCounter)
          .employer(employer)
          .premium(ThreadLocalRandom.current().nextBoolean())
          .city(CITIES[vacancyCounter % CITIES.length])
          .salary(salary)
          .type("open")
          .publishedAt(Instant.now())
          .createdAt(Instant.now())
          .archived(false)
          .applyAlternateUrl("https://hh.ru")
          .url("https://hh.ru")
          .alternateUrl("https://hh.ru")
          .schedule("fullDay")
          .responseUrl("https://hh.ru")
          .professionalRoles(List.of(Map.of("id", "1", "name", "Java Developer")))
          .employment("full")
          .description("Описание вакансии #" + vacancyCounter)
          .build();
      VacancyInfo vacancyInfo = VacancyInfo.builder()
          .vacancy(vacancy)
          .aiApproved(aiApproved)
          .status(status)
          .build();
      vacancy.setVacancyInfo(vacancyInfo);
      vacancies.add(vacancy);
      vacancyCounter++;
    }
    return vacancies;
  }
}
