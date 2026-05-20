package com.project.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.*;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false)
  private String name;

  private String city;
  private Integer stars;
  private String description;

  @PositiveOrZero
  @Column(name = "economy_price_per_night", precision = 10, scale = 2)
  private BigDecimal economyPricePerNight;

  @PositiveOrZero
  @Column(name = "standard_price_per_night", precision = 10, scale = 2)
  private BigDecimal standardPricePerNight;

  @PositiveOrZero
  @Column(name = "luxury_price_per_night", precision = 10, scale = 2)
  private BigDecimal luxuryPricePerNight;

  public BigDecimal getPricePerNight(Order.RoomLevel roomLevel) {
    RoomLevelSafe safeLevel = RoomLevelSafe.from(roomLevel);
    return switch (safeLevel) {
      case STANDARD ->
          standardPricePerNight == null ? getMinimumPricePerNight() : standardPricePerNight;
      case LUXURY ->
          luxuryPricePerNight == null
              ? (standardPricePerNight == null ? getMinimumPricePerNight() : standardPricePerNight)
              : luxuryPricePerNight;
      case ECONOMY -> getMinimumPricePerNight();
    };
  }

  public BigDecimal getMinimumPricePerNight() {
    return valueOrZero(economyPricePerNight);
  }

  private BigDecimal valueOrZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }

  private enum RoomLevelSafe {
    ECONOMY,
    STANDARD,
    LUXURY;

    static RoomLevelSafe from(Order.RoomLevel roomLevel) {
      if (roomLevel == null) {
        return ECONOMY;
      }
      return RoomLevelSafe.valueOf(roomLevel.name());
    }
  }
}
