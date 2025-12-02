package com.cursoIntegrador.lePettiteCoffe.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Util.PdfGenerator;

/**
 * Servicio encargado de la generación de reportes en formato PDF.
 * Utiliza Reflexión para leer los campos de los objetos en una lista
 * y construir una tabla HTML que luego se convierte a PDF.
 */
@Service
public class ReportService {

    /**
     * Genera un reporte en formato PDF a partir de una lista de objetos.
     * Utiliza Reflexión para determinar las cabeceras de la tabla (nombres de campos)
     * y los datos (valores de los campos) de los objetos, construyendo un HTML
     * que luego es transformado a PDF.
     *
     * @param lista Una lista de objetos de cualquier tipo que se utilizarán para generar las filas del reporte.
     * @param reportName El nombre del reporte, utilizado en el encabezado del documento.
     * @return Un array de bytes que representa el contenido del archivo PDF generado.
     */
    public byte[] generateExampleReport(List<?> lista, String reportName) throws IllegalAccessException {

        List<String> cabeceras = new ArrayList<>();

        if (!lista.isEmpty()) {
            Object primerElemento = lista.get(0);
            Class<?> clazz = primerElemento.getClass();

            Field[] campos = clazz.getDeclaredFields();

            for (Field campo : campos) {
                campo.setAccessible(true);
                String nombreCampo = campo.getName();
                cabeceras.add(nombreCampo);
            }
        }

        StringBuilder cabeceraHTML = new StringBuilder("");

        for (String elemento : cabeceras) {
            cabeceraHTML.append("<th>");
            cabeceraHTML.append(elemento);
            cabeceraHTML.append("</th>");
        }

        StringBuilder filasHTML = new StringBuilder();

        for (Object obj : lista) {
            filasHTML.append("<tr>");

            Class<?> clazz = obj.getClass();
            Field[] campos = clazz.getDeclaredFields();

            for (Field campo : campos) {
                campo.setAccessible(true);
                Object valor = campo.get(obj);
                filasHTML.append("<td>");
                filasHTML.append(valor != null ? valor.toString() : "");
                filasHTML.append("</td>");
            }

            filasHTML.append("</tr>");
        }

        String plantilla = """
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml" lang="es">
                <head>
                <meta charset="UTF-8"/>
                <title>REPORTE DE PRODUCTOS</title>

                <style>

                body {
                    font-family: Arial, Helvetica, sans-serif;
                    margin: 0;
                    padding: 0;
                    font-size: 10pt;
                    background-color: #f8fafc;
                    color: #1f2937;
                }

                .report-container {
                    width: 100%;
                    background-color: #ffffff;
                }

                .header {
                    background-color: #ffb056;
                    color: #ffffff;
                    padding: 20px;
                    overflow: auto;
                    border-radius: 10px;
                }

                .logo-box {
                    float: left;
                    width: 60px;
                    height: 60px;
                    background-color: #f97316;
                    border-radius: 30px;
                    text-align: center;
                    line-height: 60px;
                    font-size: 20pt;
                    font-weight: bold;
                    margin-right: 20px;
                }

                .report-name-box {
                    float: right;
                    background-color: #f97316;
                    color: #ffffff;
                    padding: 8px 15px;
                    margin-top: 10px;
                    border-radius: 5px;
                    font-size: 10pt;
                    font-weight: bold;
                    text-transform: uppercase;
                }

                .main-title {
                    text-align: center;
                    margin-top: 10px;
                }

                .main-title h1 {
                    font-size: 18pt;
                    margin: 0;
                }

                main {
                    padding: 25px;
                }

                h3 {
                    font-size: 14pt;
                    border-bottom: 2px solid #ddd;
                    padding-bottom: 5px;
                }

                table {
                    width: 100%;
                    border-collapse: collapse;
                    font-size: 10pt;
                }

                thead {
                    background-color: #e7d5be;
                    font-weight: bold;
                }

                th, td {
                    border: 1px solid #ccc;
                    padding: 10px;
                }

                tbody tr:nth-child(odd) {
                    background-color: #f9fafb;
                }

                img{
                    width: 60px;
                }

                footer {
                    position: running(footerArea);
                    padding: 10px 0;
                    font-size: 8pt;
                    color: #6b7280;
                    text-align: center;
                    border-top: 1px solid #ddd;
                }

                @page {
                    @bottom-center {
                        content: element(footerArea);
                    }
                }

                </style>
                </head>

                <body>
                <div class="report-container">

                <header class="header">
                    <div class="logo-box"><img src="data:image/png;base64,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" /></div>
                    <div class="report-name-box">REPORTE DE ${reportname}</div>
                    <div class="main-title">
                        <h1>LE PETITE COFFEE</h1>
                    </div>
                    <br/>
                </header>

                <main>
                <h3>Detalles del reporte</h3>

                <table>
                    <thead>
                        <tr>
                            ${cabeceras}
                        </tr>
                    </thead>
                    <tbody>
                        ${filas}
                    </tbody>
                </table>

                </main>

                <footer>
                    ¡Documento generado con mucha paciencia y cafe! - 2025
                </footer>

                </div>
                </body>
                </html>
                """;

        String htmlFinal = plantilla.replace("${reportname}", reportName)
                .replace("${cabeceras}", cabeceraHTML.toString())
                .replace("${filas}", filasHTML.toString());

        return PdfGenerator.generatePdfFromHtml(htmlFinal);
    }

}
