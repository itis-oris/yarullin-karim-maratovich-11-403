package com.project.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.project.entity.User;
import com.project.exception.BusinessException;
import com.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@TestPropertySource(properties = {"spring.freemarker.check-template-location=false"})
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @Test
  void loginPageShouldReturnLayout() throws Exception {
    mockMvc
        .perform(get("/auth/login"))
        .andExpect(status().isOk())
        .andExpect(view().name("layout"))
        .andExpect(model().attribute("pageContent", "auth/login.ftlh"));
  }

  @Test
  void registerShouldRedirectToLoginOnSuccess() throws Exception {
    mockMvc
        .perform(
            post("/auth/register")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "password123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/auth/login"));

    verify(userService)
        .register("newuser", "newuser@example.com", "password123", User.UserRole.USER);
  }

  @Test
  void registerShouldRedirectBackWhenBusinessExceptionOccurs() throws Exception {
    doThrow(new BusinessException("Username already exists"))
        .when(userService)
        .register(any(), any(), any(), any());

    mockMvc
        .perform(
            post("/auth/register")
                .param("username", "newuser")
                .param("email", "newuser@example.com")
                .param("password", "password123"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/auth/register"));
  }
}
