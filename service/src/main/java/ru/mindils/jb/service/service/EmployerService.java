package ru.mindils.jb.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.repository.EmployerRepository;

@Service
@RequiredArgsConstructor
public class EmployerService {

  private final EmployerRepository employerRepository;

  public Page<Employer> findAll(Pageable pageable) {
    return employerRepository.findAll(pageable);
  }

  public Employer findById(String id) {
    return employerRepository.findById(id).orElseThrow();
  }
}
