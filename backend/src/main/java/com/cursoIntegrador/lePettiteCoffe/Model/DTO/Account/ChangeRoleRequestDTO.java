package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeRoleRequestDTO {

    /**
     * DTO para solicitudes de cambio de rol en una cuenta.
     *
     * @param idcuenta identificador de la cuenta a modificar
     * @param rol      nuevo rol a asignar a la cuenta
     */

    private Integer idcuenta;
    private String rol;
}
