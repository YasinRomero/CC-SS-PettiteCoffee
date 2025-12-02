package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;

import lombok.Data;

@Data
public class AccountListDTO {

    /**
     * DTO para listar información pública de una cuenta (sin credenciales).
     * <p>
     * Incluye identificador, email, rol, estado y datos básicos de perfil.
     */

    private Integer idcuenta;
    private String email;
    private String rol;
    private String estado;
    private LocalDateTime fechaRegistro;
    private String alias;
    private String direccion;
    private String pais;
    private LocalDate fechaNacimiento;
    private String telefono;

    public AccountListDTO(Cuenta cuenta) {
        /**
         * Crea un AccountListDTO mapeando desde la entidad Cuenta.
         *
         * @param cuenta entidad Cuenta desde la cual se extraen los campos para la lista
         */
        this.idcuenta = cuenta.getIdcuenta();
        this.email = cuenta.getEmail();
        this.rol = cuenta.getRol();
        this.estado = cuenta.getEstado();
        this.fechaRegistro = cuenta.getFechaRegistro();
        this.alias = cuenta.getAlias();
        this.direccion = cuenta.getDireccion();
        this.pais = cuenta.getPais();
        this.fechaNacimiento = cuenta.getFechaNacimiento();
        this.telefono = cuenta.getTelefono();
    }

}
