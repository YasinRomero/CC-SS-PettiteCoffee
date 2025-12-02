package com.cursoIntegrador.lePettiteCoffe.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Review.ReviewDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Reviews;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.ReviewService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
/**
 * Controlador que expone endpoints para gestionar reseñas (reviews).
 * <p>
 * Permite crear reseñas como invitado o como usuario autenticado y listar reseñas.
 */
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    final ReviewService reviewService;

    @PostMapping("/addReviewGuest")
    /**
     * Crea una reseña enviada por un invitado (no autenticado).
     *
     * @param review entidad Reviews con los datos de la reseña proporcionada por el invitado
     * @return ResponseEntity con el ReviewDTO creado si se guarda correctamente, o
     *         un ResponseEntity con estado 500 en caso de error
     */
    public ResponseEntity<?> addReviewGuest(@RequestBody Reviews review) {

        logger.info("Intento de agregar reseña como invitado de: {}", review.getEmail());

        try {
            ReviewDTO newReview = reviewService.saveReviewGuest(review);
            logger.info("Review de invitado agregado por : {}", review.getEmail());
            return ResponseEntity.ok(newReview);
        } catch (RuntimeException e) {
            logger.error("Error al agregar reseña de invitado: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al agregar reseña");
        }

    }

        @PostMapping("/addReview")
        /**
         * Crea una reseña para un usuario autenticado.
         *
         * @param review      entidad Reviews con los datos de la reseña
         * @param userDetails información del usuario autenticado
         * @return ResponseEntity con el ReviewDTO creado si se guarda correctamente, o
         *         un ResponseEntity con estado 500 en caso de error
         */
        public ResponseEntity<?> addReview(@RequestBody Reviews review,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        logger.info("Intento de agregar reseña de: {}", review.getEmail());

        try {
            ReviewDTO newReview = reviewService.saveReviewUser(review, userDetails);
            logger.info("Review agregado por : {}", review.getEmail());
            return ResponseEntity.ok(newReview);
        } catch (RuntimeException e) {
            logger.error("Error al agregar reseña : {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al agregar reseña");
        }

    }

    @GetMapping("/getReviews")
    /**
     * Devuelve la lista completa de reseñas en el sistema.
     *
     * @return ResponseEntity con la lista de ReviewDTO si la operación es exitosa,
     *         o ResponseEntity con estado 500 en caso de error
     */
    public ResponseEntity<?> getAllReviews() {
        logger.info("Intento de solicitar lista de reviews");

        try {

            List<ReviewDTO> reviews = reviewService.getAllReviews();
            logger.info("Todas las reviews obtenidas");
            return ResponseEntity.ok(reviews);

        } catch (RuntimeException e) {

            logger.error("Error al listar reviews: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);

        }

    }

}
