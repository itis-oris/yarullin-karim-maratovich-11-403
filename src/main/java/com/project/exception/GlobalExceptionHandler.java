package com.project.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public Object handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());

    if (isAjaxRequest(request)) {
      return ResponseEntity.status(404).body(new ErrorResponse(ex.getMessage(), 404));
    }

    ModelAndView mav = new ModelAndView("error/404");
    mav.addObject("message", ex.getMessage());
    mav.setStatus(org.springframework.http.HttpStatus.NOT_FOUND);
    return mav;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Object handleValidationErrors(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    log.warn("Validation failed: {}", ex.getMessage());

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String field = ((FieldError) error).getField();
              String message = error.getDefaultMessage();
              errors.put(field, message);
            });

    if (isAjaxRequest(request)) {
      return ResponseEntity.badRequest().body(new ErrorResponse("Validation failed", 400, errors));
    }

    ModelAndView mav = new ModelAndView("redirect:" + request.getHeader("Referer"));
    mav.addObject("errors", errors.values());
    return mav;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
    log.warn("Access denied: {}", ex.getMessage());

    if (isAjaxRequest(request)) {
      return ResponseEntity.status(403).body(new ErrorResponse("Доступ запрещён", 403));
    }

    return new ModelAndView("error/403", org.springframework.http.HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({TourCreationException.class, BusinessException.class})
  public Object handleBusinessException(BusinessException ex, HttpServletRequest request) {
    log.error("Business error: {}", ex.getMessage(), ex);

    if (isAjaxRequest(request)) {
      return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), 400));
    }

    ModelAndView mav = new ModelAndView("error/business");
    mav.addObject("message", ex.getMessage());
    mav.addObject("redirectUrl", request.getHeader("Referer"));
    return mav;
  }

  @ExceptionHandler(Exception.class)
  public Object handleGenericException(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error: ", ex);

    if (isAjaxRequest(request)) {
      return ResponseEntity.status(500).body(new ErrorResponse("Внутренняя ошибка сервера", 500));
    }

    ModelAndView mav = new ModelAndView("error/500");
    mav.addObject("message", "Произошла непредвиденная ошибка");
    mav.addObject("errorId", java.util.UUID.randomUUID().toString().substring(0, 8));
    mav.setStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    return mav;
  }

  private boolean isAjaxRequest(HttpServletRequest request) {
    return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"))
        || (request.getHeader("Accept") != null
            && request.getHeader("Accept").contains("application/json"));
  }

  public record ErrorResponse(String message, int status, Map<String, String> errors) {
    public ErrorResponse(String message, int status) {
      this(message, status, null);
    }
  }
}
