package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountUpdateDTO {

    /**
     * DTO usado para actualizar datos opcionales del perfil de una cuenta.
     * <p>
     * Incluye alias, dirección, país, fecha de nacimiento y teléfono.
     */
    private String alias;
    private String direccion;
    private String pais;
    private LocalDate fechaNacimiento;
    private String telefono;
}