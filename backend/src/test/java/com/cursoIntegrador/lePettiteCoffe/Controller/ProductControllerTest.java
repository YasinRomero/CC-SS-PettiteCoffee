package com.cursoIntegrador.lePettiteCoffe.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Product.ProductDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Product;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    /**
     * Pruebas unitarias para ProductController (endpoints de productos).
     */

    @Mock
    private ProductService productService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProductController controller;

    private Product productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Product();
        productoEjemplo.setCodproducto("1");
        productoEjemplo.setNombre("Café");
    }

    @Test
    /**
    * Verifica que getAllProductsWithImage devuelve OK y mapea correctamente la URL de imagen cuando existen productos.
     */
    void testGetAllProductsWithImage() {
        when(productService.getAllProducts()).thenReturn(List.of(productoEjemplo));
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);

        ResponseEntity<List<ProductDTO>> response = controller.getAllProductsWithImage(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("http://localhost:8080/images/productos/1.webp", response.getBody().get(0).getImageUrl());
        verify(productService).getAllProducts();
    }

    @Test
    /**
    * Verifica que getAllProductsWithImage devuelve NO_CONTENT cuando no hay productos.
     */
    void testGetAllProductsWithImage_Empty() {
        when(productService.getAllProducts()).thenReturn(List.of());

        ResponseEntity<List<ProductDTO>> response = controller.getAllProductsWithImage(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(productService).getAllProducts();
    }

    @Test
    /**
    * Verifica que getAllProductsWithImage devuelve INTERNAL_SERVER_ERROR cuando el servicio lanza una excepción.
     */
    void testGetAllProductsWithImage_Exception() {
        when(productService.getAllProducts()).thenThrow(new RuntimeException("boom"));

        ResponseEntity<List<ProductDTO>> response = controller.getAllProductsWithImage(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(productService).getAllProducts();
    }

    @Test
    /**
    * Verifica que agregarProducto devuelve OK y el producto creado cuando el servicio tiene éxito.
     */
    void testAgregarProducto_Success() {
        Product p = new Product();
        p.setNombre("Cafe Nuevo");

        when(productService.guardarProducto(p)).thenReturn(p);

        ResponseEntity<?> response = controller.agregarProducto(p);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(p, response.getBody());
        verify(productService).guardarProducto(p);
    }

    @Test
    /**
    * Verifica que agregarProducto devuelve INTERNAL_SERVER_ERROR y el mensaje esperado cuando el servicio falla.
     */
    void testAgregarProducto_Exception() {
        Product p = new Product();
        p.setNombre("Cafe Error");

        when(productService.guardarProducto(p)).thenThrow(new RuntimeException("fail"));

        ResponseEntity<?> response = controller.agregarProducto(p);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al agregar producto", response.getBody());
        verify(productService).guardarProducto(p);
    }

    @Test
    /**
    * Verifica que eliminarProducto devuelve OK cuando la eliminación es correcta.
     */
    void testEliminarProducto_Success() {
        doNothing().when(productService).eliminarProductoPorId(1);

        ResponseEntity<?> response = controller.eliminarProducto(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productService).eliminarProductoPorId(1);
    }

    @Test
    /**
    * Verifica que eliminarProducto devuelve NOT_FOUND cuando el producto no existe.
     */
    void testEliminarProducto_NotFound() {
        doThrow(new IllegalArgumentException("No existe")).when(productService).eliminarProductoPorId(999);

        ResponseEntity<?> response = controller.eliminarProducto(999);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No existe", response.getBody());
        verify(productService).eliminarProductoPorId(999);
    }

    @Test
    /**
    * Verifica que actualizarParcial devuelve OK y el producto actualizado cuando la modificación tiene éxito.
     */
    void testActualizarParcial_Success() {
        Product input = new Product();
        input.setNombre("Old");
        Product updated = new Product();
        updated.setNombre("New");

        when(productService.modificarProducto(1, input)).thenReturn(updated);

        ResponseEntity<?> response = controller.actualizarParcial(1, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
        verify(productService).modificarProducto(1, input);
    }

    @Test
    /**
    * Verifica que actualizarParcial devuelve INTERNAL_SERVER_ERROR ante excepciones inesperadas.
     */
    void testActualizarParcial_Exception() {
        Product input = new Product();
        when(productService.modificarProducto(1, input)).thenThrow(new RuntimeException("err"));

        ResponseEntity<?> response = controller.actualizarParcial(1, input);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error al actualizar producto", response.getBody());
        verify(productService).modificarProducto(1, input);
    }

    @Test
    /**
    * Verifica que getReportProduct devuelve los bytes del PDF y la cabecera content-disposition cuando está disponible.
     *
    * @throws Exception si la generación del reporte falla durante la preparación del test
     */
    void testGetReport_Success() throws Exception {
        byte[] pdf = new byte[] {1,2,3};
        when(productService.getReport()).thenReturn(pdf);

        ResponseEntity<?> response = controller.getReportProduct();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("attachment; filename=reporte.pdf", response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals(pdf, response.getBody());
        verify(productService).getReport();
    }
}
