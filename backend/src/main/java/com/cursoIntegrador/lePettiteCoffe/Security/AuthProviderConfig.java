package com.cursoIntegrador.lePettiteCoffe.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

/**
 * Configuración encargada de definir los proveedores de autenticación
 * y el administrador de autenticación para el sistema de seguridad.
 */
@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Configura y devuelve el proveedor de autenticación basado en DAO,
     * utilizando el servicio de usuarios y el codificador de contraseñas.
     *
     * @return el proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Proporciona el administrador de autenticación a partir de la configuración existente.
     *
     * @param config la configuración de autenticación de Spring Security.
     * @return el administrador de autenticación configurado.
     * @throws Exception si ocurre un error obteniendo el administrador.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

