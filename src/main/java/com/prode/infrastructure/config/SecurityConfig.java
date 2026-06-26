package com.prode.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.prode.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS ACTUALIZADAS (Se agregaron refresh y logout)
                        .requestMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/refresh",
                                "/api/users/logout",
                                "/users/login",
                                "/users/new",
                                "/users",
                                "/error",
                                "/css/**", "/js/**", "/images/**",
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()

                        // 2. RESTRICCIÓN ESTRICTA API: Solo ADMIN puede modificar datos
                        .requestMatchers(HttpMethod.POST, "/api/matches/**", "/api/rounds/**", "/api/teams/**",
                                "/api/players/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/matches/**", "/api/rounds/**", "/api/teams/**",
                                "/api/players/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/matches/**", "/api/rounds/**", "/api/teams/**",
                                "/api/players/**")
                        .hasRole("ADMIN")

                        // 3. PANEL USER: USER y ADMIN
                        .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

                        // 4. RESTRICCIÓN ESTRICTA WEB (Thymeleaf): Solo ADMIN
                        .requestMatchers("/", "/matches/**", "/rounds/**", "/teams/**", "/players/**")
                        .hasRole("ADMIN")

                        // 5. API LECTURA: USER y ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN")

                        // 6. BLOQUEO POR DEFECTO
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.sendError(401, "No autorizado");
                            } else {
                                response.sendRedirect("/users/login");
                            }
                        }))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}