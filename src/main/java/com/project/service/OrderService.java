package com.project.service;

import com.project.entity.Order;
import com.project.entity.Tour;
import com.project.entity.User;
import com.project.exception.BusinessException;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.OrderRepository;
import com.project.repository.TourRepository;
import com.project.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
  private final OrderRepository orderRepository;
  private final TourRepository tourRepository;
  private final UserRepository userRepository;

  @Transactional
  public Order addTourToCart(Long userId, Long tourId) {
    Tour tour =
        tourRepository
            .findById(tourId)
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + tourId));
    return addTourToCart(
        userId, tourId, tour.getStartDate(), tour.getEndDate(), 1, Order.RoomLevel.ECONOMY);
  }

  @Transactional
  public Order addTourToCart(
      Long userId,
      Long tourId,
      LocalDate bookingStartDate,
      LocalDate bookingEndDate,
      Integer guests,
      Order.RoomLevel roomLevel) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    Tour tour =
        tourRepository
            .findById(tourId)
            .orElseThrow(() -> new ResourceNotFoundException("Tour not found: " + tourId));

    LocalDate start = bookingStartDate == null ? tour.getStartDate() : bookingStartDate;
    LocalDate end = bookingEndDate == null ? tour.getEndDate() : bookingEndDate;
    validateBookingDates(tour, start, end);

    int safeGuests = guests == null ? 1 : guests;
    if (safeGuests < 1) {
      throw new BusinessException("Количество человек должно быть не меньше 1");
    }

    Order.RoomLevel safeRoomLevel = roomLevel == null ? Order.RoomLevel.ECONOMY : roomLevel;
    int nights = (int) Math.max(1, ChronoUnit.DAYS.between(start, end));

    BigDecimal tourExtraPrice = valueOrZero(tour.getBasePrice());
    BigDecimal excursionsPrice =
        tour.calculateTotalExcursionPrice().multiply(BigDecimal.valueOf(safeGuests));
    BigDecimal hotelPrice = calculateHotelPrice(tour, safeRoomLevel, nights, safeGuests);
    BigDecimal total = tourExtraPrice.add(excursionsPrice).add(hotelPrice);
    if (total.compareTo(BigDecimal.ZERO) <= 0) {
      total = BigDecimal.ONE;
    }

    Order order =
        Order.builder()
            .user(user)
            .tour(tour)
            .bookingStartDate(start)
            .bookingEndDate(end)
            .nights(nights)
            .guests(safeGuests)
            .roomLevel(safeRoomLevel)
            .tourExtraPrice(tourExtraPrice)
            .excursionsPrice(excursionsPrice)
            .hotelPrice(hotelPrice)
            .totalPrice(total)
            .status(Order.OrderStatus.PENDING)
            .build();
    return orderRepository.save(order);
  }

  @Transactional(readOnly = true)
  public List<Order> getCart(Long userId) {
    return orderRepository.findByUser_IdAndStatus(userId, Order.OrderStatus.PENDING);
  }

  @Transactional(readOnly = true)
  public Page<Order> getCart(Long userId, Pageable pageable) {
    return orderRepository.findByUser_IdAndStatus(userId, Order.OrderStatus.PENDING, pageable);
  }

  @Transactional(readOnly = true)
  public List<Order> getHistory(Long userId) {
    return orderRepository.findByUser_Id(userId).stream()
        .filter(order -> order.getStatus() != Order.OrderStatus.PENDING)
        .toList();
  }

  @Transactional(readOnly = true)
  public Page<Order> getHistory(Long userId, Pageable pageable) {
    return orderRepository.findByUser_IdAndStatusNot(userId, Order.OrderStatus.PENDING, pageable);
  }

  @Transactional
  public void checkout(Long userId) {
    getCart(userId)
        .forEach(
            order -> {
              order.setStatus(Order.OrderStatus.CONFIRMED);
              orderRepository.save(order);
            });
  }

  @Transactional
  public void removeFromCart(Long userId, Long orderId) {
    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    if (!order.getUser().getId().equals(userId) || order.getStatus() != Order.OrderStatus.PENDING) {
      throw new ResourceNotFoundException("Order not found: " + orderId);
    }
    orderRepository.delete(order);
  }

  private void validateBookingDates(Tour tour, LocalDate start, LocalDate end) {
    if (start == null || end == null) {
      throw new BusinessException("Укажите даты бронирования");
    }
    if (!end.isAfter(start)) {
      throw new BusinessException("Дата окончания должна быть позже даты начала");
    }
    if (tour.getStartDate() != null && start.isBefore(tour.getStartDate())) {
      throw new BusinessException("Дата начала бронирования не может быть раньше даты начала тура");
    }
    if (tour.getEndDate() != null && end.isAfter(tour.getEndDate())) {
      throw new BusinessException(
          "Дата окончания бронирования не может быть позже даты окончания тура");
    }
  }

  private BigDecimal calculateHotelPrice(
      Tour tour, Order.RoomLevel roomLevel, int nights, int guests) {
    if (tour.getHotel() == null) {
      return BigDecimal.ZERO;
    }
    return tour.getHotel()
        .getPricePerNight(roomLevel)
        .multiply(BigDecimal.valueOf(nights))
        .multiply(BigDecimal.valueOf(guests));
  }

  private BigDecimal valueOrZero(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value;
  }
}
