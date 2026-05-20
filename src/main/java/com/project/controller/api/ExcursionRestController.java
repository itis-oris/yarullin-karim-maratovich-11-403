package com.project.controller.api;

import com.project.dto.ExcursionDTO;
import com.project.entity.User;
import com.project.service.ExcursionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/excursions")
@Tag(name = "Excursions", description = "API для управления экскурсиями")
@RequiredArgsConstructor
@Slf4j
public class ExcursionRestController {

  private final ExcursionService excursionService;

  @Operation(
      summary = "Получить все экскурсии",
      description = "Возвращает пагинированный список экскурсий")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Успех",
        content = @Content(schema = @Schema(implementation = Page.class))),
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
  })
  @GetMapping
  public ResponseEntity<Page<ExcursionDTO>> getAllExcursions(
      @Parameter(description = "Параметры пагинации") @PageableDefault(size = 20)
          Pageable pageable) {

    return ResponseEntity.ok(
        excursionService
            .searchExcursions("", pageable)
            .map(excursion -> excursionService.getExcursionById(excursion.getId())));
  }

  @Operation(summary = "Получить экскурсию по ID", description = "Возвращает детали экскурсии")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Успех"),
    @ApiResponse(responseCode = "404", description = "Экскурсия не найдена")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ExcursionDTO> getExcursionById(
      @Parameter(description = "ID экскурсии", required = true) @PathVariable Long id) {
    return ResponseEntity.ok(excursionService.getExcursionById(id));
  }

  @Operation(
      summary = "Создать экскурсию",
      description = "Создаёт новую экскурсию (только MANAGER)")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Создано"),
    @ApiResponse(responseCode = "400", description = "Неверные данные"),
    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
  })
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping
  public ResponseEntity<ExcursionDTO> createExcursion(
      @Parameter(description = "Данные экскурсии", required = true) @RequestBody @Valid
          ExcursionDTO dto,
      @AuthenticationPrincipal User currentUser) {

    log.info("REST API: Creating excursion by user {}", currentUser.getUsername());

    ExcursionDTO created = excursionService.createExcursion(dto, currentUser.getId());

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.getId())
            .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @Operation(
      summary = "Обновить экскурсию",
      description = "Обновляет существующую экскурсию (только MANAGER)")
  @PutMapping("/{id}")
  public ResponseEntity<ExcursionDTO> updateExcursion(
      @PathVariable Long id,
      @RequestBody @Valid ExcursionDTO dto,
      @AuthenticationPrincipal User currentUser) {

    return ResponseEntity.ok(excursionService.updateExcursion(id, dto, currentUser.getId()));
  }

  @Operation(summary = "Удалить экскурсию", description = "Удаляет экскурсию (только MANAGER)")
  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204", description = "Удалено")
  public ResponseEntity<Void> deleteExcursion(
      @PathVariable Long id, @AuthenticationPrincipal User currentUser) {

    excursionService.deleteExcursion(id, currentUser.getId());
    return ResponseEntity.noContent().build();
  }
}
