package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.io.IOException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountListDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.AccountUpdateDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Account.ChangeRoleRequestDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Repository.AccountRepository;
import com.cursoIntegrador.lePettiteCoffe.Service.ReportService;
import com.cursoIntegrador.lePettiteCoffe.Util.ExcelGenerator;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de cuentas de usuario (Cuenta).
 * Proporciona métodos para buscar, guardar, actualizar y listar cuentas.
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    @Autowired
    private final ReportService reportService;

    @Autowired
    private final AccountRepository accountRepository;

    /**
     * Busca una cuenta de usuario por su dirección de correo electrónico.
     *
     * @param email La dirección de correo electrónico de la cuenta a buscar.
     * @return La entidad Cuenta encontrada o null si no existe.
     */
    public Cuenta findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    /**
     * Guarda una nueva cuenta de usuario.
     * Establece el estado inicial a "ACTIVO", el rol a "CLIENTE" y la fecha de registro actual.
     *
     * @param user La entidad Cuenta a guardar.
     */
    public void save(Cuenta user) {
        user.setEstado("ACTIVO");
        user.setRol("CLIENTE");
        user.setFechaRegistro(LocalDateTime.now());
        accountRepository.save(user);
    }

    /**
     * Actualiza la contraseña de una cuenta de usuario dado su correo electrónico.
     * Si la cuenta existe, se actualiza la contraseña y se guarda.
     *
     * @param email La dirección de correo electrónico de la cuenta a actualizar.
     * @param nuevaPassword La nueva contraseña a establecer.
     */
    public void updatePassword(String email, String nuevaPassword) {
        Cuenta cuenta = accountRepository.findByEmail(email);
        if (cuenta != null) {
            cuenta.setPassword(nuevaPassword);
            accountRepository.save(cuenta);
        }
    }

    /**
     * Obtiene una lista de todas las cuentas de usuario en formato DTO para listado.
     *
     * @return Una lista de objetos AccountListDTO con la información de las cuentas.
     */
    public List<AccountListDTO> listarUsuarios() {
        List<Cuenta> cuentas = accountRepository.findAll();
        List<AccountListDTO> cuentasDTO = new ArrayList<>();

        for (Cuenta element : cuentas) {
            AccountListDTO elementDTO = new AccountListDTO(element);
            cuentasDTO.add(elementDTO);
        }

        return cuentasDTO;
    }

    /**
     * Exporta la lista de cuentas de usuario a un archivo Excel en forma de ByteArrayInputStream.
     *
     * @return Un ByteArrayInputStream que contiene el archivo Excel generado.
     */
    public ByteArrayInputStream exportarExcel() throws IOException {
        List<AccountListDTO> cuentas = this.listarUsuarios();
        return ExcelGenerator.generateExcel(cuentas, "Cuentas");
    }

    /**
     * Actualiza los datos de la cuenta del usuario actualmente autenticado (userDetails) con la información del DTO.
     * Solo se copian las propiedades que no son nulas en el DTO.
     *
     * @param userDetails Los detalles del usuario autenticado, incluyendo la entidad Cuenta.
     * @param dto El DTO con los datos de cuenta a actualizar.
     */
    public void updateAccountData(CustomUserDetails userDetails, AccountUpdateDTO dto) {
        Cuenta cuenta = userDetails.getCuenta();
        if (cuenta == null)
            return;

        String[] nullProps = getNullPropertyNames(dto);
        BeanUtils.copyProperties(dto, cuenta, nullProps);

        accountRepository.save(cuenta);
    }

    /**
     * Obtiene los nombres de las propiedades de un objeto que tienen un valor nulo.
     * Utilizado para copiar propiedades ignorando los valores nulos.
     *
     * @param source El objeto del cual se obtendrán los nombres de las propiedades nulas.
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
     * Genera un reporte de usuarios en formato de bytes.
     * Utiliza el servicio de reportes para generar un informe basado en la lista de usuarios.
     *
     * @return Un array de bytes que representa el reporte generado, o un array de bytes vacío si ocurre un error.
     */
    public byte[] getReport() {
        try {
            return reportService.generateExampleReport(this.listarUsuarios(), "USUARIOS");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Cambia el rol de una cuenta de usuario.
     * Busca la cuenta por ID, verifica que el rol sea válido ("ADMIN" o "CLIENTE") y actualiza el rol.
     *
     * @param changeRoleRequestDTO El DTO que contiene el ID de la cuenta y el nuevo rol.
     * @return Un DTO de respuesta que contiene el ID de la cuenta y el rol actualizado.
     */
    public ChangeRoleRequestDTO cambiarRol(ChangeRoleRequestDTO changeRoleRequestDTO) {

        Cuenta cuenta = accountRepository.findById(changeRoleRequestDTO.getIdcuenta())
                .orElse(null);

        if (cuenta == null) {
            throw new RuntimeException("Cuenta no encontrada");
        }

        List<String> rolesPermitidos = List.of("ADMIN", "CLIENTE");

        if (!rolesPermitidos.contains(changeRoleRequestDTO.getRol())) {
            throw new RuntimeException("Rol inválido");
        }

        cuenta.setRol(changeRoleRequestDTO.getRol());
        accountRepository.save(cuenta);

        return new ChangeRoleRequestDTO(cuenta.getIdcuenta(), cuenta.getRol());
    }
}
