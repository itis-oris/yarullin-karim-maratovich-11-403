package com.project.controller;

import com.project.dto.ExcursionDTO;
import com.project.entity.User;
import com.project.service.ExcursionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/excursions")
@RequiredArgsConstructor
public class ExcursionController {
  private final ExcursionService excursionService;

  @GetMapping
  public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
    var page = excursionService.searchExcursions("", pageable);
    model.addAttribute("excursions", page.getContent());
    model.addAttribute("page", page);
    model.addAttribute("pageTitle", "Экскурсии");
    model.addAttribute("pageContent", "excursions/list.ftlh");
    return "layout";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable Long id, Model model) {
    model.addAttribute("excursion", excursionService.getExcursionById(id));
    model.addAttribute("pageTitle", "Экскурсия");
    model.addAttribute("pageContent", "excursions/detail.ftlh");
    return "layout";
  }

  @GetMapping("/create")
  public String createForm(Model model) {
    model.addAttribute("excursion", new ExcursionDTO());
    model.addAttribute("pageTitle", "Создать экскурсию");
    model.addAttribute("pageContent", "excursions/form.ftlh");
    return "layout";
  }

  @PostMapping("/create")
  public String create(
      @ModelAttribute("excursion") @Valid ExcursionDTO dto,
      BindingResult br,
      @AuthenticationPrincipal User user,
      RedirectAttributes ra) {
    if (br.hasErrors()) return "redirect:/excursions/create";
    excursionService.createExcursion(dto, user.getId());
    ra.addFlashAttribute("successMessage", "Экскурсия создана");
    return "redirect:/excursions";
  }

  @GetMapping("/{id}/edit")
  public String editForm(@PathVariable Long id, Model model) {
    model.addAttribute("excursion", excursionService.getExcursionById(id));
    model.addAttribute("pageTitle", "Редактировать экскурсию");
    model.addAttribute("pageContent", "excursions/form.ftlh");
    return "layout";
  }

  @PostMapping("/{id}/edit")
  public String edit(
      @PathVariable Long id,
      @ModelAttribute("excursion") @Valid ExcursionDTO dto,
      BindingResult br,
      @AuthenticationPrincipal User user) {
    if (br.hasErrors()) return "redirect:/excursions/" + id + "/edit";
    excursionService.updateExcursion(id, dto, user.getId());
    return "redirect:/excursions";
  }

  @PostMapping("/{id}/delete")
  public String delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
    excursionService.deleteExcursion(id, user.getId());
    return "redirect:/excursions";
  }
}
