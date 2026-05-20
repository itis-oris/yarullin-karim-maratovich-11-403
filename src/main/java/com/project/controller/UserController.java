package com.project.controller;

import com.project.entity.User;
import com.project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public String list(Model model, @PageableDefault(size = 10) Pageable pageable) {
    var page = userService.findAll(pageable);
    model.addAttribute("users", page.getContent());
    model.addAttribute("page", page);
    model.addAttribute("pageTitle", "Пользователи");
    model.addAttribute("pageContent", "users/list.ftlh");
    return "layout";
  }

  @PostMapping("/{id}/role")
  public String updateRole(@PathVariable Long id, @RequestParam User.UserRole role) {
    userService.updateRole(id, role);
    return "redirect:/users";
  }

  @GetMapping("/me")
  public String me(@AuthenticationPrincipal User currentUser, Model model) {
    model.addAttribute("user", currentUser);
    model.addAttribute("pageTitle", "Мой профиль");
    model.addAttribute("pageContent", "users/me.ftlh");
    return "layout";
  }

  @PostMapping("/me")
  public String updateMe(
      @AuthenticationPrincipal User currentUser,
      @RequestParam String email,
      @RequestParam(required = false) String password) {
    userService.updateProfile(currentUser.getId(), email, password);
    return "redirect:/users/me";
  }
}
