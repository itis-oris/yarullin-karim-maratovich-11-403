package com.project.dto;

import com.project.entity.Tour;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class TourDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long id;
  @NotBlank private String title;
  private String description;
  @NotNull @FutureOrPresent private LocalDate startDate;
  @NotNull @FutureOrPresent private LocalDate endDate;
  private BigDecimal basePrice;
  private BigDecimal excursionTotalPrice;
  private BigDecimal totalPrice;
  private Tour.TourStatus status;
  private Long hotelId;
  private String hotelName;
  private String hotelCity;
  private Integer hotelStars;
  private BigDecimal hotelEconomyPricePerNight;
  private BigDecimal hotelStandardPricePerNight;
  private BigDecimal hotelLuxuryPricePerNight;
  private BigDecimal minimumHotelPrice;
  private List<ExcursionDTO> excursions = new ArrayList<>();
  private List<ScheduledExcursionDTO> scheduledExcursions = new ArrayList<>();
}
