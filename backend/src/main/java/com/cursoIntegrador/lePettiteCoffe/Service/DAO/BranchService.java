package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Branch;
import com.cursoIntegrador.lePettiteCoffe.Repository.BranchRepository;
import com.cursoIntegrador.lePettiteCoffe.Service.ReportService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de sucursales (Branch).
 * Proporciona métodos para listar, guardar, eliminar y modificar sucursales.
 */
@Service
@RequiredArgsConstructor
public class BranchService {

    @Autowired
    private final BranchRepository branchrepo;

    @Autowired
    private final ReportService reportService;

    /**
     * Obtiene una lista de todas las sucursales existentes.
     *
     * @return Una lista de objetos Branch.
     */
    public List<Branch> listarSucursales() {
        return branchrepo.findAll();
    }

    /**
     * Guarda una nueva sucursal en la base de datos o actualiza una existente si tiene ID.
     *
     * @param branch La entidad Branch a guardar.
     * @return La entidad Branch guardada o actualizada.
     */
    public Branch guardarSucursal(Branch branch) {
        return branchrepo.save(branch);
    }

    /**
     * Elimina una sucursal de la base de datos por su ID.
     *
     * @param id El ID de la sucursal a eliminar.
     */
    public void eliminarSucursal(Integer id) {
        if (!branchrepo.existsById(id)) {
            throw new IllegalArgumentException("La sucursal con ID " + id + " no existe");
        }
        branchrepo.deleteById(id);
    }

    /**
     * Realiza una modificación parcial de una sucursal existente.
     * Solo las propiedades no nulas del objeto `branchActualizada` se copian a la sucursal existente.
     *
     * @param id El ID de la sucursal a modificar.
     * @param branchActualizada La entidad Branch con los campos actualizados.
     * @return La entidad Branch modificada y guardada.
     */
    public Branch modificarSucursalParcial(Integer id, Branch branchActualizada) {
        Branch existente = branchrepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal con ID " + id + " no encontrada"));

        BeanUtils.copyProperties(branchActualizada, existente, getNullPropertyNames(branchActualizada));

        return branchrepo.save(existente);
    }

    /**
     * Obtiene los nombres de las propiedades de un objeto que son nulas.
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
     * Genera un reporte de sucursales en formato de bytes.
     * Utiliza el servicio de reportes para crear un informe basado en la lista de sucursales.
     *
     * @return Un array de bytes que representa el reporte generado, o un array de bytes vacío si ocurre un error.
     */
    public byte[] getReport() {
        try {
            return reportService.generateExampleReport(this.listarSucursales(), "SUCURSALES");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
