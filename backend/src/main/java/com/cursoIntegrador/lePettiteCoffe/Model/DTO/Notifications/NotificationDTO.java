package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Notifications;

import java.time.LocalDateTime;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Notification;

import lombok.Data;

@Data
public class NotificationDTO {

    /**
     * DTO para transferir notificaciones al cliente o capas superiores.
     */

    private Long idNotificaciones;

    private String asunto;

    private String descripcion;

    private LocalDateTime fechaHoraEnvio;

    public NotificationDTO(Notification notification) {
        /**
         * Crea un NotificationDTO a partir de la entidad Notification.
         *
         * @param notification entidad Notification que contiene los datos originales
         */
        this.idNotificaciones = notification.getIdNotificaciones();
        this.asunto = notification.getAsunto();
        this.descripcion = notification.getDescripcion();
        this.fechaHoraEnvio = notification.getFechaHoraEnvio();
    }

}
