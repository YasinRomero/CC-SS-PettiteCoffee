package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.IA.PromptRequest;
import com.cursoIntegrador.lePettiteCoffe.Service.IA.GeminiService;

@ExtendWith(MockitoExtension.class)
public class IAControllerTest {

    /**
     * Pruebas unitarias para IAController que verifican la delegación de prompts al servicio Gemini.
     */

    @Mock
    private GeminiService gemService;

    @InjectMocks
    private IAController controller;

    @Test
    /**
     * Verifica que consulta reenvía el prompt y el modo al GeminiService y devuelve la respuesta.
     */
    void testConsulta_ReturnsResponse() {
        PromptRequest req = new PromptRequest("hola","default");
        when(gemService.lePettitePromptCompuesto("hola","default")).thenReturn("respuesta");

        String result = controller.consulta(req);

        assertEquals("respuesta", result);
        verify(gemService).lePettitePromptCompuesto("hola","default");
    }

}
