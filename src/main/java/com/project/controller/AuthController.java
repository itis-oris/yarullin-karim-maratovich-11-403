package com.project.controller;

import com.project.dto.RegisterDTO;
import com.project.entity.User;
import com.project.exception.BusinessException;
import com.project.service.UserService;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final UserService userService;

  @GetMapping("/login")
  public String login(@RequestParam(name = "error", required = false) Boolean error, Model model) {
    if (Boolean.TRUE.equals(error)) {
      model.addAttribute("errorMessage", "Неверный логин или пароль");
    }
    model.addAttribute("pageTitle", "Вход");
    model.addAttribute("pageContent", "auth/login.ftlh");
    return "layout";
  }

  @GetMapping("/register")
  public String registerPage(Model model) {
    if (!model.containsAttribute("registerForm")) {
      model.addAttribute("registerForm", new RegisterDTO());
    }
    model.addAttribute("pageTitle", "Регистрация");
    model.addAttribute("pageContent", "auth/register.ftlh");
    return "layout";
  }

  @PostMapping("/register")
  public String register(
      @ModelAttribute("registerForm") @Valid RegisterDTO form,
      BindingResult br,
      RedirectAttributes ra) {
    if (br.hasErrors()) {
      ra.addFlashAttribute(
          "errors",
          br.getFieldErrors().stream()
              .map(e -> e.getField() + ": " + e.getDefaultMessage())
              .collect(Collectors.toList()));
      ra.addFlashAttribute("registerForm", form);
      return "redirect:/auth/register";
    }

    try {
      userService.register(
          form.getUsername(), form.getEmail(), form.getPassword(), User.UserRole.USER);
      ra.addFlashAttribute("successMessage", "Регистрация успешна. Войдите в систему.");
      return "redirect:/auth/login";
    } catch (BusinessException ex) {
      ra.addFlashAttribute("errorMessage", ex.getMessage());
      ra.addFlashAttribute("registerForm", form);
      return "redirect:/auth/register";
    }
  }
}
