package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "tours",
    indexes = {
      @Index(name = "idx_tour_status", columnList = "status"),
      @Index(name = "idx_tour_dates", columnList = "start_date, end_date")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Tour implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotBlank(message = "Tour title is required")
  @Size(min = 3, max = 255, message = "Title must be 3-255 characters")
  @Column(nullable = false, length = 255)
  private String title;

  @Size(max = 2000, message = "Description must not exceed 2000 characters")
  @Column(columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Start date is required")
  @Future(message = "Start date must be in the future")
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  @Future(message = "End date must be in the future")
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private TourStatus status;

  @Column(name = "base_price", precision = 10, scale = 2)
  private BigDecimal basePrice;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id")
  private Hotel hotel;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Builder.Default
  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<TourExcursion> tourExcursions = new HashSet<>();

  @Builder.Default
  @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Order> orders = new HashSet<>();

  public enum TourStatus {
    DRAFT,
    ACTIVE,
    COMPLETED,
    CANCELLED
  }

  public Set<Excursion> getExcursions() {
    if (this.tourExcursions == null) {
      return new HashSet<>();
    }
    return tourExcursions.stream().map(TourExcursion::getExcursion).collect(Collectors.toSet());
  }

  public BigDecimal calculateTotalExcursionPrice() {
    if (this.tourExcursions == null) {
      return BigDecimal.ZERO;
    }
    return tourExcursions.stream()
        .map(te -> te.getExcursion().getPrice())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
