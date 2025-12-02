package com.cursoIntegrador.lePettiteCoffe.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Purchase;

/**
 * Repositorio encargado de gestionar las operaciones de persistencia
 * relacionadas con la entidad {@link Purchase}.
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * Obtiene todas las compras asociadas a una cuenta mediante su ID.
     *
     * @param idCuenta el identificador único de la cuenta.
     * @return una lista de compras correspondientes a la cuenta indicada.
     */
    List<Purchase> findAllByCuentaIdcuenta(Integer idCuenta);
}
