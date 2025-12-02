package com.cursoIntegrador.lePettiteCoffe.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountLoginDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Security.JwtUtil;
import com.cursoIntegrador.lePettiteCoffe.Service.DAO.AccountService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de autenticación y autorización de usuarios.
 * Maneja el inicio de sesión, registro, gestión de tokens JWT y verificación de roles.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    @Autowired
    private final JwtUtil jwtUtil;
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    @Autowired
    private final AuthenticationManager authenticationManager;

    // public Map<String, Object> login(String username, String password) {
    // Cuenta user = accountService.findByEmail(username);
    // if (this.userExists(username) && passwordEncoder.matches(password,
    // user.getPassword())) {
    // Map<String, Object> respuesta = new ConcurrentHashMap<>();
    // String token = jwtUtil.generateToken(user.getEmail());
    // Cuenta cuenta = accountService.findByEmail(username);
    // respuesta.put("loginData", new AccountLoginDTO(cuenta, token));
    // return respuesta;
    // }
    // throw new RuntimeException("Credenciales inválidas");
    // }

    /**
     * Realiza el proceso de inicio de sesión de un usuario.
     * Autentica al usuario usando el AuthenticationManager y genera un token JWT.
     *
     * @param username El nombre de usuario (email) de la cuenta.
     * @param password La contraseña de la cuenta.
     * @return Un mapa que contiene los datos de inicio de sesión, incluyendo el token JWT y el DTO de la cuenta.
     */
    public Map<String, Object> login(String username, String password) {
        Authentication auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));

        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(user.getUsername());

        Cuenta cuenta = accountService.findByEmail(username);
        Map<String, Object> respuesta = new ConcurrentHashMap<>();
        respuesta.put("loginData", new AccountLoginDTO(cuenta, token));

        return respuesta;
    }

    /**
     * Extrae el nombre de usuario (email) del token JWT.
     *
     * @param token El token JWT del cual se desea extraer el nombre de usuario.
     * @return El nombre de usuario (email) contenido en el token.
     */
    public String extractUsername(String token) {
        return jwtUtil.validateAndGetUser(token);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica que el usuario no exista, encripta la contraseña y guarda la nueva cuenta.
     *
     * @param email La dirección de correo electrónico del nuevo usuario.
     * @param password La contraseña del nuevo usuario.
     */
    public void register(String email, String password) {
        if (this.userExists(email)) {
            throw new RuntimeException("El usuario ya está registrado");
        }
        String encriptada = passwordEncoder.encode(password);

        Cuenta newUser = new Cuenta();
        newUser.setEmail(email);
        newUser.setPassword(encriptada);
        accountService.save(newUser);
    }

    /**
     * Verifica si un usuario con el nombre de usuario (email) dado ya existe.
     *
     * @param username El nombre de usuario (email) a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    public boolean userExists(String username) {
        return accountService.findByEmail(username) != null;
    }

    /**
     * Invalida un token JWT, añadiéndolo a una lista de tokens revocados.
     *
     * @param token El token JWT a invalidar.
     */
    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    /**
     * Verifica si un token JWT es válido (no revocado y con firma correcta).
     *
     * @param token El token JWT a validar.
     * @return true si el token es válido y no está revocado, false en caso contrario.
     */
    public boolean isTokenValid(String token) {
        if (invalidatedTokens.contains(token)) {
            return false;
        }

        try {
            jwtUtil.validateTokenAndGetClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Actualiza la contraseña de un usuario.
     * Busca el usuario por email, encripta la nueva contraseña y llama al servicio de cuentas para la actualización.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @param newPassword La nueva contraseña a establecer.
     */
    public void actualizarPassword(String email, String newPassword) {
        Cuenta user = accountService.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        String encriptada = passwordEncoder.encode(newPassword);
        accountService.updatePassword(email, encriptada);
    }

    /**
     * Verifica si un usuario con el email dado tiene el rol especificado.
     *
     * @param email La dirección de correo electrónico del usuario.
     * @param searchRole El rol que se desea verificar.
     * @return true si el usuario existe y tiene el rol, false en caso contrario.
     */
    public boolean userHasRole(String email, String searchRole) {

        if (userExists(email)) {
            Cuenta user = accountService.findByEmail(email);
            String role = user.getRol();
            return role.equals(searchRole);
        }

        return false;
    }

    /**
     * Valida un token JWT y verifica si el usuario asociado al token tiene el rol especificado.
     *
     * @param token El token JWT a validar.
     * @param role El rol que se debe verificar.
     * @return true si el token es válido y el usuario tiene el rol, false en caso contrario.
     */
    public boolean validateTokenAndRole(String token, String role) {
        if (this.isTokenValid(token)) {
            String username = this.extractUsername(token);
            return this.userHasRole(username, role);
        }
        return false;
    }
}
