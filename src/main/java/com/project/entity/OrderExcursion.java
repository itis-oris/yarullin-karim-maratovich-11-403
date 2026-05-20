package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(
    name = "order_excursions",
    indexes = {
      @Index(name = "idx_order_excursion_order", columnList = "order_id"),
      @Index(name = "idx_order_excursion_excursion", columnList = "excursion_id")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderExcursion implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "excursion_id", nullable = false)
  private Excursion excursion;

  @NotNull(message = "Price at booking is required")
  @Positive(message = "Price must be positive")
  @Column(name = "price_at_booking", nullable = false, precision = 10, scale = 2)
  private BigDecimal priceAtBooking;
}
