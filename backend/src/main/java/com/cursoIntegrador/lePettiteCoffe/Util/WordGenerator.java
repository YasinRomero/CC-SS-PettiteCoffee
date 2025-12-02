package com.cursoIntegrador.lePettiteCoffe.Util;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.time.LocalDateTime;
import java.net.URI;
import java.time.format.DateTimeFormatter;

/**
 * Clase de utilidad para generar documentos de Microsoft Word (.docx)
 * con formatos predefinidos para correos de bienvenida y recuperación de contraseña,
 * utilizando la librería Apache POI XWPF.
 */
public class WordGenerator {

    /**
     * Genera un documento Word con el contenido de bienvenida para un nuevo usuario.
     * El documento incluye el logo de la cafetería, un mensaje de bienvenida personalizado
     * con el email del usuario y la fecha de registro.
     *
     * @param email La dirección de correo electrónico del usuario para personalizar el saludo.
     * @return Un objeto File que representa el archivo .docx temporal generado.
     */
    public static File generarPdfBienvenida(String email) throws IOException {
        File pdfFile = File.createTempFile("Bienvenida_", ".docx");

        try (FileOutputStream out = new FileOutputStream(pdfFile);
                XWPFDocument document = new XWPFDocument()) {

            XWPFParagraph imageParagraph = document.createParagraph();
            imageParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imageRun = imageParagraph.createRun();

            try (InputStream imageStream = URI.create(
                    "https://images.vexels.com/media/users/3/305727/isolated/preview/4695ea7b3f321f8cfc8c7ad0a5577133-circulo-de-cafe.png")
                    .toURL().openStream()) {
                imageRun.addPicture(
                        imageStream,
                        Document.PICTURE_TYPE_PNG,
                        "logo.png",
                        Units.toEMU(120),
                        Units.toEMU(120));
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen: " + e.getMessage());
            }

            imageRun.addBreak();
            imageRun.addBreak();
            imageRun.addBreak();

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("¡Bienvenido a Le Pettite Coffee!");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            document.createParagraph().createRun().addBreak();

            XWPFParagraph body = document.createParagraph();
            XWPFRun bodyRun = body.createRun();

            bodyRun.setText("Hola, " + email + " ☕");
            bodyRun.addBreak();
            bodyRun.setText("¡Gracias por unirte a nuestra comunidad de amantes del café!");
            bodyRun.addBreak();
            bodyRun.addBreak();

            bodyRun.setText(
                    "En Le Pettite Coffee, nos encanta ofrecerte una experiencia única llena de aroma y sabor.");
            bodyRun.addBreak();
            bodyRun.setText("Desde ahora podrás disfrutar de nuestros productos, promociones y novedades especiales.");
            bodyRun.addBreak();
            bodyRun.addBreak();

            bodyRun.setText(
                    "Recuerda que puedes iniciar sesión con tu cuenta registrada para acceder a todas las funciones.");
            bodyRun.addBreak();
            bodyRun.addBreak();

            bodyRun.setBold(true);
            bodyRun.setText("¡Nos alegra tenerte con nosotros!");
            bodyRun.setBold(false);
            bodyRun.addBreak();
            bodyRun.addBreak();

            bodyRun.setText("Fecha de registro: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            bodyRun.addBreak();
            bodyRun.addBreak();

            bodyRun.setBold(true);
            bodyRun.setText("Con cariño, el equipo de Le Pettite Coffee ☕");
            bodyRun.setBold(false);

            document.write(out);
        }

        return pdfFile;
    }

    /**
     * Genera un documento Word con el contenido de recuperación de contraseña para un usuario.
     * El documento incluye el logo de la cafetería, el email del usuario y el token de recuperación generado.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @param token El token único generado para la recuperación de contraseña.
     * @return Un objeto File que representa el archivo .docx temporal generado.
     */
    public static File generarPdfRecuperacion(String email, String token) throws IOException {
        File pdfFile = File.createTempFile("Recuperacion_", ".docx");

        try (FileOutputStream out = new FileOutputStream(pdfFile);
                XWPFDocument document = new XWPFDocument()) {

            XWPFParagraph imageParagraph = document.createParagraph();
            imageParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imageRun = imageParagraph.createRun();

            try (InputStream imageStream = URI.create(
                    "https://images.vexels.com/media/users/3/305727/isolated/preview/4695ea7b3f321f8cfc8c7ad0a5577133-circulo-de-cafe.png")
                    .toURL().openStream()) {
                imageRun.addPicture(
                        imageStream,
                        Document.PICTURE_TYPE_PNG,
                        "logo.png",
                        Units.toEMU(120),
                        Units.toEMU(120));
            } catch (Exception e) {
                System.err.println("No se pudo cargar la imagen " + e.getMessage());
            }
            imageRun.addBreak();
            imageRun.addBreak();
            imageRun.addBreak();

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Recuperación de Contraseña - Le Pettite Coffee");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            document.createParagraph().createRun().addBreak();

            XWPFParagraph body = document.createParagraph();
            XWPFRun bodyRun = body.createRun();
            bodyRun.setText("Hola, " + email);
            bodyRun.addBreak();
            bodyRun.setText("Has solicitado la recuperación de tu contraseña.");
            bodyRun.addBreak();
            bodyRun.setText("Tu token de recuperación es:");
            bodyRun.addBreak();
            bodyRun.setBold(true);
            bodyRun.setText(token);
            bodyRun.addBreak();
            bodyRun.setBold(false);
            bodyRun.addBreak();
            bodyRun.setText("Este token es válido por 10 minutos.");
            bodyRun.addBreak();
            bodyRun.setText("Fecha de generación: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.setText("Si no solicitaste este cambio, considera cambiar tu contraseña por seguridad.");
            bodyRun.addBreak();
            bodyRun.addBreak();
            bodyRun.setBold(true);
            bodyRun.setText("Atentamente, el equipo de Le Pettite Coffee.");
            bodyRun.setBold(false);
            document.write(out);
        }

        return pdfFile;
    }
}
