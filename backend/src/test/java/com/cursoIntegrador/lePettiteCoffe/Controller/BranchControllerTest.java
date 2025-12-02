package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Branch;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.BranchService;

@ExtendWith(MockitoExtension.class)
public class BranchControllerTest {

        /**
         * Pruebas unitarias para BranchController que cubren listar, crear, actualizar y eliminar sucursales.
         */

        @Mock
        private BranchService branchService;

        @InjectMocks
        private BranchController controller;

        private Branch sucursalEjemplo;

        @BeforeEach
        void setUp() {
                sucursalEjemplo = new Branch();
                sucursalEjemplo.setIdsucursal(1);
                sucursalEjemplo.setNombre("Sucursal Centro");
                sucursalEjemplo.setDireccion("Av. Principal 123");
        }

        @Test
        /**
         * Verifica que listarSucursales devuelve OK con la lista de sucursales cuando el servicio tiene éxito.
         */
        void testListarSucursales_Success() {
                Branch sucursal2 = new Branch();
                sucursal2.setIdsucursal(2);
                sucursal2.setNombre("Sucursal Norte");
                sucursal2.setDireccion("Calle Norte 456");

                List<Branch> sucursales = Arrays.asList(sucursalEjemplo, sucursal2);
                when(branchService.listarSucursales()).thenReturn(sucursales);

                ResponseEntity<List<Branch>> response = controller.listarSucursales();

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(2, response.getBody().size());
                assertEquals("Sucursal Centro", response.getBody().get(0).getNombre());
                verify(branchService).listarSucursales();
        }

        @Test
        /**
         * Verifica que listarSucursales devuelve INTERNAL_SERVER_ERROR cuando el servicio lanza una excepción inesperada.
         */
        void testListarSucursales_ErrorInesperado() {
                when(branchService.listarSucursales()).thenThrow(new RuntimeException("Error de base de datos"));

                ResponseEntity<List<Branch>> response = controller.listarSucursales();

                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

        @Test
        /**
         * Verifica que agregarSucursal devuelve OK y la sucursal creada que retorna el servicio.
         */
        void testAgregarSucursal_ConPermisos_Success() {
                when(branchService.guardarSucursal(any(Branch.class))).thenReturn(sucursalEjemplo);

                ResponseEntity<?> response = controller.agregarSucursal(sucursalEjemplo);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals(sucursalEjemplo, response.getBody());
                verify(branchService).guardarSucursal(any(Branch.class));
        }

        @Test
        /**
         * Verifica que modificarSucursal devuelve OK y llama al servicio para modificar la sucursal parcialmente.
         */
        void testModificarSucursal_Success() {
                Branch sucursalActualizada = new Branch();
                sucursalActualizada.setNombre("Sucursal Centro Actualizada");

                when(branchService.modificarSucursalParcial(eq(1), any(Branch.class))).thenReturn(sucursalEjemplo);

                ResponseEntity<?> response = controller.modificarSucursal(1, sucursalActualizada);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                verify(branchService).modificarSucursalParcial(eq(1), any(Branch.class));
        }

        @Test
        /**
         * Verifica que modificarSucursal devuelve NOT_FOUND cuando el servicio lanza IllegalArgumentException por sucursal no encontrada.
         */
        void testModificarSucursal_NoEncontrada() {
                when(branchService.modificarSucursalParcial(eq(999), any(Branch.class)))
                                .thenThrow(new IllegalArgumentException("Sucursal con ID 999 no encontrada"));

                ResponseEntity<?> response = controller.modificarSucursal(999, sucursalEjemplo);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("Sucursal con ID 999 no encontrada", response.getBody());
        }

        @Test
        /**
         * Verifica que eliminarSucursal devuelve OK y el mensaje correcto cuando la eliminación tiene éxito.
         */
        void testEliminarSucursal_Success() {
                Mockito.doNothing().when(branchService).eliminarSucursal(1);

                ResponseEntity<?> response = controller.eliminarSucursal(1);

                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Sucursal eliminada correctamente", response.getBody());
                verify(branchService).eliminarSucursal(1);
        }

        @Test
        /**
         * Verifica que eliminarSucursal devuelve NOT_FOUND cuando el servicio lanza IllegalArgumentException.
         */
        void testEliminarSucursal_NoEncontrada() {
                Mockito.doThrow(new IllegalArgumentException("La sucursal con ID 999 no existe"))
                                .when(branchService).eliminarSucursal(999);

                ResponseEntity<?> response = controller.eliminarSucursal(999);

                assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
                assertEquals("La sucursal con ID 999 no existe", response.getBody());
        }
}
