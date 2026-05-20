package com.project.repository.impl;

import com.project.entity.Tour;
import com.project.repository.TourRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TourRepositoryImpl implements TourRepositoryCustom {

  private final EntityManager entityManager;

  @Override
  public List<Tour> findToursByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Tour> cq = cb.createQuery(Tour.class);
    Root<Tour> tour = cq.from(Tour.class);

    Predicate statusActive = cb.equal(tour.get("status"), Tour.TourStatus.ACTIVE);
    Predicate priceRange = cb.between(tour.get("basePrice"), minPrice, maxPrice);

    cq.select(tour).where(cb.and(statusActive, priceRange));
    cq.orderBy(cb.asc(tour.get("basePrice")));

    return entityManager.createQuery(cq).getResultList();
  }
}
