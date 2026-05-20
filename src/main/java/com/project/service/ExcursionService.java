package com.project.service;

import com.project.converter.ExcursionConverter;
import com.project.dto.ExcursionDTO;
import com.project.entity.Excursion;
import com.project.entity.User;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.ExcursionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExcursionService {
  private final ExcursionRepository repo;
  private final ExcursionConverter converter;
  private final UserService userService;

  public List<Excursion> findAll() {
    return repo.findAll();
  }

  public static String buildCacheKey(String keyword, Pageable p) {
    String normalizedKeyword = keyword == null ? "" : keyword;
    if (p == null || p.isUnpaged()) {
      return normalizedKeyword + "|unpaged";
    }
    return normalizedKeyword + "|" + p.getPageNumber() + "|" + p.getPageSize() + "|" + p.getSort();
  }

  public Page<Excursion> searchExcursions(String keyword, Pageable p) {
    Page<Excursion> page = repo.searchByTitle(keyword == null ? "" : keyword, p);
    if (page.getPageable().isUnpaged()) {
      Pageable serializablePageable = PageRequest.of(0, Math.max(page.getContent().size(), 1));
      return new PageImpl<>(page.getContent(), serializablePageable, page.getTotalElements());
    }
    return page;
  }

  public ExcursionDTO getExcursionById(Long id) {
    return converter.convert(
        repo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Excursion not found: " + id)));
  }

  @CacheEvict(value = "excursions", allEntries = true)
  public ExcursionDTO createExcursion(ExcursionDTO dto, Long userId) {
    User u =
        userService
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return converter.convert(repo.save(converter.convertBack(dto, u)));
  }

  @CacheEvict(value = "excursions", allEntries = true)
  public ExcursionDTO updateExcursion(Long id, ExcursionDTO dto, Long userId) {
    Excursion e =
        repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Excursion not found"));
    e.setTitle(dto.getTitle());
    e.setDescription(dto.getDescription());
    e.setDurationMinutes(dto.getDurationMinutes());
    e.setPrice(dto.getPrice());
    return converter.convert(repo.save(e));
  }

  @CacheEvict(value = "excursions", allEntries = true)
  public void deleteExcursion(Long id, Long userId) {
    repo.deleteById(id);
  }
}
