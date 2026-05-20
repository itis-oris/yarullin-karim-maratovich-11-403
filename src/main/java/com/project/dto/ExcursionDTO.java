package com.project.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ExcursionDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long id;
  @NotBlank private String title;
  private String description;
  @NotNull @Positive private Integer durationMinutes;
  @NotNull @Positive private BigDecimal price;
}
