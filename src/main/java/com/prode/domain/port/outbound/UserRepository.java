package com.prode.domain.port.outbound;

import java.util.List;
import java.util.Optional;

import com.prode.domain.model.User;

public interface UserRepository {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User save(User user);

    User update(User user);

    boolean existsByEmail(String email);

    
}