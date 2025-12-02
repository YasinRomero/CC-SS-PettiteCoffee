package com.cursoIntegrador.lePettiteCoffe.Model.DTO.IA;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PromptRequest {

    /**
     * DTO para solicitudes de consulta a servicio de IA que contiene prompt y modo.
     */
    private String prompt;
    private String mode;
}
