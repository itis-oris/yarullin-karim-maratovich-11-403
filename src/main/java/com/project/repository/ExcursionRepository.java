package com.project.repository;

import com.project.entity.Excursion;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcursionRepository extends JpaRepository<Excursion, Long> {

  List<Excursion> findByCreatedBy_Id(Long userId);

  @Query(
      "SELECT e FROM Excursion e WHERE e.price BETWEEN :minPrice AND :maxPrice ORDER BY e.price ASC")
  List<Excursion> findByPriceRange(
      @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

  @Query("SELECT e FROM Excursion e WHERE LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  Page<Excursion> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

  @Query("SELECT e FROM Excursion e WHERE e.price < (SELECT AVG(e2.price) FROM Excursion e2)")
  List<Excursion> findBelowAveragePrice();

  boolean existsByTitleAndIdNot(String title, Long excludeId);
}
