package com.cursoIntegrador.lePettiteCoffe.Service;

import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de gestionar las acciones de bienvenida a nuevos usuarios,
 * como el envío de correos de bienvenida.
 */
@Service
public class WelcomeService {

    @Autowired
    private EmailService emailService;

    /**
     * Envía un correo electrónico de bienvenida a la dirección especificada.
     * Si ocurre un error al enviar el correo, este se registra en la consola de errores.
     *
     * @param email La dirección de correo electrónico a la que se enviará la bienvenida.
     */
    public void enviarBienvenida(String email) {
        try {
            emailService.enviarCorreoBienvenida(email);
        } catch (EmailException e) {
            System.err.println("No se pudo enviar correo de bienvenida a " + email + ": " + e.getMessage());
        }
    }
}