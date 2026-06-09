package com.prode.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prode.application.dto.request.UserRequest;
import com.prode.application.dto.response.UserResponse;
import com.prode.domain.enums.RolUsuario;
import com.prode.domain.model.User;
import com.prode.domain.port.outbound.UserRepository;
import com.prode.shared.exception.BusinessException;
import com.prode.shared.exception.ResourceNotFoundException;


@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(UserRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "Ya existe un usuario con ese email"
            );
        }

        User user = new User();

        user.setNombre(request.getNombre());
        user.setApellido(request.getApellido());
        user.setEmail(request.getEmail());

        user.setContrasenia(request.getContrasenia());

        user.setRol(RolUsuario.USER);

        user.setActivo(true);

        user.setPuntosTotal(0);

        user.setFechaCreacion(LocalDateTime.now());

        User saved = userRepository.save(user);

        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setNombre(user.getNombre());
        response.setApellido(user.getApellido());
        response.setEmail(user.getEmail());
        response.setRol(user.getRol());
        response.setPuntosTotal(user.getPuntosTotal());

        return response;
    }
}