package com.cursoIntegrador.lePettiteCoffe.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.AccountService;

@Service
/**
 * Service encargado de cargar los detalles de usuario para procesos de autenticación.
 * Implementa la lógica para obtener un usuario por su email.
 */
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    /**
     * Carga los detalles de un usuario utilizando su correo electrónico.
     *
     * @param email Correo electrónico del usuario que se desea autenticar.
     * @return Los detalles del usuario autenticado.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email proporcionado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Cuenta cuenta = accountService.findByEmail(email);

        if (cuenta == null) {
            throw new UsernameNotFoundException("Usuario no encontrado:" + email);
        }

        return new CustomUserDetails(cuenta);
    }
}
