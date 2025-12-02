package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Review;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Reviews;

import lombok.Data;

@Data
public class ReviewDTO {

    /**
     * DTO que representa una reseña (review) para transferencia entre capas.
     * <p>
     * Contiene los campos mostrados al cliente o usados por la API para representar una reseña.
     */

    private Long idReview;

    private String nombre;

    private String email;

    private String cuerpo;

    private Integer puntuacion;

    private boolean verified;

    public ReviewDTO(Reviews review) {
        /**
         * Constructor que crea un ReviewDTO a partir de una entidad Reviews.
         *
         * @param review entidad Reviews desde la cual se mapean los campos
         */
        this.idReview = review.getIdReview();
        this.nombre = review.getNombre();
        this.email = review.getEmail();
        this.cuerpo = review.getCuerpo();
        this.puntuacion = review.getPuntuacion();
        this.verified = review.isVerified();
    }

}
