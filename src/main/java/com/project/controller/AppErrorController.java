package com.project.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements ErrorController {

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model) {
    Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    int status = statusCode != null ? Integer.parseInt(statusCode.toString()) : 500;

    if (status == HttpStatus.NOT_FOUND.value()) {
      model.addAttribute("message", "Ресурс не найден");
      model.addAttribute("pageTitle", "404 - Не найдено");
      model.addAttribute("pageContent", "error/404.ftlh");
    } else if (status == HttpStatus.FORBIDDEN.value()) {
      model.addAttribute("pageTitle", "403 - Доступ запрещен");
      model.addAttribute("pageContent", "error/403.ftlh");
    } else {
      model.addAttribute("message", "Произошла непредвиденная ошибка");
      model.addAttribute("pageTitle", "Ошибка сервера");
      model.addAttribute("pageContent", "error/500.ftlh");
    }

    return "layout";
  }
}
