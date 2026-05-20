package com.project.repository;

import com.project.entity.Order;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findByUser_Id(Long userId);

  List<Order> findByUser_IdAndStatus(Long userId, Order.OrderStatus status);

  Page<Order> findByUser_IdAndStatus(Long userId, Order.OrderStatus status, Pageable pageable);

  Page<Order> findByUser_IdAndStatusNot(Long userId, Order.OrderStatus status, Pageable pageable);

  List<Order> findByTour_Id(Long tourId);

  @Query("SELECT o FROM Order o WHERE o.totalPrice > :minAmount ORDER BY o.totalPrice DESC")
  List<Order> findHighValueOrders(@Param("minAmount") BigDecimal minAmount);

  @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
  BigDecimal sumByStatus(@Param("status") Order.OrderStatus status);

  @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
  long countByUserId(@Param("userId") Long userId);
}
