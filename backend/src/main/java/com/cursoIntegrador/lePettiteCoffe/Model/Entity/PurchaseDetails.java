package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchasedetails")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PurchaseDetails {

    /**
     * Entidad que representa una línea de detalle dentro de una compra.
     * <p>
     * Contiene referencia al producto, cantidad e instrucciones específicas.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idpurchase")
    private Purchase purchase;

    @ManyToOne
    @JoinColumn(name = "idproducto")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = true)
    private String instructions;
}
