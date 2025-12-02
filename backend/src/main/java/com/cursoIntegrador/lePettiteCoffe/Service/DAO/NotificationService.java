package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Notifications.NotificationDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Notification;
import com.cursoIntegrador.lePettiteCoffe.Repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de notificaciones.
 * Proporciona métodos para recuperar notificaciones asociadas a un usuario.
 */
@RequiredArgsConstructor
@Service
public class NotificationService {

    @Autowired
    private final NotificationRepository notiRepo;

    /**
     * Obtiene todas las notificaciones de un usuario específico, dado su correo electrónico.
     * Convierte las entidades Notification a objetos DTO para su transferencia.
     *
     * @param email El correo electrónico del usuario cuyas notificaciones se desean recuperar.
     * @return Una lista de objetos NotificationDTO que representan las notificaciones del usuario.
     */
    public List<NotificationDTO> getAllUserNotis(String email) {
        List<Notification> notis = notiRepo.findByCuentaEmail(email);
        List<NotificationDTO> notisDTO = new ArrayList<>();

        for (Notification noti : notis) {
            NotificationDTO tempoNotiDTO = new NotificationDTO(noti);
            notisDTO.add(tempoNotiDTO);
        }
        return notisDTO;
    }
}
