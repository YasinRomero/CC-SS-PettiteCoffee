package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notificaciones")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

    /**
     * Entidad que representa una notificación en el sistema dirigida a un usuario.
     * <p>
     * Incluye asunto, descripción, fecha de envío y la cuenta a la que pertenece.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnotificaciones")
    private Long idNotificaciones;

    @ManyToOne
    @JoinColumn(name = "idcuenta", referencedColumnName = "idcuenta", nullable = true)
    private Cuenta cuenta;

    @NotBlank(message = "El asunto no puede estar vacío")
    @Size(max = 50, message = "El asunto no puede tener más de 50 caracteres")
    @Column(length = 50, nullable = false)
    private String asunto;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 300, message = "La descripción no puede tener más de 300 caracteres")
    @Column(length = 300, nullable = false)
    private String descripcion;

    @PastOrPresent(message = "La fecha de envío no puede ser futura")
    @Column(name = "fechahoraenvio")
    private LocalDateTime fechaHoraEnvio;

}
