package com.cursoIntegrador.lePettiteCoffe.Model.Security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;

/**
 * Implementación de UserDetails que adapta la entidad Cuenta al contrato de Spring Security.
 * <p>
 * Proporciona autoridades, nombre de usuario, contraseña y los flags necesarios para
 * la autenticación/autorization basados en la entidad Cuenta.
 */
public class CustomUserDetails implements UserDetails {

    private final Cuenta cuenta;

    /**
     * Crea un CustomUserDetails a partir de la entidad Cuenta.
     *
     * @param cuenta entidad Cuenta asociada al usuario autenticado
     */
    public CustomUserDetails(Cuenta cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    /**
     * Obtiene las autoridades (roles) del usuario.
     *
     * @return colección de GrantedAuthority que contiene los roles del usuario en formato ROLE_*
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + cuenta.getRol()));
    }

    @Override
    /**
     * Devuelve la contraseña almacenada para la cuenta.
     *
     * @return la contraseña (como está guardada en la entidad Cuenta)
     */
    public String getPassword() {
        return cuenta.getPassword();
    }

    @Override
    /**
     * Devuelve el identificador de usuario (email) de la cuenta.
     *
     * @return el email de la cuenta utilizado como username
     */
    public String getUsername() {
        return cuenta.getEmail();
    }

    @Override
    /**
     * Indica si la cuenta no está expirada.
     *
     * @return true si la cuenta no está expirada, false en caso contrario
     */
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    /**
     * Indica si la cuenta no está bloqueada.
     *
     * @return true si el estado de la cuenta es distinto de bloqueada; aquí se considera
     *         "ACTIVO" como no bloqueada
     */
    public boolean isAccountNonLocked() {
        return "ACTIVO".equalsIgnoreCase(cuenta.getEstado());
    }

    @Override
    /**
     * Indica si las credenciales no han expirado.
     *
     * @return true si las credenciales siguen siendo válidas
     */
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    /**
     * Indica si la cuenta está habilitada para iniciar sesión.
     *
     * @return true si el estado de la cuenta es "ACTIVO"
     */
    public boolean isEnabled() {
        return "ACTIVO".equalsIgnoreCase(cuenta.getEstado());
    }

    /**
     * Devuelve la entidad Cuenta subyacente asociada a este UserDetails.
     *
     * @return la entidad Cuenta referenciada por este CustomUserDetails
     */
    public Cuenta getCuenta() {
        return cuenta;
    }
}
