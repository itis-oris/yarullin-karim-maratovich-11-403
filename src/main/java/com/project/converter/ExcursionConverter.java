package com.project.converter;

import com.project.dto.ExcursionDTO;
import com.project.entity.Excursion;
import com.project.entity.User;
import org.springframework.stereotype.Component;

@Component
public class ExcursionConverter {
  public ExcursionDTO convert(Excursion e) {
    ExcursionDTO dto = new ExcursionDTO();
    dto.setId(e.getId());
    dto.setTitle(e.getTitle());
    dto.setDescription(e.getDescription());
    dto.setDurationMinutes(e.getDurationMinutes());
    dto.setPrice(e.getPrice());
    return dto;
  }

  public Excursion convertBack(ExcursionDTO dto, User user) {
    return Excursion.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .durationMinutes(dto.getDurationMinutes())
        .price(dto.getPrice())
        .createdBy(user)
        .build();
  }
}
