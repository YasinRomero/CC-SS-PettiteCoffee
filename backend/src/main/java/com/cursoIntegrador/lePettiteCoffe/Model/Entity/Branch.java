package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sucursal")
public class Branch {

    /**
     * Entidad que representa una sucursal física o punto de venta.
     * <p>
     * Contiene identificador, nombre y dirección de la sucursal.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Idsucursal;

    @NotBlank(message = "El nombre de la sucursal no puede estar vacío")
    @Size(max = 100, message = "El nombre de la sucursal no puede superar los 100 caracteres")
    @Column(length = 100, nullable = false)
    private String Nombre;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
    @Column(length = 200, nullable = false)
    private String Direccion;
}
