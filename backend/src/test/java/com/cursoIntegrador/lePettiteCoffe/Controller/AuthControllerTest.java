package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountLoginDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login.LoginRequest;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login.PasswordChangeRequest;
import com.cursoIntegrador.lePettiteCoffe.Service.WelcomeService;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Service.AuthService;
import com.cursoIntegrador.lePettiteCoffe.Service.PasswordRecoveryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    /**
     * Pruebas unitarias para AuthController que cubren login, registro, logout y flujos de recuperación de contraseña.
     */

    @InjectMocks
    private AuthController controller;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private WelcomeService welcomeService;

    @Test
    /**
     * Verifica que el endpoint de login devuelve OK y los datos de inicio de sesión esperados cuando las credenciales son válidas.
     */
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("user@test.com", "pass123");

        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("loginData", new AccountLoginDTO(
            new Cuenta(999, "user@test.com", "pass123", "ADMIN", "Activo", LocalDateTime.now(), "123456789"), "tokenFake"));

        Mockito.when(authService.login("user@test.com", "pass123")).thenReturn(fakeResponse);

        ResponseEntity<?> response = controller.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fakeResponse, response.getBody());
        Mockito.verify(authService).login("user@test.com", "pass123");
    }

    @Test
    /**
     * Verifica que login devuelve UNAUTHORIZED con un mensaje de error cuando la contraseña es inválida.
     */
    void testLoginFail_InvalidPassword() {
        LoginRequest request = new LoginRequest("user@test.com", "wrongpass");

        Mockito.when(authService.login("user@test.com", "wrongpass"))
                .thenThrow(new RuntimeException("Credenciales inválidas"));

        ResponseEntity<?> response = controller.login(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());
        Mockito.verify(authService).login("user@test.com", "wrongpass");
    }

    @Test
    /**
     * Verifica que protectedTest devuelve OK e incluye el nombre de usuario autenticado.
     */
    void testProtectedTest() {
        Authentication mockAuth = Mockito.mock(Authentication.class);
        Mockito.when(mockAuth.getName()).thenReturn("user@test.com");
        SecurityContextHolder.getContext().setAuthentication(mockAuth);

        ResponseEntity<?> response = controller.protectedTest();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("USUARIO : user@test.com ENTRO AL ENDPOINT PROTEGIDO", response.getBody());

        SecurityContextHolder.clearContext();
    }

    @Test
    /**
     * Verifica que el endpoint register registra un nuevo usuario, envía correo de bienvenida y devuelve los datos de login.
     */
    void testRegisterSuccess() {
        LoginRequest request = new LoginRequest("newuser@test.com", "pass123");

        Map<String, Object> fakeResponse = new HashMap<>();
        fakeResponse.put("loginData", new AccountLoginDTO(
                new Cuenta(100, "newuser@test.com", "pass123", "CLIENTE", "ACTIVO", LocalDateTime.now(), "123"),
                "tokenNuevo"));

        Mockito.doNothing().when(welcomeService).enviarBienvenida("newuser@test.com");
        Mockito.doNothing().when(authService).register("newuser@test.com", "pass123");
        Mockito.when(authService.login("newuser@test.com", "pass123")).thenReturn(fakeResponse);

        ResponseEntity<?> response = controller.register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(fakeResponse, response.getBody());
        Mockito.verify(welcomeService).enviarBienvenida("newuser@test.com");
    }

    @Test
    /**
     * Verifica que register devuelve BAD_REQUEST cuando el registro falla (usuario ya existe).
     */
    void testRegisterFail() {
        LoginRequest request = new LoginRequest("exists@test.com", "pass123");

        Mockito.doThrow(new RuntimeException("El usuario ya está registrado"))
                .when(authService).register("exists@test.com", "pass123");

        ResponseEntity<?> response = controller.register(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El usuario ya está registrado", response.getBody());
    }

    @Test
    /**
     * Verifica que logout invalida el token y devuelve un mensaje de confirmación cuando tiene éxito.
     */
    void testLogoutSuccess() {
        String token = "token123";
        String header = "Bearer " + token;

        Mockito.when(authService.extractUsername(token)).thenReturn("user@test.com");
        Mockito.doNothing().when(authService).invalidateToken(token);

        ResponseEntity<?> response = controller.logout(header);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token invalidado, funcionó el logout", response.getBody());
        Mockito.verify(authService).invalidateToken(token);
    }

    @Test
    /**
     * Verifica que logout devuelve BAD_REQUEST cuando el manejo del token falla.
     */
    void testLogoutFail() {
        String token = "badtoken";
        String header = "Bearer " + token;

        Mockito.when(authService.extractUsername(token)).thenThrow(new RuntimeException("Token inválido"));

        ResponseEntity<?> response = controller.logout(header);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error al hacer logout", response.getBody());
    }

    @Test
    /**
     * Verifica que enviarToken devuelve OK y envía un token de recuperación cuando el correo existe.
     *
     * @throws Exception si el envío del token falla durante la preparación del test
     */
    void testEnviarToken_Success() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "exist@test.com");

        Mockito.when(authService.userExists("exist@test.com")).thenReturn(true);
        Mockito.doNothing().when(passwordRecoveryService).generarYEnviarToken("exist@test.com");

        ResponseEntity<String> response = controller.enviarToken(body);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Se envió un token de recuperación al correo exist@test.com", response.getBody());
    }

    @Test
    /**
     * Verifica que enviarToken devuelve NOT_FOUND cuando el correo no está registrado.
     */
    void testEnviarToken_NotFound() {
        Map<String, String> body = new HashMap<>();
        body.put("email", "noexist@test.com");

        Mockito.when(authService.userExists("noexist@test.com")).thenReturn(false);

        ResponseEntity<String> response = controller.enviarToken(body);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No existe ninguna cuenta asociada a este correo", response.getBody());
    }

    @Test
    /**
     * Verifica que enviarToken devuelve INTERNAL_SERVER_ERROR cuando el envío del token falla.
     *
     * @throws Exception si el servicio simulado falla inesperadamente
     */
    void testEnviarToken_InternalError() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "exist2@test.com");

        Mockito.when(authService.userExists("exist2@test.com")).thenReturn(true);
        Mockito.doThrow(new RuntimeException("SMTP error")).when(passwordRecoveryService)
                .generarYEnviarToken("exist2@test.com");

        ResponseEntity<String> response = controller.enviarToken(body);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        org.assertj.core.api.Assertions.assertThat(response.getBody()).contains("Error al enviar token: SMTP error");
    }

    @Test
    /**
     * Verifica que cambiarPassword devuelve UNAUTHORIZED cuando el token proporcionado es inválido.
     */
    void testCambiarPassword_TokenInvalid() {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setEmail("user@test.com");
        req.setToken("tok");
        req.setNuevaPassword("nueva");

        Mockito.when(passwordRecoveryService.validarToken("user@test.com", "tok")).thenReturn(false);

        ResponseEntity<Map<String, Object>> response = controller.cambiarPassword(req);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals(false, body.get("valido"));
    }

    @Test
    /**
     * Verifica que cambiarPassword actualiza la contraseña y devuelve OK cuando el token es válido.
     */
    void testCambiarPassword_Success() {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setEmail("user2@test.com");
        req.setToken("tok2");
        req.setNuevaPassword("nueva2");

        Mockito.when(passwordRecoveryService.validarToken("user2@test.com", "tok2")).thenReturn(true);
        Mockito.doNothing().when(authService).actualizarPassword("user2@test.com", "nueva2");
        Mockito.doNothing().when(passwordRecoveryService).invalidarToken("user2@test.com");

        ResponseEntity<Map<String, Object>> response = controller.cambiarPassword(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals(true, body.get("valido"));
        Mockito.verify(passwordRecoveryService).invalidarToken("user2@test.com");
    }

    @Test
    /**
     * Verifica que cambiarPassword devuelve INTERNAL_SERVER_ERROR cuando la actualización de contraseña falla.
     */
    void testCambiarPassword_ErrorDuringUpdate() {
        PasswordChangeRequest req = new PasswordChangeRequest();
        req.setEmail("user3@test.com");
        req.setToken("tok3");
        req.setNuevaPassword("nueva3");

        Mockito.when(passwordRecoveryService.validarToken("user3@test.com", "tok3")).thenReturn(true);
        Mockito.doThrow(new RuntimeException("DB error")).when(authService).actualizarPassword("user3@test.com", "nueva3");

        ResponseEntity<Map<String, Object>> response = controller.cambiarPassword(req);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertEquals(false, body.get("valido"));
        org.assertj.core.api.Assertions.assertThat((String) body.get("mensaje")).contains("Error al cambiar la contraseña");
    }
}
