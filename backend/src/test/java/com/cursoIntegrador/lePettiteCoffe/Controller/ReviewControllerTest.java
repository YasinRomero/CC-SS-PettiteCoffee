package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Review.ReviewDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Reviews;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.ReviewService;

@ExtendWith(MockitoExtension.class)
public class ReviewControllerTest {

    /**
     * Pruebas unitarias para ReviewController: creación de reseñas por invitados y usuarios y listado de reseñas.
     */

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController controller;

    private Reviews ejemplo;

    @BeforeEach
    void setUp() {
        ejemplo = new Reviews();
        ejemplo.setIdReview(1L);
        ejemplo.setNombre("Juan");
        ejemplo.setEmail("juan@example.com");
        ejemplo.setCuerpo("Muy buen producto");
        ejemplo.setPuntuacion(5);
    }

    @Test
    /**
     * Verifica que addReviewGuest devuelve OK y el ReviewDTO creado al guardar una reseña de invitado.
     */
    void testAddReviewGuest_Success() {
        ReviewDTO dto = new ReviewDTO(ejemplo);
        when(reviewService.saveReviewGuest(ejemplo)).thenReturn(dto);

        ResponseEntity<?> response = controller.addReviewGuest(ejemplo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(reviewService).saveReviewGuest(ejemplo);
    }

    @Test
    /**
     * Verifica que addReviewGuest devuelve INTERNAL_SERVER_ERROR si ocurre un fallo al guardar.
     */
    void testAddReviewGuest_Exception() {
        when(reviewService.saveReviewGuest(ejemplo)).thenThrow(new RuntimeException("boom"));

        ResponseEntity<?> response = controller.addReviewGuest(ejemplo);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al agregar reseña", response.getBody());
        verify(reviewService).saveReviewGuest(ejemplo);
    }

    @Test
    /**
     * Verifica que addReview para usuarios autenticados devuelve OK con el ReviewDTO creado.
     */
    void testAddReview_Success() {
        CustomUserDetails user = mock(CustomUserDetails.class);
        ReviewDTO dto = new ReviewDTO(ejemplo);

        when(reviewService.saveReviewUser(ejemplo, user)).thenReturn(dto);

        ResponseEntity<?> response = controller.addReview(ejemplo, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
        verify(reviewService).saveReviewUser(ejemplo, user);
    }

    @Test
    /**
     * Verifica que getAllReviews devuelve OK con una lista de ReviewDTO cuando existen reseñas.
     */
    void testGetAllReviews_Success() {
        ReviewDTO dto = new ReviewDTO(ejemplo);
        when(reviewService.getAllReviews()).thenReturn(List.of(dto));

        ResponseEntity<?> response = controller.getAllReviews();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).getAllReviews();
    }

    @Test
    /**
     * Verifica que getAllReviews devuelve INTERNAL_SERVER_ERROR si el servicio lanza una excepción.
     */
    void testGetAllReviews_Exception() {
        when(reviewService.getAllReviews()).thenThrow(new RuntimeException("err"));

        ResponseEntity<?> response = controller.getAllReviews();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(reviewService).getAllReviews();
    }
}
