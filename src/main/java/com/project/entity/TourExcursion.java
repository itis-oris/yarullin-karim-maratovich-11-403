package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(
    name = "tour_excursions",
    uniqueConstraints = @UniqueConstraint(columnNames = {"tour_id", "excursion_id", "start_time"}),
    indexes = {
      @Index(name = "idx_tour_excursion_tour", columnList = "tour_id"),
      @Index(name = "idx_tour_excursion_excursion", columnList = "excursion_id")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TourExcursion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "tour_id", nullable = false)
  private Tour tour;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "excursion_id", nullable = false)
  private Excursion excursion;

  @NotNull(message = "Start time is required")
  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @NotNull(message = "End time is required")
  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  // Helper: рассчитать длительность
  public int getDurationMinutes() {
    return java.time.Duration.between(startTime, endTime).toMinutesPart();
  }
}
