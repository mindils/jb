package ru.mindils.jb.service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
@Table(name = "jb_salary")
public class Salary {

    @Column(name = "salary_gross")
    private Boolean gross;

    @Column(name = "salary_from")
    private Integer from;

    @Column(name = "salary_to")
    private Integer to;

    @Column(name = "salary_currency")
    private String currency;
}
