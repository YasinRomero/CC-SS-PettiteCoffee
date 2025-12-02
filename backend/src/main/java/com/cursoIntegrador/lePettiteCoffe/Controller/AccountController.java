package com.cursoIntegrador.lePettiteCoffe.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountListDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountUpdateDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.ChangeRoleRequestDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Product;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
/**
 * Controlador que expone endpoints para operaciones sobre cuentas de usuario.
 * <p>
 * Proporciona funcionalidades de exportación, listado, actualización de perfil,
 * generación de reportes y cambio de roles sobre las cuentas.
 */
public class AccountController {

    @Autowired
    private final AccountService accountService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/export")
    /**
     * Exporta todas las cuentas en formato Excel y devuelve un recurso descargable.
     *
     * @return ResponseEntity con un InputStreamResource que contiene el archivo Excel
     *         listo para descargar (content-disposition: attachment; filename=cuentas.xlsx).
     */
    public ResponseEntity<?> exportarExcel() throws IOException {

        ByteArrayInputStream excelStream = accountService.exportarExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=cuentas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/listar")
    /**
     * Obtiene el listado de cuentas de usuario.
     *
     * @return ResponseEntity con la lista de AccountListDTO cuando existen cuentas
     *         o ResponseEntity.noContent() (204) si la lista está vacía.
     */
    public ResponseEntity<?> listarUsuarios() {

        List<AccountListDTO> cuentas = accountService.listarUsuarios();

        if (cuentas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cuentas);
    }

    @PatchMapping("/update-profile")
        /**
         * Actualiza el perfil de la cuenta asociada al usuario autenticado.
         *
         * @param userDetails información del usuario autenticado obtenida del contexto de seguridad
         * @param dto         DTO con los campos para actualizar en la cuenta
         * @return ResponseEntity con mensaje de éxito si la actualización fue correcta,
         *         o ResponseEntity con estado 401 si el usuario no está autenticado.
         */
        public ResponseEntity<?> actualizarPerfil(@AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AccountUpdateDTO dto) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado");
        }

        accountService.updateAccountData(userDetails, dto);
        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getReport")
    /**
     * Genera y devuelve un reporte en PDF sobre las cuentas.
     *
     * @return ResponseEntity con un arreglo de bytes que representa el PDF
     *         y se envía como attachment con el nombre 'reporte.pdf'.
     */
    public ResponseEntity<?> getReportProduct() throws Exception {
        byte[] pdf = accountService.getReport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PatchMapping("/changeRole")
    @PreAuthorize("hasRole('ADMIN')")
    /**
     * Cambia el rol de una cuenta según los parámetros proporcionados.
     *
     * @param changeRoleRequestDTO DTO que contiene la información necesaria para
     *                             cambiar el rol de la cuenta objetivo
     * @return ResponseEntity con el DTO actualizado en caso de éxito; en caso de
     *         error devuelve un ResponseEntity con estado 500 y un mensaje de error.
     */
    public ResponseEntity<?> actualizarParcial(@RequestBody ChangeRoleRequestDTO changeRoleRequestDTO) {
        try {
            ChangeRoleRequestDTO actualizado = accountService.cambiarRol(changeRoleRequestDTO);
            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar producto");
        }
    }

}
