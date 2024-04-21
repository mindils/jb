package ru.mindils.jb.service.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mindils.jb.service.dto.EmployerReadDto;
import ru.mindils.jb.service.dto.EmployerUpdateStatusDto;
import ru.mindils.jb.service.entity.Employer;
import ru.mindils.jb.service.entity.EmployerInfo;
import ru.mindils.jb.service.mapper.EmployerUpdateStatusMapper;
import ru.mindils.jb.service.repository.EmployerInfoRepository;
import ru.mindils.jb.service.repository.EmployerRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployerService {

  private final EmployerRepository employerRepository;
  private final EmployerInfoRepository employerInfoRepository;
  private final EmployerUpdateStatusMapper mapper;

  public Page<Employer> findAll(Pageable pageable) {
    return employerRepository.findAll(pageable);
  }

  public Employer findById(String id) {
    return employerRepository.findById(id).orElseThrow();
  }

  @Transactional
  public Optional<EmployerReadDto> updateStatus(String id, EmployerUpdateStatusDto dto) {
    return employerRepository
        .findById(id)
        .map(entity -> {
          if (entity.getEmployerInfo() == null) {
            var employerInfo =
                EmployerInfo.builder().employer(entity).status(dto.status()).build();
            employerInfoRepository.save(employerInfo);
            entity.setEmployerInfo(employerInfo);
          } else {
            entity.getEmployerInfo().setStatus(dto.status());
          }
          return entity;
        })
        .map(employerRepository::saveAndFlush)
        .map(mapper::map);
  }
}
