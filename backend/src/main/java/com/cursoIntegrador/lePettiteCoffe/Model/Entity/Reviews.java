package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
public class Reviews {

    /**
     * Entidad que representa una reseña (review) de producto o servicio.
     * <p>
     * Incluye nombre, email, cuerpo de la reseña, puntuación y referencia a la cuenta opcional.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreview")
    private Long idReview;

    @Column(length = 100, nullable = false)
    private String nombre;

    @Column(length = 150, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String cuerpo;

    @Column(nullable = false)
    private Integer puntuacion;

    @ManyToOne
    @JoinColumn(name = "idcuenta", referencedColumnName = "idcuenta", nullable = true)
    private Cuenta cuenta;

    @Column(nullable = false)
    private boolean verified;

    public Reviews(String nombre, String email, String cuerpo, Integer puntuacion) {
        /**
         * Crea una instancia de Reviews con los campos obligatorios.
         *
         * @param nombre     nombre del autor de la reseña
         * @param email      correo del autor
         * @param cuerpo     texto de la reseña
         * @param puntuacion puntuación numérica otorgada
         */
        this.nombre = nombre;
        this.email = email;
        this.cuerpo = cuerpo;
        this.puntuacion = puntuacion;
    }
}
