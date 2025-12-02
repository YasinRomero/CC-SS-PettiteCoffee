package com.cursoIntegrador.lePettiteCoffe.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login.LoginRequest;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login.PasswordChangeRequest;
import com.cursoIntegrador.lePettiteCoffe.Service.AuthService;
import com.cursoIntegrador.lePettiteCoffe.Service.PasswordRecoveryService;
import com.cursoIntegrador.lePettiteCoffe.Service.WelcomeService;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Controlador de autenticación y gestión de cuentas de usuario.
     * <p>
     * Integra login, registro, logout, recuperación y cambio de contraseña.
     */

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordRecoveryService passwordRecoveryService;

    @Autowired
    private WelcomeService welcomeService;

    @PostMapping("/login")
    /**
     * Realiza la autenticación de un usuario con credenciales.
     *
     * @param loginRequest DTO con username (email) y password
     * @return ResponseEntity con los datos de sesión / token al autenticarse correctamente,
     *         o 401 si las credenciales son inválidas
     */
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        String email = loginRequest.getUsername();
        logger.info("Intento de login para: {}", email);
        try {
            var response = authService.login(email, loginRequest.getPassword());
            logger.debug("Login exitoso para: {}", email);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Login fallido para: {} - {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @GetMapping("/protectedTest")
    @PreAuthorize("isAuthenticated()")
    /**
     * Endpoint de prueba protegido para usuarios autenticados.
     *
     * @return ResponseEntity con un mensaje que incluye el nombre del usuario autenticado
     */
    public ResponseEntity<?> protectedTest() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("USUARIO : " + username + " ENTRO AL ENDPOINT PROTEGIDO");
    }

    @PostMapping("/register")
    /**
     * Registra una nueva cuenta y retorna los datos de sesión.
     *
     * @param loginRequest DTO con username (email) y password para registrar la cuenta
     * @return ResponseEntity con los datos de sesión al registrar correctamente,
     *         o 400 si el registro falla
     */
    public ResponseEntity<?> register(@RequestBody LoginRequest loginRequest) {

        String email = loginRequest.getUsername();
        logger.info("Intento de registro para: {}", email);

        try {
            authService.register(email, loginRequest.getPassword());
            logger.debug("Registro exitoso para: {}", email);
            welcomeService.enviarBienvenida(email);
            Map<String, Object> response = authService.login(email, loginRequest.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Error en registro de {} - {}", email, e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    /**
     * Invalida el token de autenticación enviado en la cabecera Authorization.
     *
     * @param authHeader cabecera Authorization que contiene el token Bearer
     * @return ResponseEntity con mensaje de confirmación o estado 400 en caso de error
     */
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String username = authService.extractUsername(token);
            authService.invalidateToken(token);
            logger.info("Logout exitoso para: {}", username);
            return ResponseEntity.ok("Token invalidado, funcionó el logout");
        } catch (RuntimeException e) {
            logger.error("Error al hacer logout: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body("Error al hacer logout");
        }
    }

    @PostMapping("/recuperar")
    /**
     * Genera y envía un token de recuperación de contraseña al correo proporcionado.
     *
     * @param body Mapa que debe contener la clave "email" con la dirección de correo
     * @return ResponseEntity con mensaje de éxito si se envía el token o estados 404/500 según el caso
     */
    public ResponseEntity<String> enviarToken(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        logger.info("Solicitud de recuperación de contraseña para: {}", email);

        if (!authService.userExists(email)) {
            logger.warn("Intento de recuperación de contraseña para email no registrado: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe ninguna cuenta asociada a este correo");
        }

        try {
            passwordRecoveryService.generarYEnviarToken(email);
            logger.debug("Token de recuperación generado y enviado a {}", email);
            return ResponseEntity.ok("Se envió un token de recuperación al correo " + email);
        } catch (Exception e) {
            logger.error("Error al enviar token de recuperación a {} - {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al enviar token: " + e.getMessage());
        }
    }

    @PostMapping("/cambiar-password")
    /**
     * Cambia la contraseña de una cuenta usando un token válido de recuperación.
     *
     * @param request DTO PasswordChangeRequest con email, token y nueva password
     * @return ResponseEntity con un mapa indicando si el cambio fue válido y un mensaje descriptivo
     */
    public ResponseEntity<Map<String, Object>> cambiarPassword(@RequestBody PasswordChangeRequest request) {

        String email = request.getEmail();
        logger.info("Intento de cambio de contraseña para: {}", email);
        Map<String, Object> respuesta = new HashMap<>();
        boolean valido = passwordRecoveryService.validarToken(email, request.getToken());

        if (!valido) {
            logger.warn("Token inválido o expirado en intento de cambio de contraseña para: {}", email);
            respuesta.put("valido", false);
            respuesta.put("mensaje", "Token inválido o expirado, no se cambió la contraseña");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }

        try {
            authService.actualizarPassword(email, request.getNuevaPassword());
            passwordRecoveryService.invalidarToken(email);

            logger.debug("Contraseña actualizada correctamente para: {}", email);
            respuesta.put("valido", true);
            respuesta.put("mensaje", "Contraseña actualizada correctamente");
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            logger.error("Error al cambiar la contraseña para {} - {}", email, e.getMessage(), e);
            respuesta.put("valido", false);
            respuesta.put("mensaje", "Error al cambiar la contraseña: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}
