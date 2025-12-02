package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseProductDTO {

    /**
     * DTO que representa un producto dentro de una solicitud de compra, incluyendo cantidad e instrucciones.
     */
    private Integer idProducto;
    private Integer quantity;
    private String instructions;
}
