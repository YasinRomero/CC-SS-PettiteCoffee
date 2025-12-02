package com.cursoIntegrador.lePettiteCoffe.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad para la aplicación.
 * Define las políticas de autenticación, autorización,
 * manejo de sesiones y filtros de seguridad.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Crea y expone un codificador de contraseñas basado en BCrypt.
     *
     * @return Implementación de PasswordEncoder utilizando BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura la cadena de filtros de seguridad, incluyendo:
     * <ul>
     *     <li>Rutas públicas y protegidas</li>
     *     <li>Desactivación de CSRF</li>
     *     <li>Manejo de sesiones stateless</li>
     *     <li>Inserción del filtro JWT antes del filtro de autenticación estándar</li>
     * </ul>
     *
     * @param http Objeto HttpSecurity para construir la configuración.
     * @param jwtAuthFilter Filtro personalizado encargado de validar tokens JWT.
     * @return Cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http.cors(cors -> {
        })
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**",
                                "/products/getAllProducts",
                                "/images/**",
                                "/sucursales/listar",
                                "/IA/**",
                                "/actuator/prometheus",
                                "/reviews/addReviewGuest",
                                "/reviews/getReviews")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS));
        return http.build();
    }

}

