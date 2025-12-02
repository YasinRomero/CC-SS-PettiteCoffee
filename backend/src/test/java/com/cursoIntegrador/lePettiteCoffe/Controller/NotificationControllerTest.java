package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.anyString;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Notifications.NotificationDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Notification;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.NotificationService;

/**
 * Clase de prueba unitaria para {@link NotificationController}.
 * Simula las interacciones del controlador con el servicio y verifica
 * las respuestas HTTP esperadas en diferentes escenarios.
 */
@ExtendWith(MockitoExtension.class)
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private Principal principal;

    @InjectMocks
    private NotificationController notificationController;

    private static final String USER_EMAIL = "test_user@lePettite.com";
    private NotificationDTO sampleNotificationDTO;

    /**
     * Configura el entorno de prueba, inicializando una notificación de ejemplo
     * antes de cada test.
     */
    @BeforeEach
    void setUp() {
        Notification notification = new Notification();
        notification.setIdNotificaciones(1L);
        notification.setAsunto("Pedido Aprobado");
        notification.setDescripcion("Tu pedido fue aprobado.");
        notification.setFechaHoraEnvio(LocalDateTime.now());

        sampleNotificationDTO = new NotificationDTO(notification);
    }

    /**
     * Prueba el escenario exitoso donde se encuentran notificaciones
     * y se espera un código de estado 200 OK con la lista de datos.
     *
     * @return ResponseEntity<?> La respuesta HTTP con la lista de DTOs.
     */
    @Test
    @DisplayName("Debe devolver 200 OK y una lista de notificaciones")
    void listarNotificaciones_conDatos_debeDevolver200YLista() {
        when(principal.getName()).thenReturn(USER_EMAIL);
        List<NotificationDTO> mockList = List.of(sampleNotificationDTO);
        
        when(notificationService.getAllUserNotis(USER_EMAIL))
            .thenReturn(mockList);

        ResponseEntity<?> response = notificationController.listarNotificaciones(principal);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        List<NotificationDTO> resultList = (List<NotificationDTO>) response.getBody();
        assertEquals(1, resultList.size());

        verify(notificationService).getAllUserNotis(USER_EMAIL);
        verify(principal).getName();
    }

    /**
     * Prueba el escenario donde el servicio devuelve una lista vacía
     * y se espera un código de estado 204 No Content.
     *
     * @return ResponseEntity<?> La respuesta HTTP con el código 204.
     */
    @Test
    @DisplayName("Debe devolver 204 No Content cuando no hay notificaciones")
    void listarNotificaciones_listaVacia_debeDevolver204NoContent() {
        when(principal.getName()).thenReturn(USER_EMAIL);
        when(notificationService.getAllUserNotis(USER_EMAIL))
            .thenReturn(Collections.emptyList());

        ResponseEntity<?> response = notificationController.listarNotificaciones(principal);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(notificationService).getAllUserNotis(USER_EMAIL);
        verify(principal).getName();
    }

    /**
     * Prueba el manejo de excepciones del servicio, esperando un
     * código de estado 500 Internal Server Error.
     *
     * @return ResponseEntity<?> La respuesta HTTP con el código 500 y un mensaje de error.
     */
    @Test
    @DisplayName("Debe devolver 500 Internal Server Error si el servicio lanza una excepción")
    void listarNotificaciones_excepcionServicio_debeDevolver500() {

        when(principal.getName()).thenReturn(USER_EMAIL);
        when(notificationService.getAllUserNotis(USER_EMAIL))
            .thenThrow(new RuntimeException("Error simulado"));

        ResponseEntity<?> response = notificationController.listarNotificaciones(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al listar notificaciones", response.getBody());
        verify(notificationService).getAllUserNotis(USER_EMAIL);
        verify(principal).getName();
    }

    /**
     * Prueba el manejo de excepciones al intentar obtener el nombre del Principal,
     * esperando un código de estado 500.
     *
     * @return ResponseEntity<?> La respuesta HTTP con el código 500 y un mensaje de error.
     */
    @Test
    @DisplayName("Debe devolver 500 si Principal falla")
    void listarNotificaciones_principalNameFalla_debeDevolver500() {
        when(principal.getName()).thenThrow(new RuntimeException("Error"));

        ResponseEntity<?> response = notificationController.listarNotificaciones(principal);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al listar notificaciones", response.getBody());
        verify(notificationService, never()).getAllUserNotis(anyString());
        verify(principal).getName();
    }

    /**
     * Prueba el caso donde el objeto Principal es nulo, esperando un
     * código de estado 500.
     *
     * @return ResponseEntity<?> La respuesta HTTP con el código 500 y un mensaje de error.
     */
    @Test
    @DisplayName("Debe devolver 500 cuando el Principal es nulo")
    void listarNotificaciones_principalNulo_debeDevolver500() {
        ResponseEntity<?> response = notificationController.listarNotificaciones(null);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al listar notificaciones", response.getBody());
        verify(notificationService, never()).getAllUserNotis(anyString());
    }
}