package ru.mindils.jb.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.EmployerInfo;

@Repository
public interface EmployerInfoRepository extends JpaRepository<EmployerInfo, Long> {}
