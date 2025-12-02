package com.cursoIntegrador.lePettiteCoffe.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.IA.PromptRequest;
import com.cursoIntegrador.lePettiteCoffe.Service.IA.GeminiService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/IA")
public class IAController {

    /**
     * Controlador que expone un endpoint para consultas a un servicio de IA (GeminiService).
     */

    @Autowired
    private final GeminiService gemservice;

    @PostMapping("/consulta")
    /**
     * Envía la consulta al servicio de IA y devuelve la respuesta como texto.
     *
     * @param request DTO PromptRequest que contiene prompt y modo de consulta
     * @return String con la respuesta generada por el servicio de IA
     */
    public String consulta(@RequestBody PromptRequest request) {
        return gemservice.lePettitePromptCompuesto(request.getPrompt(), request.getMode());
    }

}
