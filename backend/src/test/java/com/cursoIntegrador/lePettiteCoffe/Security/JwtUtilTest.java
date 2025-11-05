package com.cursoIntegrador.lePettiteCoffe.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtilTest {

    @Test
    void generateTokenAndValidate() {
        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.generateToken("usuario@prueba.com");
        String username = jwtUtil.validateAndGetUser(token);

        assertEquals("usuario@prueba.com", username);
    }

    @Test
    void testTokenExpirado() throws InterruptedException {
        JwtUtil jwtUtil = new JwtUtil();

        String token = Jwts.builder()
                .subject("usuario@prueba.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(4000)))
                .expiration(Date.from(Instant.now().minusSeconds(1000)))
                .signWith(Keys.hmacShaKeyFor("llave_De_Prueba_Momentanea_12345".getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class,
                () -> jwtUtil.validateAndGetUser(token));
    }

    @Test
    void testTokenInvalido() {
        JwtUtil jwtUtil = new JwtUtil();
        SecretKey otraClave = Keys.hmacShaKeyFor("llave_De_Prueba_Momentanea_123456".getBytes(StandardCharsets.UTF_8));
        String tokenInvalido = Jwts.builder().subject("usuario@prueba.com").signWith(otraClave).compact();

        assertThrows(RuntimeException.class,
                () -> jwtUtil.validateAndGetUser(tokenInvalido));
    }
}
