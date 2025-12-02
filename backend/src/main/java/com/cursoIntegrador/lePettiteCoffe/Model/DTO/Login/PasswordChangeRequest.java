package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {

    /**
     * DTO para solicitudes de cambio de contraseña usando un token de recuperación.
     *
     * Contiene el email, el token de recuperación y la nueva contraseña deseada.
     */
    private String email;
    private String token;
    private String nuevaPassword;
}
