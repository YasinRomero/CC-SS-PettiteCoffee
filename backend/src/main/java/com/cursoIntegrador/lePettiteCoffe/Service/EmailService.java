package com.cursoIntegrador.lePettiteCoffe.Service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Util.WordGenerator;

/**
 * Servicio encargado del envío de correos electrónicos.
 * Utiliza la librería Apache Commons Email para enviar mensajes con o sin adjuntos,
 * como tokens de recuperación y correos de bienvenida.
 */
@Service
@PropertySource("classpath:secret-credentials.properties")
public class EmailService {

    @Value("${email.sender}")
    private String EMAIL_EMISOR;

    @Value("${email.password}")
    private String PASSWORD_EMISOR;

    /**
     * Configura los parámetros básicos del objeto MultiPartEmail (host, puerto, autenticación).
     *
     * @return Un objeto MultiPartEmail configurado y listo para ser utilizado.
     */
    private MultiPartEmail configurarEmail() throws EmailException {
        MultiPartEmail email = new MultiPartEmail();
        email.setHostName("smtp.gmail.com");
        email.setSmtpPort(587);
        email.setAuthentication(EMAIL_EMISOR, PASSWORD_EMISOR);
        email.setStartTLSEnabled(true);
        email.setFrom(EMAIL_EMISOR);
        return email;
    }

    /**
     * Envía un correo electrónico que contiene un token de recuperación de contraseña.
     * Adjunta un documento generado automáticamente con los detalles de recuperación.
     *
     * @param destinatario La dirección de correo electrónico a la que se enviará el token.
     * @param token El token de recuperación de contraseña.
     */
    public void enviarTokenDeRecuperacion(String destinatario, String token) throws EmailException {
        File pdf;

        try {
            pdf = WordGenerator.generarPdfRecuperacion(destinatario, token);
        } catch (IOException e) {
            throw new RuntimeException("Error generando el PDF de recuperación", e);
        }

        MultiPartEmail email = configurarEmail();
        email.setSubject("Recuperación de contraseña - Le Pettite Coffee");
        email.setMsg("Tu token de recuperación es: " + token
                + "\nVálido por 10 minutos. \nPégalo en el formulario de recuperación de contraseña."
                + "\nSi no solicitaste este correo, ignóralo y cambia tu contraseña por seguridad.");
        email.addTo(destinatario);

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(pdf.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Detalles de recuperación de contraseña");
        attachment.setName("Recuperacion_LePettiteCoffee.docx");

        email.attach(attachment);
        email.send();

        pdf.deleteOnExit();
    }

    /**
     * Envía un correo electrónico de bienvenida a un nuevo usuario registrado.
     * Adjunta un documento generado automáticamente como carta de bienvenida.
     *
     * @param destinatario La dirección de correo electrónico del nuevo usuario.
     */
    public void enviarCorreoBienvenida(String destinatario) throws EmailException {
        File pdf;

        try {
            pdf = WordGenerator.generarPdfBienvenida(destinatario);
        } catch (IOException e) {
            throw new RuntimeException("Error generando el PDF de bienvenida", e);
        }

        MultiPartEmail email = configurarEmail();
        email.setSubject("¡Bienvenido a Le Pettite Coffee!");
        email.setMsg("Hola " + destinatario + "\n\n"
                + "¡Gracias por unirte a nuestra comunidad de amantes del café!\n"
                + "Adjunto encontrarás tu carta de bienvenida.\n\n"
                + "El equipo de Le Pettite Coffee te desea una excelente experiencia");
        email.addTo(destinatario);

        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(pdf.getAbsolutePath());
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        attachment.setDescription("Carta de bienvenida - Le Pettite Coffee");
        attachment.setName("Bienvenida_LePettiteCoffee.docx");

        email.attach(attachment);
        email.send();

        pdf.deleteOnExit();
    }
}
