package com.prode.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


 // Configuración de seguridad exclusiva para tests.
 // Reemplaza SecurityConfig en el contexto de @WebMvcTest,
 // permitiendo que @WithMockUser y .with(user(...)) funcionen correctamente
 // sin necesidad de JWT, ya que su funcionamiento stateles causaba conflictos con las pruebas.
 
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
