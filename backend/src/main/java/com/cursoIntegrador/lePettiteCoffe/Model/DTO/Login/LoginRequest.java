package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {

    /**
     * DTO para solicitudes de login que contiene las credenciales de usuario.
     */
    private String username;
    private String password;
}
