package com.cursoIntegrador.lePettiteCoffe.Security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para la generación y validación de tokens JWT.
 * Encargada de crear tokens firmados y extraer información contenida en ellos.
 */
@Component
public class JwtUtil {

    private static final String LLAVE = "llave_De_Prueba_Momentanea_12345";
    private final SecretKey secretKey;

    /**
     * Construye la instancia inicializando la clave secreta usada para firmar los tokens.
     */
    public JwtUtil() {
        this.secretKey = Keys.hmacShaKeyFor(LLAVE.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT basado en el nombre de usuario proporcionado.
     *
     * @param username Nombre de usuario que será incluido como sujeto del token.
     * @return Token JWT generado.
     */
    public String generateToken(String username) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expires = Date.from(now.plusSeconds(3600));

        String token = Jwts.builder().subject(username).issuedAt(issuedAt).expiration(expires).signWith(secretKey)
                .compact();

        return token;
    }

    /**
     * Valida el token y obtiene el nombre de usuario contenido en él.
     *
     * @param token Token JWT recibido.
     * @return Nombre de usuario extraído del token.
     */
    public String validateAndGetUser(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims.getSubject();
    }

    /**
     * Valida el token y devuelve todos los claims asociados.
     *
     * @param token Token JWT que se desea validar.
     * @return Claims contenidos en el token.
     */
    public Claims validateTokenAndGetClaims(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            throw new RuntimeException("Token inválido o expirado", e);
        }
    }
}
