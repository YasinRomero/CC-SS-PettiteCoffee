package com.cursoIntegrador.lePettiteCoffe.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración global de CORS (Cross-Origin Resource Sharing) para la aplicación.
 * Permite gestionar los orígenes, métodos y encabezados que pueden acceder al backend.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Define la configuración personalizada de CORS, permitiendo solicitudes desde
     * un origen específico y habilitando ciertos métodos HTTP.
     *
     * @return Un objeto WebMvcConfigurer con la configuración de CORS aplicada.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            /**
             * Registra las reglas de CORS para todos los endpoints del backend.
             *
             * @param registry Objeto que administra las configuraciones de CORS.
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // Aqui cambias a tu host waaa
                        .allowedMethods("GET", "POST", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
