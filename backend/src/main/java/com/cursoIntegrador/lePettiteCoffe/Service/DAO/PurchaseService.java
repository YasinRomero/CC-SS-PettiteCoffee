package com.cursoIntegrador.lePettiteCoffe.Service.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase.PurchaseProductDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase.PurchaseRequestDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase.PurhcaseHistoryDTO;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Cuenta;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Purchase;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.PurchaseDetails;
import com.cursoIntegrador.lePettiteCoffe.Model.Security.CustomUserDetails;
import com.cursoIntegrador.lePettiteCoffe.Repository.AccountRepository;
import com.cursoIntegrador.lePettiteCoffe.Repository.ProductRepository;
import com.cursoIntegrador.lePettiteCoffe.Repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión de compras (Purchase).
 * Proporciona métodos para guardar nuevas compras y recuperar el historial de compras de un usuario.
 */
@Service
@RequiredArgsConstructor
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Guarda una nueva compra en la base de datos.
     * Crea la entidad Purchase a partir del DTO, asocia los detalles de los productos
     * y guarda la compra completa.
     *
     * @param userDetails Los detalles del usuario autenticado que realiza la compra.
     * @param purchaseDTO El DTO con la información de la compra y la lista de productos comprados.
     * @return El mismo DTO de solicitud de compra que fue procesado.
     */
    public PurchaseRequestDTO savePurchase(CustomUserDetails userDetails, PurchaseRequestDTO purchaseDTO) {

        Cuenta cuenta = accountRepository.findByEmail(userDetails.getUsername());

        List<PurchaseDetails> realDetails = new ArrayList<>();

        Purchase purchase = new Purchase();
        purchase.setCuenta(cuenta);
        purchase.setCityDelivery(purchaseDTO.getCityDelivery());
        purchase.setAddressDelivery(purchaseDTO.getAddressDelivery());
        purchase.setTotalAmount(purchaseDTO.getMontoProcesado());

        for (PurchaseProductDTO dtoProduct : purchaseDTO.getProductos()) {
            var product = productRepository.findById(dtoProduct.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + dtoProduct.getIdProducto()));

            PurchaseDetails detail = new PurchaseDetails();
            detail.setProduct(product);
            detail.setQuantity(dtoProduct.getQuantity());
            detail.setPurchase(purchase);
            detail.setInstructions(dtoProduct.getInstructions());

            realDetails.add(detail);
        }

        purchase.setDetails(realDetails);

        purchaseRepository.save(purchase);

        return purchaseDTO;
    }

    /**
     * Recupera el historial de compras de un usuario autenticado.
     * Busca todas las compras realizadas por el ID de la cuenta del usuario y las convierte a DTOs de historial.
     *
     * @param userDetails Los detalles del usuario autenticado cuya historia de compras se desea recuperar.
     * @return Una lista de objetos PurhcaseHistoryDTO que representan el historial de compras.
     */
    public List<PurhcaseHistoryDTO> getHistory(CustomUserDetails userDetails) {

        List<Purchase> purchases = purchaseRepository.findAllByCuentaIdcuenta(userDetails.getCuenta().getIdcuenta());
        List<PurhcaseHistoryDTO> dtos = new ArrayList<>();

        for (Purchase purchase : purchases) {
            dtos.add(new PurhcaseHistoryDTO(purchase));
        }

        return dtos;
    }

}
