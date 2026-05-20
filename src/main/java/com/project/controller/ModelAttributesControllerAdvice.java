package com.project.controller;

import com.project.entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ModelAttributesControllerAdvice {

  @ModelAttribute("isAuthenticated")
  public boolean isAuthenticated() {
    return currentAuthentication() != null;
  }

  @ModelAttribute("canManageContent")
  public boolean canManageContent() {
    Authentication authentication = currentAuthentication();
    return hasRole(authentication, User.UserRole.MANAGER)
        || hasRole(authentication, User.UserRole.ADMIN);
  }

  @ModelAttribute("hasAdminAccess")
  public boolean hasAdminAccess() {
    return hasRole(currentAuthentication(), User.UserRole.ADMIN);
  }

  @ModelAttribute("hasManagerAccess")
  public boolean hasManagerAccess() {
    return canManageContent();
  }

  private Authentication currentAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null
        || !authentication.isAuthenticated()
        || authentication instanceof AnonymousAuthenticationToken) {
      return null;
    }
    return authentication;
  }

  private boolean hasRole(Authentication authentication, User.UserRole role) {
    if (authentication == null || role == null) {
      return false;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof User user && role == user.getRole()) {
      return true;
    }

    String authorityName = "ROLE_" + role.name();
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authorityName.equals(authority.getAuthority()));
  }
}
