package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "excursions",
    indexes = {@Index(name = "idx_excursion_price", columnList = "price")})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Excursion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotBlank(message = "Excursion title is required")
  @Size(min = 3, max = 255, message = "Title must be 3-255 characters")
  @Column(nullable = false, length = 255)
  private String title;

  @Size(max = 2000, message = "Description must not exceed 2000 characters")
  @Column(columnDefinition = "TEXT")
  private String description;

  @NotNull(message = "Duration is required")
  @Positive(message = "Duration must be positive")
  @Column(name = "duration_minutes", nullable = false)
  private Integer durationMinutes;

  @NotNull(message = "Price is required")
  @Positive(message = "Price must be positive")
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", nullable = false)
  private User createdBy;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<TourExcursion> tourExcursions = new HashSet<>();

  @OneToMany(mappedBy = "excursion", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OrderExcursion> orderExcursions = new HashSet<>();
}
