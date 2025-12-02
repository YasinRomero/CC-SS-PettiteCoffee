package com.cursoIntegrador.lePettiteCoffe.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Product;

/**
 * Repositorio encargado de gestionar las operaciones de persistencia
 * relacionadas con la entidad {@link Product}.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
