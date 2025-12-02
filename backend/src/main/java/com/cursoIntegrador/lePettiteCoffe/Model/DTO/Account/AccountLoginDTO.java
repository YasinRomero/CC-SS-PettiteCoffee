package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;

import lombok.Data;

@Data
public class AccountLoginDTO {

    /**
     * DTO devuelto al autenticarse: contiene información de la cuenta y token.
     * <p>
     * Incluye datos de perfil (alias, dirección, país, teléfono), estado/rol y el token JWT.
     */

    private String email;
    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;

    private String alias;
    private String direccion;
    private String pais;
    private LocalDate fechaNacimiento;
    private String telefono;

    private String token;

    public AccountLoginDTO(Cuenta cuenta, String token) {
        /**
         * Construye un AccountLoginDTO a partir de la entidad Cuenta y el token.
         *
         * @param cuenta entidad Cuenta con información del usuario
         * @param token  token de sesión/ autenticación asignado al usuario
         */
        this.email = cuenta.getEmail();
        this.rol = cuenta.getRol();
        this.estado = cuenta.getEstado();
        this.fechaRegistro = cuenta.getFechaRegistro();

        this.alias = cuenta.getAlias();
        this.direccion = cuenta.getDireccion();
        this.pais = cuenta.getPais();
        this.fechaNacimiento = cuenta.getFechaNacimiento();
        this.telefono = cuenta.getTelefono();

        this.token = token;
    }

}
