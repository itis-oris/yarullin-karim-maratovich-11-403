package com.project.service;

import com.project.converter.TourConverter;
import com.project.dto.TourDTO;
import com.project.entity.Tour;
import com.project.entity.User;
import com.project.exception.ResourceNotFoundException;
import com.project.exception.TourCreationException;
import com.project.repository.ExcursionRepository;
import com.project.repository.HotelRepository;
import com.project.repository.TourRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TourService {

  private final TourRepository tourRepository;
  private final ExcursionRepository excursionRepository;
  private final HotelRepository hotelRepository;
  private final TourConverter tourConverter;
  private final UserService userService;

  @Transactional(readOnly = true)
  public List<Tour> getAllActiveTours() {
    log.debug("Fetching all active tours from database");
    return tourRepository.findByStatus(Tour.TourStatus.ACTIVE);
  }

  @Transactional(readOnly = true)
  public Page<Tour> searchActiveTours(String keyword, Pageable pageable) {
    log.debug("Searching tours with keyword: {}", keyword);
    return tourRepository.searchActiveToursByTitle(keyword, pageable);
  }

  @Transactional(readOnly = true)
  public TourDTO getTourById(Long id) {
    log.debug("Fetching tour with id: {}", id);
    Tour tour =
        tourRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + id));
    return tourConverter.convert(tour);
  }

  @Transactional
  public TourDTO createTour(TourDTO dto, Long userId) {
    return createTour(dto, userId, null, List.of(), List.of());
  }

  @Transactional
  @CacheEvict(
      value = {"tours", "tour"},
      allEntries = true)
  public TourDTO createTour(
      TourDTO dto, Long userId, Long hotelId, List<Long> excursionIds, List<LocalTime> startTimes) {
    try {
      log.info("Creating new tour with title: {}", dto.getTitle());

      User createdBy =
          userService
              .findById(userId)
              .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

      if (tourRepository.existsByTitle(dto.getTitle())) {
        throw new TourCreationException("Tour with this title already exists");
      }

      Tour tour = tourConverter.convertBack(dto, createdBy);
      applyHotel(tour, hotelId);
      applyExcursions(tour, excursionIds, startTimes);
      Tour saved = tourRepository.save(tour);

      log.info("Tour created successfully with id: {}", saved.getId());
      return tourConverter.convert(saved);

    } catch (Exception ex) {
      log.error("Error creating tour: ", ex);
      throw new TourCreationException("Ошибка при создании тура: " + ex.getMessage(), ex);
    }
  }

  @Transactional
  public TourDTO updateTour(Long id, TourDTO dto, Long userId) {
    return updateTour(id, dto, userId, null, List.of(), List.of());
  }

  @Transactional
  @CacheEvict(
      value = {"tours", "tour"},
      allEntries = true)
  public TourDTO updateTour(
      Long id,
      TourDTO dto,
      Long userId,
      Long hotelId,
      List<Long> excursionIds,
      List<LocalTime> startTimes) {
    log.info("Updating tour with id: {}", id);

    Tour tour =
        tourRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + id));

    tour.setTitle(dto.getTitle());
    tour.setDescription(dto.getDescription());
    tour.setStartDate(dto.getStartDate());
    tour.setEndDate(dto.getEndDate());
    tour.setBasePrice(dto.getBasePrice());
    applyHotel(tour, hotelId);
    applyExcursions(tour, excursionIds, startTimes);

    Tour updated = tourRepository.save(tour);
    log.info("Tour updated successfully: {}", id);
    return tourConverter.convert(updated);
  }

  @Transactional
  @CacheEvict(
      value = {"tours", "tour"},
      allEntries = true)
  public void deleteTour(Long id, Long userId) {
    log.info("Deleting tour with id: {}", id);

    Tour tour =
        tourRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + id));

    tourRepository.cancelTour(id);
    log.info("Tour cancelled successfully: {}", id);
  }

  private void applyHotel(Tour tour, Long hotelId) {
    if (hotelId == null) {
      tour.setHotel(null);
      return;
    }
    tour.setHotel(
        hotelRepository
            .findById(hotelId)
            .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId)));
  }

  private void applyExcursions(Tour tour, List<Long> excursionIds, List<LocalTime> startTimes) {
    tour.getTourExcursions().clear();
    if (excursionIds == null || excursionIds.isEmpty()) {
      return;
    }

    for (int i = 0; i < excursionIds.size(); i++) {
      Long excursionId = excursionIds.get(i);
      if (excursionId == null) {
        continue;
      }
      LocalTime startTime = startTimes != null && i < startTimes.size() ? startTimes.get(i) : null;
      if (startTime == null) {
        throw new TourCreationException("Укажите время начала для каждой выбранной экскурсии");
      }

      var excursion =
          excursionRepository
              .findById(excursionId)
              .orElseThrow(
                  () -> new ResourceNotFoundException("Excursion not found: " + excursionId));
      var tourExcursion =
          com.project.entity.TourExcursion.builder()
              .tour(tour)
              .excursion(excursion)
              .startTime(startTime)
              .endTime(startTime.plusMinutes(excursion.getDurationMinutes()))
              .build();
      tour.getTourExcursions().add(tourExcursion);
    }
  }

  @Transactional(readOnly = true)
  public List<Tour> getToursWithAboveAverageExcursions() {
    log.debug("Fetching tours with above average excursions count");
    return tourRepository.findToursWithAboveAverageExcursions();
  }

  @Transactional(readOnly = true)
  public List<Tour> findToursByDateRange(LocalDate start, LocalDate end) {
    if (start == null && end == null) {
      return tourRepository.findByStatus(Tour.TourStatus.ACTIVE);
    }
    return tourRepository.findByStatus(Tour.TourStatus.ACTIVE).stream()
        .filter(tour -> start == null || !tour.getStartDate().isBefore(start))
        .filter(tour -> end == null || !tour.getEndDate().isAfter(end))
        .toList();
  }

  @Transactional(readOnly = true)
  public Optional<Tour> findById(Long id) {
    return tourRepository.findById(id);
  }
}
