package com.project.controller;

import com.project.dto.TourDTO;
import com.project.entity.User;
import com.project.service.ExcursionService;
import com.project.service.HotelService;
import com.project.service.TourService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tours")
@RequiredArgsConstructor
@Slf4j
public class TourController {

  private final TourService tourService;
  private final HotelService hotelService;
  private final ExcursionService excursionService;

  @GetMapping
  public String listTours(
      Model model,
      @PageableDefault(size = 12) Pageable pageable,
      @RequestParam(required = false) String keyword) {

    Page<TourDTO> tours;
    if (keyword != null && !keyword.isBlank()) {
      tours =
          tourService
              .searchActiveTours(keyword, pageable)
              .map(tour -> tourService.getTourById(tour.getId()));
    } else {
      var tourList = tourService.getAllActiveTours();
      tours =
          PageableExecutionUtils.getPage(
              tourList.stream().map(tour -> tourService.getTourById(tour.getId())).toList(),
              pageable,
              tourList::size);
    }

    model.addAttribute("tours", tours.getContent());
    model.addAttribute("page", tours);
    model.addAttribute("pageTitle", "Все туры");
    model.addAttribute("selectedCurrency", "RUB");
    model.addAttribute("pageContent", "tours/list.ftlh");

    return "layout";
  }

  @GetMapping("/{id}")
  public String viewTour(@PathVariable Long id, Model model) {
    TourDTO tour = tourService.getTourById(id);
    model.addAttribute("tour", tour);
    model.addAttribute("selectedCurrency", "RUB");
    model.addAttribute("pageTitle", tour.getTitle());
    model.addAttribute("pageContent", "tours/detail.ftlh");
    return "layout";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    if (!model.containsAttribute("tour")) {
      model.addAttribute("tour", new TourDTO());
    }
    addTourFormDictionaries(model);
    model.addAttribute("pageTitle", "Создать тур");
    model.addAttribute("pageContent", "tours/form.ftlh");
    return "layout";
  }

  @PostMapping("/create")
  public String createTour(
      @ModelAttribute("tour") @Valid TourDTO dto,
      BindingResult result,
      @RequestParam(required = false) Long hotelId,
      @RequestParam(required = false) List<Long> excursionIds,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
          List<LocalTime> startTimes,
      @AuthenticationPrincipal User currentUser,
      RedirectAttributes redirectAttributes) {

    if (result.hasErrors()) {
      redirectAttributes.addFlashAttribute(
          "errors",
          result.getFieldErrors().stream()
              .map(f -> f.getField() + ": " + f.getDefaultMessage())
              .toList());
      redirectAttributes.addFlashAttribute("tour", dto);
      return "redirect:/tours/create";
    }

    try {
      tourService.createTour(dto, currentUser.getId(), hotelId, excursionIds, startTimes);
      redirectAttributes.addFlashAttribute("successMessage", "Тур успешно создан! 🎉");
    } catch (Exception ex) {
      redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + ex.getMessage());
      redirectAttributes.addFlashAttribute("tour", dto);
      return "redirect:/tours/create";
    }

    return "redirect:/tours";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("tour", tourService.getTourById(id));
    addTourFormDictionaries(model);
    model.addAttribute("pageTitle", "Редактировать тур");
    model.addAttribute("pageContent", "tours/form.ftlh");
    return "layout";
  }

  @PostMapping("/{id}/edit")
  public String editTour(
      @PathVariable Long id,
      @ModelAttribute("tour") @Valid TourDTO dto,
      BindingResult result,
      @RequestParam(required = false) Long hotelId,
      @RequestParam(required = false) List<Long> excursionIds,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
          List<LocalTime> startTimes,
      @AuthenticationPrincipal User currentUser,
      RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      redirectAttributes.addFlashAttribute(
          "errors",
          result.getFieldErrors().stream()
              .map(f -> f.getField() + ": " + f.getDefaultMessage())
              .toList());
      return "redirect:/tours/" + id + "/edit";
    }
    tourService.updateTour(id, dto, currentUser.getId(), hotelId, excursionIds, startTimes);
    redirectAttributes.addFlashAttribute("successMessage", "Тур обновлён");
    return "redirect:/tours";
  }

  @PostMapping("/{id}/delete")
  public String deleteTour(
      @PathVariable Long id,
      @AuthenticationPrincipal User currentUser,
      RedirectAttributes redirectAttributes) {
    tourService.deleteTour(id, currentUser.getId());
    redirectAttributes.addFlashAttribute("successMessage", "Тур удалён");
    return "redirect:/tours";
  }

  @GetMapping("/filter")
  public String filterTours(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      Model model) {

    var tours =
        tourService.findToursByDateRange(startDate, endDate).stream()
            .map(tour -> tourService.getTourById(tour.getId()))
            .toList();
    model.addAttribute("tours", tours);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    return "tours/list";
  }

  private void addTourFormDictionaries(Model model) {
    model.addAttribute("hotels", hotelService.findAll());
    model.addAttribute("allExcursions", excursionService.findAll());
  }
}
