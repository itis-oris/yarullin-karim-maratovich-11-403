package com.project.controller;

import com.project.entity.Order;
import com.project.entity.User;
import com.project.service.OrderService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
  private final OrderService orderService;

  @GetMapping("/cart")
  public String cart(
      @AuthenticationPrincipal User user,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    var page = orderService.getCart(user.getId(), pageable);
    model.addAttribute("orders", page.getContent());
    model.addAttribute("page", page);
    model.addAttribute("pageTitle", "Корзина");
    model.addAttribute("pageContent", "orders/cart.ftlh");
    return "layout";
  }

  @GetMapping("/history")
  public String history(
      @AuthenticationPrincipal User user,
      Model model,
      @PageableDefault(size = 10) Pageable pageable) {
    var page = orderService.getHistory(user.getId(), pageable);
    model.addAttribute("orders", page.getContent());
    model.addAttribute("page", page);
    model.addAttribute("pageTitle", "История заказов");
    model.addAttribute("pageContent", "orders/history.ftlh");
    return "layout";
  }

  @PostMapping("/add-tour")
  public String addTour(
      @AuthenticationPrincipal User user,
      @RequestParam Long tourId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingStartDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingEndDate,
      @RequestParam Integer guests,
      @RequestParam(defaultValue = "ECONOMY") Order.RoomLevel roomLevel,
      RedirectAttributes redirectAttributes) {
    orderService.addTourToCart(
        user.getId(), tourId, bookingStartDate, bookingEndDate, guests, roomLevel);
    redirectAttributes.addFlashAttribute("successMessage", "Тур добавлен в корзину");
    return "redirect:/orders/cart";
  }

  @PostMapping("/checkout")
  public String checkout(
      @AuthenticationPrincipal User user, RedirectAttributes redirectAttributes) {
    orderService.checkout(user.getId());
    redirectAttributes.addFlashAttribute("successMessage", "Заказ оформлен");
    return "redirect:/orders/history";
  }

  @PostMapping("/{id}/remove")
  public String remove(@AuthenticationPrincipal User user, @PathVariable Long id) {
    orderService.removeFromCart(user.getId(), id);
    return "redirect:/orders/cart";
  }
}
