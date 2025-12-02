package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Review.ReviewDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Reviews;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Repository.ReviewsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de reseñas (Reviews).
 * Proporciona métodos para listar todas las reseñas y guardar nuevas reseñas de invitados o usuarios registrados.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    @Autowired
    private final ReviewsRepository reviewRepo;

    /**
     * Obtiene una lista de todas las reseñas existentes y las convierte a DTOs.
     *
     * @return Una lista de objetos ReviewDTO con la información de las reseñas.
     */
    public List<ReviewDTO> getAllReviews() {
        List<Reviews> original = reviewRepo.findAll();
        List<ReviewDTO> dtoList = new ArrayList<>();

        for (Reviews reviewDTO : original) {
            ReviewDTO element = new ReviewDTO(reviewDTO);
            dtoList.add(element);
        }

        return dtoList;
    }

    /**
     * Guarda una nueva reseña realizada por un invitado (no autenticado).
     * Establece la cuenta a nula y la verificación a falsa.
     *
     * @param review La entidad Reviews que contiene los datos de la reseña del invitado.
     * @return Un objeto ReviewDTO que representa la reseña guardada.
     */
    public ReviewDTO saveReviewGuest(Reviews review) {
        review.setCuenta(null);
        review.setVerified(false);
        reviewRepo.save(review);

        ReviewDTO reviewDTO = new ReviewDTO(review);
        return reviewDTO;
    }

    /**
     * Guarda una nueva reseña realizada por un usuario autenticado.
     * Asocia la reseña a la cuenta del usuario, establece el email, nombre (alias) y la marca como verificada.
     *
     * @param review La entidad Reviews que contiene los datos de la reseña.
     * @param userDetails Los detalles del usuario autenticado que realiza la reseña.
     * @return Un objeto ReviewDTO que representa la reseña guardada.
     */
    public ReviewDTO saveReviewUser(Reviews review, CustomUserDetails userDetails) {
        Cuenta cuenta = userDetails.getCuenta();

        review.setCuenta(cuenta);
        review.setEmail(cuenta.getEmail());
        review.setNombre(cuenta.getAlias());
        review.setVerified(true);
        reviewRepo.save(review);

        ReviewDTO reviewDTO = new ReviewDTO(review);
        return reviewDTO;
    }

}
