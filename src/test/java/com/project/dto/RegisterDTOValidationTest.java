package com.project.dto;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterDTOValidationTest {

  private Validator validator;

  @BeforeEach
  void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void shouldPassValidationForCorrectData() {
    RegisterDTO dto = new RegisterDTO();
    dto.setUsername("testuser");
    dto.setEmail("test@example.com");
    dto.setPassword("strongpass123");

    Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationForInvalidData() {
    RegisterDTO dto = new RegisterDTO();
    dto.setUsername("ab");
    dto.setEmail("invalid-email");
    dto.setPassword("123");

    Set<ConstraintViolation<RegisterDTO>> violations = validator.validate(dto);

    assertEquals(3, violations.size());
  }
}
