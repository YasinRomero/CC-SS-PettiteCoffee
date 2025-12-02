package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase;

import java.math.BigDecimal;

import com.cursoIntegrador.lePettiteCoffe.Model.Entity.PurchaseDetails;

import lombok.Data;

@Data
public class PurchaseHistoryDetailDTO {

    /**
     * DTO que representa una línea de detalle dentro del historial de compra.
     */
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String instructions;

    public PurchaseHistoryDetailDTO(PurchaseDetails purchaseDetail) {
        /**
         * Construye el DTO de detalle a partir de PurchaseDetails.
         *
         * @param purchaseDetail entidad PurchaseDetails con la información de la línea
         */
        this.productId = purchaseDetail.getProduct().getIdproducto();
        this.productName = purchaseDetail.getProduct().getNombre();
        this.quantity = purchaseDetail.getQuantity();
        this.price = purchaseDetail.getProduct().getPrecioventa();
        this.instructions = purchaseDetail.getInstructions();
    }

}
