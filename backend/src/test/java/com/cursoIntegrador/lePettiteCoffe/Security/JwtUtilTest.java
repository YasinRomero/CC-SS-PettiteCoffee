package com.cursoIntegrador.lePettiteCoffe.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String SECRET_KEY = "llave_De_Prueba_Momentanea_12345_ExtendedForSecurityRequirements";

    /**
     * Inicializa los recursos necesarios antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
    }

    // Tests para generateToken

    /**
     * Verifica que el token generado sea válido y tenga 3 partes.
     */
    @Test
    void testGenerateToken_Success() {
        String token = jwtUtil.generateToken("usuario@prueba.com");

        assertNotNull(token);
        assertTrue(token.contains("."));
        String[] partes = token.split("\\.");
        assertEquals(3, partes.length);
    }

    /**
     * Verifica que múltiples llamadas generen tokens válidos.
     */
    @Test
    void testGenerateToken_MultipleCalls_DifferentTokens() {
        String token1 = jwtUtil.generateToken("usuario@prueba.com");
        String token2 = jwtUtil.generateToken("usuario@prueba.com");

        assertNotNull(token1);
        assertNotNull(token2);
        assertTrue(token1.contains("."));
        assertTrue(token2.contains("."));
    }

    /**
     * Verifica la generación de token usando un correo con caracteres especiales.
     */
    @Test
    void testGenerateToken_WithSpecialCharactersEmail() {
        String emailEspecial = "usuario+tag@prueba.com";
        String token = jwtUtil.generateToken(emailEspecial);

        assertNotNull(token);
        assertEquals(emailEspecial, jwtUtil.validateAndGetUser(token));
    }

    /**
     * Verifica la generación de token con un correo largo.
     */
    @Test
    void testGenerateToken_WithLongEmail() {
        String emailLargo = "very.long.email.address.with.many.characters@test.example.com";
        String token = jwtUtil.generateToken(emailLargo);

        assertNotNull(token);
        assertEquals(emailLargo, jwtUtil.validateAndGetUser(token));
    }

    // Tests para validateAndGetUser

    /**
     * Verifica que se obtenga correctamente el usuario desde un token válido.
     */
    @Test
    void testValidateAndGetUser_Success() {
        String usuario = "usuario@prueba.com";
        String token = jwtUtil.generateToken(usuario);

        String username = jwtUtil.validateAndGetUser(token);

        assertEquals(usuario, username);
    }

    /**
     * Verifica que múltiples tokens con distintos usuarios devuelvan correctamente cada uno.
     */
    @Test
    void testValidateAndGetUser_MultipleUsers() {
        String usuario1 = "usuario1@prueba.com";
        String usuario2 = "usuario2@prueba.com";

        String token1 = jwtUtil.generateToken(usuario1);
        String token2 = jwtUtil.generateToken(usuario2);

        assertEquals(usuario1, jwtUtil.validateAndGetUser(token1));
        assertEquals(usuario2, jwtUtil.validateAndGetUser(token2));
    }

    /**
     * Verifica el comportamiento cuando se envía un token vacío.
     */
    @Test
    void testValidateAndGetUser_EmptyToken() {
        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser(""));
    }

    /**
     * Verifica que un token alterado genere excepción.
     */
    @Test
    void testValidateAndGetUser_TokenMutated() {
        String token = jwtUtil.generateToken("usuario@prueba.com");
        String mutated = token.substring(0, token.length() - 1) + "X";

        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser(mutated));
    }

    /**
     * Verifica que un token generado con otra clave secreta falle en la validación.
     */
    @Test
    void testValidateAndGetUser_InvalidTokenWithWrongSecret() {
        SecretKey wrongKey = Keys.hmacShaKeyFor("different_secret_key_123456789_ExtendedForSecurityRequirements".getBytes(StandardCharsets.UTF_8));
        String invalidToken = Jwts.builder().subject("usuario@prueba.com").signWith(wrongKey).compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser(invalidToken));
    }

    /**
     * Verifica que un token con formato incorrecto falle en la validación.
     */
    @Test
    void testValidateAndGetUser_InvalidFormat_TwoParts() {
        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser("only-two-parts.here"));
    }

    /**
     * Verifica que un token con cuatro partes sea rechazado.
     */
    @Test
    void testValidateAndGetUser_InvalidFormat_FourParts() {
        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser("a.b.c.d"));
    }

    /**
     * Verifica que un token expirado genere excepción.
     */
    @Test
    void testValidateAndGetUser_ExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("usuario@prueba.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(4000)))
                .expiration(Date.from(Instant.now().minusSeconds(1000)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.validateAndGetUser(expiredToken));
    }

    // Tests para validateTokenAndGetClaims

    /**
     * Verifica que los claims se obtengan correctamente desde un token válido.
     */
    @Test
    void testValidateTokenAndGetClaims_Success() {
        String usuario = "usuario@prueba.com";
        String token = jwtUtil.generateToken(usuario);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertNotNull(claims);
        assertEquals(usuario, claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    /**
     * Verifica el contenido esperado dentro de los claims.
     */
    @Test
    void testValidateTokenAndGetClaims_ClaimsContent() {
        String usuario = "test@example.com";
        String token = jwtUtil.generateToken(usuario);

        Claims claims = jwtUtil.validateTokenAndGetClaims(token);

        assertEquals(usuario, claims.getSubject());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    /**
     * Verifica que un token expirado no pueda obtener claims.
     */
    @Test
    void testValidateTokenAndGetClaims_ExpiredToken() {
        String expiredToken = Jwts.builder()
                .subject("usuario@prueba.com")
                .issuedAt(Date.from(Instant.now().minusSeconds(4000)))
                .expiration(Date.from(Instant.now().minusSeconds(1000)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims(expiredToken));
    }

    /**
     * Verifica que una firma inválida genere excepción al validar claims.
     */
    @Test
    void testValidateTokenAndGetClaims_InvalidSignature() {
        SecretKey wrongKey = Keys.hmacShaKeyFor("wrong_secret_key_1234567890_ExtendedForSecurityRequirements".getBytes(StandardCharsets.UTF_8));
        String invalidToken = Jwts.builder().subject("usuario@prueba.com").signWith(wrongKey).compact();

        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims(invalidToken));
    }

    /**
     * Verifica que no se acepten tokens vacíos en la obtención de claims.
     */
    @Test
    void testValidateTokenAndGetClaims_EmptyToken() {
        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims(""));
    }

    /**
     * Verifica que formatos malformados de token sean rechazados.
     */
    @Test
    void testValidateTokenAndGetClaims_MalformedToken() {
        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims("malformed.token"));
        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims("just.one"));
        assertThrows(RuntimeException.class, () -> jwtUtil.validateTokenAndGetClaims("a.b.c.d.e"));
    }
}

