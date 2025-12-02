package com.cursoIntegrador.lePettiteCoffe.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Product.ProductDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Product;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    /**
     * Controlador que expone endpoints para operaciones sobre productos.
     * <p>
     * Permite listar productos con la URL de imagen, agregar, eliminar,
     * modificar parcialmente y generar reportes en PDF.
     */

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private final ProductService productService;

    @GetMapping("/getAllProducts")
    /**
     * Devuelve todos los productos disponibles junto con la URL de su imagen.
     *
     * @param request HttpServletRequest usado para construir la URL base de las imágenes
     * @return ResponseEntity con la lista de ProductDTO si la operación es exitosa,
     *         o ResponseEntity.noContent() (204) si no hay productos
     */
    public ResponseEntity<List<ProductDTO>> getAllProductsWithImage(HttpServletRequest request) {

        String baseUrl = String.format("%s://%s:%d/images/productos/", request.getScheme(), request.getServerName(),
                request.getServerPort());

        logger.info("Solicitud para obtener todos los productos desde {}", baseUrl);

        try {
            List<Product> products = productService.getAllProducts();
            List<ProductDTO> productsDTO = new ArrayList<>();

            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            for (Product product : products) {
                ProductDTO productDTO = new ProductDTO(product);
                String imageUrl = baseUrl + product.getCodproducto() + ".webp";
                productDTO.setImageUrl(imageUrl);
                productsDTO.add(productDTO);
            }

            return ResponseEntity.ok(productsDTO);
        } catch (Exception e) {
            logger.error("Error al obtener productos - {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/agregar")
    @PreAuthorize("hasRole('ADMIN')")
    /**
     * Agrega un nuevo producto al sistema. Solo accesible por administradores.
     *
     * @param product entidad Product con los datos del nuevo producto
     * @return ResponseEntity con el producto creado si la operación es exitosa,
     *         o ResponseEntity con estado 500 en caso de error
     */
    public ResponseEntity<?> agregarProducto(@Valid @RequestBody Product product) {
        logger.info("Intento de agregar producto: {}", product.getNombre());

        try {
            Product nuevo = productService.guardarProducto(product);
            logger.info("Producto agregado por ADMIN : {}", nuevo.getNombre());
            return ResponseEntity.ok(nuevo);

        } catch (RuntimeException e) {
            logger.error("Error al agregar producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al agregar producto");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    /**
     * Elimina un producto por su identificador. Solo accesible por administradores.
     *
     * @param id identificador del producto a eliminar
     * @return ResponseEntity con mensaje de éxito si se elimina, estado 404 si no existe,
     *         o estado 500 en caso de error
     */
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer id) {
        logger.info("Intento de eliminar producto con ID: {}", id);
        try {
            productService.eliminarProductoPorId(id);
            logger.info("Producto con ID {} eliminado por admin", id);
            return ResponseEntity.ok("Producto eliminado correctamente");

        } catch (IllegalArgumentException e) {
            logger.warn("Intento de eliminar producto inexistente: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());

        } catch (Exception e) {
            logger.error("Error al eliminar producto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al eliminar producto");
        }
    }

    @PatchMapping("/modificar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    /**
     * Actualiza parcialmente un producto existente.
     *
     * @param id              identificador del producto a modificar
     * @param productoParcial entidad Product con los campos a actualizar
     * @return ResponseEntity con el producto actualizado o estado 500 en caso de error
     */
    public ResponseEntity<?> actualizarParcial(@PathVariable Integer id, @RequestBody Product productoParcial) {
        logger.info("Intento de actualización parcial de producto con ID: {}", id);
        try {
            Product actualizado = productService.modificarProducto(id, productoParcial);
            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            logger.error("Error en actualización parcial: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al actualizar producto");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getReport")
    /**
     * Genera y devuelve un reporte en PDF con la información de productos.
     *
     * @return ResponseEntity con el PDF en bytes, enviado como attachment 'reporte.pdf'
     */
    public ResponseEntity<?> getReportProduct() throws Exception {
        byte[] pdf = productService.getReport();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
