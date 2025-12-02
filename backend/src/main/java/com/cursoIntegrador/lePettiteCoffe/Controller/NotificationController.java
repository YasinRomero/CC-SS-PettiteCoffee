package com.cursoIntegrador.lePettiteCoffe.Controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Notifications.NotificationDTO;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.NotificationService;

@RequestMapping("/notificaciones")
@RestController
/**
 * Controlador para gestionar las notificaciones de usuario.
 * <p>
 * Proporciona endpoint para listar las notificaciones del usuario autenticado.
 */
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/listar")
    @PreAuthorize("isAuthenticated()")
    /**
     * Lista las notificaciones del usuario autenticado.
     *
     * @param principal información del usuario autenticado (principal)
     * @return ResponseEntity con la lista de NotificationDTO, ResponseEntity.noContent()
     *         (204) si no hay notificaciones, o ResponseEntity 500 en caso de error
     */
    public ResponseEntity<?> listarNotificaciones(Principal principal) {
        try {
            String email = principal.getName();
            logger.info("Buscando notificaciones del email: {}", email);

            List<NotificationDTO> notificaciones = notificationService.getAllUserNotis(email);
            logger.info("Notificaciones encontradas: {}", notificaciones.size());

            if (notificaciones.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(notificaciones);

        } catch (Exception e) {
            logger.error("Error al listar notificaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al listar notificaciones");
        }
    }

}
