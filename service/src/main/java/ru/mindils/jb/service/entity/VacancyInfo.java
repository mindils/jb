package ru.mindils.jb.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "vacancy_info")
public class VacancyInfo implements BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vacancy_id")
    private Vacancy vacancy;

    /**
     * Вероятность того, что вакансия подходит кандидату, по оценке AI.
     *
     * <p>Значение представляет собой десятичное число от 0 до 1, где:
     *
     * <ul>
     *   <li>0 означает абсолютное несоответствие требованиям вакансии;
     *   <li>1 означает полное соответствие требованиям вакансии.
     * </ul>
     *
     * Пример значения: 0.7532.
     */
    @Column(precision = 10, scale = 4)
    private BigDecimal aiApproved;

    private Boolean approved;

    @Enumerated(EnumType.STRING)
    private VacancyStatusEnum status;
}
