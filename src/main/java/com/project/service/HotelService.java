package com.project.service;

import com.project.entity.Hotel;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.HotelRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {
  private final HotelRepository hotelRepository;

  public List<Hotel> findAll() {
    return hotelRepository.findAll();
  }

  public Page<Hotel> findAll(Pageable pageable) {
    return hotelRepository.findAll(pageable);
  }

  public Hotel findById(Long id) {
    return hotelRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + id));
  }

  public Hotel save(Hotel hotel) {
    return hotelRepository.save(hotel);
  }

  public void delete(Long id) {
    hotelRepository.deleteById(id);
  }
}
