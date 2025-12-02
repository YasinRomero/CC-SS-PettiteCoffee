package com.cursoIntegrador.lePettiteCoffe.Service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Servicio de caché para almacenar y gestionar tokens asociados a usuarios, 
 * utilizando Guava Cache con un tiempo de expiración.
 */
@Component
public class TokenCache {
    private final Cache<String, String> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(500).build();

    /**
     * Guarda un token asociado a una dirección de correo electrónico en la caché.
     * @param email La dirección de correo electrónico del usuario.
     * @param token El token a guardar.
     */
    public void guardarToken(String email, String token) {
        tokenCache.put(email, token);
    }

    /**
     * Recupera un token de la caché asociado a una dirección de correo electrónico.
     * @param email La dirección de correo electrónico del usuario.
     * @return El token asociado al email, o null si el token no está presente o ha expirado.
     */
    public String obtenerToken(String email) {
        return tokenCache.getIfPresent(email);
    }

    /**
     * Elimina un token de la caché asociado a una dirección de correo electrónico.
     * @param email La dirección de correo electrónico del usuario cuyo token se debe eliminar.
     */
    public void eliminarToken(String email) {
        tokenCache.invalidate(email);
    }
}
