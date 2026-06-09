package com.prode.infrastructure.adapter.inbound.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prode.application.dto.request.UserRequest;
import com.prode.application.dto.response.UserResponse;
import com.prode.application.service.UserService;
import com.prode.shared.dto.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Gestion de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario")
    public ResponseEntity<ApiResult<UserResponse>> register(
            @Valid @RequestBody UserRequest request) {

        UserResponse user = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.ok(
                        "Usuario registrado exitosamente",
                        user
                ));
    }
}