package com.cursoIntegrador.lePettiteCoffe.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Reviews;

/**
 * Repositorio encargado de gestionar las operaciones de persistencia
 * relacionadas con la entidad {@link Reviews}.
 */
@Repository
public interface ReviewsRepository extends JpaRepository<Reviews, Long> {

}
