package com.cursoIntegrador.lePettiteCoffe.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la lógica de recuperación de contraseña.
 * Coordina la generación, almacenamiento (cache) y validación de tokens
 * temporales para la recuperación de contraseñas.
 */
@Service
public class PasswordRecoveryService {
    @Autowired
    private TokenCache tokenCache;
    @Autowired
    private EmailService emailService;

    /**
     * Genera un token UUID, lo guarda en la caché asociado al email y lo envía
     * al usuario por correo electrónico.
     *
     * @param email La dirección de correo electrónico del usuario que solicita la recuperación.
     */
    public void generarYEnviarToken(String email) throws Exception {
        String token = java.util.UUID.randomUUID().toString();
        tokenCache.guardarToken(email, token);
        emailService.enviarTokenDeRecuperacion(email, token);
    }

    /**
     * Valida si el token ingresado por el usuario coincide con el token almacenado
     * y no ha expirado.
     *
     * @param email La dirección de correo electrónico asociada al token.
     * @param tokenIngresado El token proporcionado por el usuario.
     * @return true si el token ingresado es válido y coincide, false en caso contrario.
     */
    public boolean validarToken(String email, String tokenIngresado) {
        String tokenGuardado = tokenCache.obtenerToken(email);
        return tokenGuardado != null && tokenGuardado.equals(tokenIngresado);
    }

    /**
     * Elimina el token de recuperación de la caché para invalidarlo,
     * usualmente después de que la contraseña ha sido cambiada.
     *
     * @param email La dirección de correo electrónico asociada al token a invalidar.
     */
    public void invalidarToken(String email) {
        tokenCache.eliminarToken(email);
    }
}
