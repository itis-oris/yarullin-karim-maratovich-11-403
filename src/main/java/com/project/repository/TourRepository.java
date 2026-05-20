package com.project.repository;

import com.project.entity.Tour;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourRepository extends JpaRepository<Tour, Long>, TourRepositoryCustom {

  // Standard derived query
  List<Tour> findByStatus(Tour.TourStatus status);

  @Query(
      "SELECT t FROM Tour t WHERE t.startDate >= :startDate AND t.endDate <= :endDate AND t.status = 'ACTIVE'")
  List<Tour> findActiveToursWithinDateRange(
      @Param("startDate") LocalDate start, @Param("endDate") LocalDate end);

  @Query(
      "SELECT t FROM Tour t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND t.status = 'ACTIVE'")
  Page<Tour> searchActiveToursByTitle(@Param("keyword") String keyword, Pageable pageable);

  @Query(
      "SELECT t FROM Tour t WHERE SIZE(t.tourExcursions) > "
          + "(SELECT AVG(SIZE(t2.tourExcursions)) FROM Tour t2 WHERE t2.status = 'ACTIVE') "
          + "AND t.status = 'ACTIVE'")
  List<Tour> findToursWithAboveAverageExcursions();

  List<Tour> findByCreatedBy_Id(Long userId);

  boolean existsByTitle(String title);

  boolean existsByTitleAndIdNot(String title, Long excludeId);

  @Modifying
  @Query("UPDATE Tour t SET t.status = 'CANCELLED' WHERE t.id = :id")
  int cancelTour(@Param("id") Long id);
}
