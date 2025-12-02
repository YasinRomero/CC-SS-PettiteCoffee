package com.cursoIntegrador.lePettiteCoffe.Model.DTO.Purchase;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PurchaseRequestDTO {

    /**
     * DTO usado para solicitar la creación de una compra.
     * <p>
     * Contiene el monto procesado, la lista de productos y datos de envío.
     */

    private BigDecimal montoProcesado;
    private List<PurchaseProductDTO> productos;
    private String cityDelivery;
    private String addressDelivery;

}
