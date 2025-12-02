package com.cursoIntegrador.lePettiteCoffe.Model.Entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Purchase {

    /**
     * Entidad que representa una compra realizada por una cuenta.
     * <p>
     * Incluye referencia a la cuenta, detalles de la compra, datos de entrega y total.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpurchase;

    @ManyToOne
    @JoinColumn(name = "idcuenta", nullable = false)
    private Cuenta cuenta;

    @Column(name = "cityDelivery")
    private String cityDelivery;

    @Column(name = "addressDelivery")
    private String addressDelivery;

    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
    private List<PurchaseDetails> details;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;
}
