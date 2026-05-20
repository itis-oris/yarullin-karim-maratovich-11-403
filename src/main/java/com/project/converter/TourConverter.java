package com.project.converter;

import com.project.dto.ScheduledExcursionDTO;
import com.project.dto.TourDTO;
import com.project.entity.Tour;
import com.project.entity.TourExcursion;
import com.project.entity.User;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TourConverter {
  private final ExcursionConverter excursionConverter;

  public TourDTO convert(Tour tour) {
    TourDTO dto = new TourDTO();
    dto.setId(tour.getId());
    dto.setTitle(tour.getTitle());
    dto.setDescription(tour.getDescription());
    dto.setStartDate(tour.getStartDate());
    dto.setEndDate(tour.getEndDate());
    dto.setStatus(tour.getStatus());
    dto.setBasePrice(tour.getBasePrice());

    BigDecimal basePrice = tour.getBasePrice() == null ? BigDecimal.ZERO : tour.getBasePrice();
    BigDecimal excursionTotalPrice = tour.calculateTotalExcursionPrice();
    BigDecimal minimumHotelPrice = BigDecimal.ZERO;
    dto.setExcursionTotalPrice(excursionTotalPrice);

    if (tour.getHotel() != null) {
      dto.setHotelId(tour.getHotel().getId());
      dto.setHotelName(tour.getHotel().getName());
      dto.setHotelCity(tour.getHotel().getCity());
      dto.setHotelStars(tour.getHotel().getStars());
      dto.setHotelEconomyPricePerNight(tour.getHotel().getEconomyPricePerNight());
      dto.setHotelStandardPricePerNight(tour.getHotel().getStandardPricePerNight());
      dto.setHotelLuxuryPricePerNight(tour.getHotel().getLuxuryPricePerNight());
      minimumHotelPrice =
          tour.getHotel()
              .getMinimumPricePerNight()
              .multiply(BigDecimal.valueOf(calculateTourNights(tour)));
    }
    dto.setMinimumHotelPrice(minimumHotelPrice);
    dto.setTotalPrice(basePrice.add(excursionTotalPrice).add(minimumHotelPrice));

    dto.setScheduledExcursions(
        tour.getTourExcursions().stream()
            .sorted(Comparator.comparing(TourExcursion::getStartTime))
            .map(this::convertScheduledExcursion)
            .toList());
    dto.setExcursions(
        dto.getScheduledExcursions().stream().map(ScheduledExcursionDTO::getExcursion).toList());
    return dto;
  }

  public Tour convertBack(TourDTO dto, User user) {
    return Tour.builder()
        .id(dto.getId())
        .title(dto.getTitle())
        .description(dto.getDescription())
        .startDate(dto.getStartDate())
        .endDate(dto.getEndDate())
        .basePrice(dto.getBasePrice())
        .status(dto.getStatus() == null ? Tour.TourStatus.ACTIVE : dto.getStatus())
        .createdBy(user)
        .build();
  }

  private long calculateTourNights(Tour tour) {
    if (tour.getStartDate() == null
        || tour.getEndDate() == null
        || !tour.getEndDate().isAfter(tour.getStartDate())) {
      return 1;
    }
    return Math.max(1, ChronoUnit.DAYS.between(tour.getStartDate(), tour.getEndDate()));
  }

  private ScheduledExcursionDTO convertScheduledExcursion(TourExcursion tourExcursion) {
    ScheduledExcursionDTO dto = new ScheduledExcursionDTO();
    dto.setExcursion(excursionConverter.convert(tourExcursion.getExcursion()));
    dto.setStartTime(tourExcursion.getStartTime());
    dto.setEndTime(tourExcursion.getEndTime());
    return dto;
  }
}
