package com.project.repository;

import com.project.entity.Tour;
import java.math.BigDecimal;
import java.util.List;

public interface TourRepositoryCustom {
  List<Tour> findToursByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
}
