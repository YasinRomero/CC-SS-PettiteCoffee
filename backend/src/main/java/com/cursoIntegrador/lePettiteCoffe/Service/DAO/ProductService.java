package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Product;
import com.cursoIntegrador.lePettiteCoffe.Repository.ProductRepository;
import com.cursoIntegrador.lePettiteCoffe.Service.ReportService;

import org.springframework.beans.BeanWrapperImpl;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de productos (Product).
 * Proporciona métodos para listar, guardar, eliminar y modificar productos.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ReportService reportService;

    /**
     * Obtiene una lista de todos los productos disponibles.
     *
     * @return Una lista de objetos Product.
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Guarda un nuevo producto en la base de datos o actualiza uno existente si tiene ID.
     *
     * @param product La entidad Product a guardar.
     * @return La entidad Product guardada o actualizada.
     */
    public Product guardarProducto(Product product) {
        return productRepository.save(product);
    }

    /**
     * Elimina un producto de la base de datos por su ID.
     *
     * @param idproducto El ID del producto a eliminar.
     */
    public void eliminarProductoPorId(Integer idproducto) {
        if (!productRepository.existsById(idproducto)) {
            throw new IllegalArgumentException("El producto con ID " + idproducto + " no existe");
        }
        productRepository.deleteById(idproducto);
    }

    /**
     * Modifica un producto existente, permitiendo actualizaciones parciales.
     * Solo las propiedades no nulas del objeto `p` se copian al producto existente.
     *
     * @param id El ID del producto a modificar.
     * @param p La entidad Product con los campos actualizados.
     * @return La entidad Product modificada y guardada.
     */
    public Product modificarProducto(Integer id, Product p) {
        Product existente = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto con ID " + id + " no encontrado"));

        BeanUtils.copyProperties(p, existente, getNullPropertyNames(p));

        return productRepository.save(existente);
    }

    /**
     * Obtiene los nombres de las propiedades de un objeto que tienen un valor nulo.
     * Se utiliza para ignorar propiedades nulas durante una copia de propiedades (BeanUtils.copyProperties).
     *
     * @param source El objeto del cual se verifican las propiedades nulas.
     * @return Un array de Strings con los nombres de las propiedades nulas.
     */
    private String[] getNullPropertyNames(Object source) {
        final var src = new BeanWrapperImpl(source);
        return Stream.of(src.getPropertyDescriptors())
                .map(PropertyDescriptor::getName)
                .filter(name -> src.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    /**
     * Genera un reporte de productos en formato de bytes.
     * Utiliza el servicio de reportes para crear un informe basado en la lista de todos los productos.
     *
     * @return Un array de bytes que representa el reporte generado, o un array de bytes vacío si ocurre un error.
     */
    public byte[] getReport() {
        try {
            return reportService.generateExampleReport(getAllProducts(), "PRODUCTOS");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

}
