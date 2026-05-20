package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
    name = "orders",
    indexes = {
      @Index(name = "idx_order_user", columnList = "user_id"),
      @Index(name = "idx_order_status", columnList = "status")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tour_id")
  private Tour tour;

  @NotNull(message = "Total price is required")
  @Positive(message = "Total price must be positive")
  @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalPrice;

  @Column(name = "tour_extra_price", precision = 12, scale = 2)
  private BigDecimal tourExtraPrice;

  @Column(name = "excursions_price", precision = 12, scale = 2)
  private BigDecimal excursionsPrice;

  @Column(name = "hotel_price", precision = 12, scale = 2)
  private BigDecimal hotelPrice;

  @Column(name = "booking_start_date")
  private LocalDate bookingStartDate;

  @Column(name = "booking_end_date")
  private LocalDate bookingEndDate;

  @Column(name = "nights")
  private Integer nights;

  @Column(name = "guests")
  private Integer guests;

  @Enumerated(EnumType.STRING)
  @Column(name = "room_level", length = 20)
  private RoomLevel roomLevel;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<OrderExcursion> orderExcursions = new HashSet<>();

  public enum OrderStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
  }

  public enum RoomLevel {
    ECONOMY,
    STANDARD,
    LUXURY
  }

  public void addOrderExcursion(OrderExcursion orderExcursion) {
    orderExcursions.add(orderExcursion);
    orderExcursion.setOrder(this);
    this.excursionsPrice =
        orderExcursions.stream()
            .map(OrderExcursion::getPriceAtBooking)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    this.totalPrice =
        valueOrZero(tourExtraPrice).add(valueOrZero(hotelPrice)).add(valueOrZero(excursionsPrice));
  }

  public void removeOrderExcursion(OrderExcursion orderExcursion) {
    orderExcursions.remove(orderExcursion);
    orderExcursion.setOrder(null);
    this.excursionsPrice =
        orderExcursions.stream()
            .map(OrderExcursion::getPriceAtBooking)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    this.totalPrice =
        valueOrZero(tourExtraPrice).add(valueOrZero(hotelPrice)).add(valueOrZero(excursionsPrice));
  }

  private BigDecimal valueOrZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
