package com.cursoIntegrador.lePettiteCoffe.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;

@Repository
public interface AccountRepository extends JpaRepository<Cuenta, Integer> {

    /**
     * Busca y retorna una cuenta según el correo electrónico proporcionado.
     *
     * @param email correo electrónico de la cuenta a buscar.
     * @return la cuenta asociada al correo, o null si no existe.
     */
    Cuenta findByEmail(String email);

    /**
     * Busca una cuenta que coincida con el correo y contraseña proporcionados.
     *
     * @param email correo electrónico registrado del usuario.
     * @param password contraseña asociada a la cuenta.
     * @return la cuenta que coincida con ambos valores, o null si no existe.
     */
    Cuenta findByEmailAndPassword(String email, String password);

}

