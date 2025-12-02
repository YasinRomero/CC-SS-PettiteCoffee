package com.cursoIntegrador.lePettiteCoffe.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Notification;

/**
 * Repositorio para gestionar las operaciones de la entidad {@link Notification}.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /**
     * Obtiene la lista de notificaciones asociadas a una cuenta mediante su email.
     *
     * @param email el correo electrónico de la cuenta.
     * @return una lista de notificaciones relacionadas con la cuenta indicada.
     */
    List<Notification> findByCuentaEmail(String email);
}
