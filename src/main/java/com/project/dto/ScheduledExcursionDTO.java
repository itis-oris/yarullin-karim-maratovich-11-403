package com.project.dto;

import java.io.Serializable;
import java.time.LocalTime;
import lombok.Data;

@Data
public class ScheduledExcursionDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private ExcursionDTO excursion;
  private LocalTime startTime;
  private LocalTime endTime;
}
