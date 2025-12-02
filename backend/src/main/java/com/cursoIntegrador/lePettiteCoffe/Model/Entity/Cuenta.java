package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta")
@Data
@NoArgsConstructor
public class Cuenta {

    /**
     * Entidad que representa una cuenta de usuario en el sistema.
     * <p>
     * Contiene información de autenticación y datos de perfil básicos.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcuenta")
    private Integer idcuenta;

    @Email(message = "El correo debe tener un formato válido (ejemplo@dominio.com)")
    @NotBlank(message = "El correo no puede estar vacío")
    @Column(length = 150, nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(length = 180, nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String rol;

    @Column(length = 20, nullable = false)
    private String estado;

    @Column(name = "fecharegistro")
    private LocalDateTime fechaRegistro;

    @Column(name = "alias", nullable = true)
    private String alias;

    @Column(name = "direccion", length = 200, nullable = true)
    private String direccion = "No establecida";

    @Column(name = "fechanacimiento", nullable = true)
    private LocalDate fechaNacimiento;

    @Column(name = "pais", length = 100, nullable = true)
    private String pais = "No establecido";

    @Column(name = "telefono", length = 12, nullable = true)
    private String telefono = "No establecido";

        /**
         * Constructor principal para la entidad Cuenta.
         *
         * @param idcuenta      identificador de la cuenta (puede ser null para nuevas cuentas)
         * @param email         correo electrónico asociado a la cuenta
         * @param password      contraseña (normalmente cifrada)
         * @param rol           rol del usuario (p.ej. ADMIN, USER)
         * @param estado        estado de la cuenta (p.ej. activo, inactivo)
         * @param fechaRegistro fecha y hora en que se registró la cuenta
         * @param telefono      número de teléfono asociado a la cuenta
         */
        public Cuenta(Integer idcuenta, String email, String password, String rol, String estado,
            LocalDateTime fechaRegistro, String telefono) {
        this.idcuenta = idcuenta;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.alias = "Nameless User";
        this.direccion = "No establecida";
        this.pais = "No establecido";
        this.fechaNacimiento = null;
        this.telefono = "No establecido";
    }

}
