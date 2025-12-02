package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountListDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountUpdateDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.ChangeRoleRequestDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.AccountService;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    /**
     * Pruebas unitarias para AccountController que cubren exportación, listado, actualización de perfil y cambios de rol.
     */

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController controller;

    private AccountListDTO sampleListDto;

    @BeforeEach
    void setUp() {
        // AccountListDTO does not have a no-arg constructor, create Cuenta and map
        com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta cuenta = new com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta();
        cuenta.setIdcuenta(1);
        cuenta.setEmail("a@b.com");

        sampleListDto = new AccountListDTO(cuenta);
    }

    @Test
    /**
     * Verifica que exportarExcel devuelve un InputStreamResource como attachment con las cabeceras esperadas cuando el servicio proporciona datos.
     *
     * @throws Exception si la exportación del servicio falla durante la preparación del test
     */
    void testExportarExcel_Success() throws Exception {
        byte[] data = new byte[] {1,2,3};
        ByteArrayInputStream stream = new ByteArrayInputStream(data);

        when(accountService.exportarExcel()).thenReturn(stream);

        ResponseEntity<?> response = controller.exportarExcel();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("attachment; filename=cuentas.xlsx", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertNotNull(response.getBody());
        assertEquals(InputStreamResource.class, response.getBody().getClass());
        verify(accountService).exportarExcel();
    }

    @Test
    /**
     * Verifica que listarUsuarios devuelve OK con contenido y NO_CONTENT cuando el servicio devuelve una lista vacía.
     */
    void testListarUsuarios_OK_and_NoContent() {
        when(accountService.listarUsuarios()).thenReturn(List.of(sampleListDto));

        ResponseEntity<?> respOk = controller.listarUsuarios();
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        verify(accountService).listarUsuarios();

        when(accountService.listarUsuarios()).thenReturn(List.of());
        ResponseEntity<?> respNoContent = controller.listarUsuarios();
        assertEquals(HttpStatus.NO_CONTENT, respNoContent.getStatusCode());
        verify(accountService, times(2)).listarUsuarios();
    }

    @Test
    /**
     * Verifica que actualizarPerfil devuelve 401 si no está autenticado y OK cuando la actualización es exitosa.
     */
    void testActualizarPerfil_Unauthenticated_and_Success() {
        AccountUpdateDTO dto = new AccountUpdateDTO();

        ResponseEntity<?> resp401 = controller.actualizarPerfil(null, dto);
        assertEquals(401, resp401.getStatusCodeValue());

        CustomUserDetails user = mock(CustomUserDetails.class);
        doNothing().when(accountService).updateAccountData(user, dto);

        ResponseEntity<?> respOk = controller.actualizarPerfil(user, dto);
        assertEquals(HttpStatus.OK, respOk.getStatusCode());
        assertEquals("Perfil actualizado correctamente", respOk.getBody());
        verify(accountService).updateAccountData(user, dto);
    }

    @Test
    /**
     * Verifica que getReportProduct devuelve un arreglo de bytes con el PDF y la cabecera correcta cuando tiene éxito.
     *
     * @throws Exception si la generación del reporte falla durante la preparación del test
     */
    void testGetReportProduct_Success() throws Exception {
        byte[] report = new byte[] {9,8,7};
        when(accountService.getReport()).thenReturn(report);

        ResponseEntity<?> resp = controller.getReportProduct();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("attachment; filename=reporte.pdf", resp.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(report, resp.getBody());
        verify(accountService).getReport();
    }

    @Test
    /**
     * Verifica que actualizarParcial devuelve OK cuando el cambio de rol tiene éxito y INTERNAL_SERVER_ERROR cuando el servicio lanza una excepción.
     */
    void testActualizarParcial_Success_and_Exception() {
        ChangeRoleRequestDTO req = new ChangeRoleRequestDTO(1, "USER");

        when(accountService.cambiarRol(req)).thenReturn(req);
        ResponseEntity<?> ok = controller.actualizarParcial(req);
        assertEquals(HttpStatus.OK, ok.getStatusCode());
        verify(accountService).cambiarRol(req);

        when(accountService.cambiarRol(req)).thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> err = controller.actualizarParcial(req);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, err.getStatusCode());
    }
}
