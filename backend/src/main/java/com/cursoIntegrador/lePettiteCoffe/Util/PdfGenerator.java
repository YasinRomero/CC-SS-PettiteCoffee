package com.cursoIntegrador.lePettiteCoffe.Util;

import java.io.ByteArrayOutputStream;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * Clase de utilidad para generar documentos PDF a partir de contenido HTML
 * utilizando la librería openhtmltopdf.
 */
public class PdfGenerator {

    /**
     * Convierte una cadena de contenido HTML en un documento PDF y lo retorna como un array de bytes.
     *
     * @param html La cadena que contiene el contenido HTML a renderizar en PDF.
     * @return Un array de bytes que representa el contenido del documento PDF generado.
     */
    public static byte[] generatePdfFromHtml(String html) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, "file:/");
            builder.toStream(outputStream);

            builder.run();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

}
