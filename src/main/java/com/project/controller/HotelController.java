package com.project.controller;

import com.project.entity.Hotel;
import com.project.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
  private final HotelService hotelService;

  @GetMapping
  public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
    var page = hotelService.findAll(pageable);
    model.addAttribute("hotels", page.getContent());
    model.addAttribute("page", page);
    model.addAttribute("pageTitle", "Отели");
    model.addAttribute("pageContent", "hotels/list.ftlh");
    return "layout";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("hotel", new Hotel());
    model.addAttribute("pageTitle", "Создать отель");
    model.addAttribute("pageContent", "hotels/form.ftlh");
    return "layout";
  }

  @PostMapping("/create")
  public String create(@ModelAttribute("hotel") @Valid Hotel hotel, BindingResult br) {
    if (br.hasErrors()) return "redirect:/hotels/create";
    hotelService.save(hotel);
    return "redirect:/hotels";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("hotel", hotelService.findById(id));
    model.addAttribute("pageTitle", "Редактировать отель");
    model.addAttribute("pageContent", "hotels/form.ftlh");
    return "layout";
  }

  @PostMapping("/{id}/edit")
  public String edit(
      @PathVariable Long id, @ModelAttribute("hotel") @Valid Hotel hotel, BindingResult br) {
    if (br.hasErrors()) return "redirect:/hotels/" + id + "/edit";
    hotel.setId(id);
    hotelService.save(hotel);
    return "redirect:/hotels";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id) {
    hotelService.delete(id);
    return "redirect:/hotels";
  }
}
