package ru.mindils.jb.service.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mindils.jb.service.entity.Employer;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, String> {
    Slice<Employer> findAllByDetailed(boolean detailed, PageRequest pageable);
}
