package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.Purchase;
import com.cursoIntegrador.lePettiteCoffe.Model.Entity.PurchaseDetails;

import lombok.Data;

@Data
public class PurhcaseHistoryDTO {

    /**
     * DTO que representa el historial de una compra para mostrar en la API.
     */
    private Long id;
    private String cityDelivery;
    private String addressDelivery;
    private BigDecimal total;
    private List<PurchaseHistoryDetailDTO> detalles = new ArrayList<>();;

    public PurhcaseHistoryDTO(Purchase purchase) {
        /**
         * Construye un PurhcaseHistoryDTO mapeando una entidad Purchase.
         *
         * @param purchase entidad Purchase que contiene el historial y detalles
         */
        this.id = purchase.getIdpurchase();
        this.cityDelivery = purchase.getCityDelivery();
        this.addressDelivery = purchase.getAddressDelivery();
        this.total = purchase.getTotalAmount();

        for (PurchaseDetails detail : purchase.getDetails()) {

            this.detalles.add(new PurchaseHistoryDetailDTO(detail));

        }
    }

}
