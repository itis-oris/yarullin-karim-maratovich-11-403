package com.project.service;

import com.project.entity.User;
import com.project.exception.BusinessException;
import com.project.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> findByUsername(String u) {
    return userRepository.findByUsername(u);
  }

  public List<User> findAll() {
    return userRepository.findAll();
  }

  public Page<User> findAll(Pageable pageable) {
    return userRepository.findAll(pageable);
  }

  public User register(String username, String email, String password, User.UserRole role) {
    if (userRepository.existsByUsername(username)) {
      throw new BusinessException("Пользователь с таким username уже существует");
    }
    if (userRepository.existsByEmail(email)) {
      throw new BusinessException("Пользователь с таким email уже существует");
    }

    User u =
        User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(role == null ? User.UserRole.USER : role)
            .build();
    return userRepository.save(u);
  }

  // Метод обновления роли
  @Transactional
  public void updateRole(Long id, User.UserRole role) {
    userRepository
        .findById(id)
        .ifPresent(
            user -> {
              user.setRole(role);
              userRepository.save(user);
            });
  }

  // Метод обновления профиля (email и пароль)
  @Transactional
  public void updateProfile(Long id, String email, String password) {
    userRepository
        .findById(id)
        .ifPresent(
            user -> {
              user.setEmail(email);
              if (password != null && !password.isEmpty()) {
                user.setPassword(passwordEncoder.encode(password));
              }
              userRepository.save(user);
            });
  }
}
