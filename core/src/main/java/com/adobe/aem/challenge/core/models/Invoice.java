package com.adobe.aem.challenge.core.models;

import lombok.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

import java.math.BigDecimal;

@Model(adaptables = {Resource.class})
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    private int numero;
    private int idProduto;
    private int idCliente;
    private int quantidade;
    private BigDecimal valor;

    public boolean hasMissingFields() {
        return idProduto == 0 || idCliente == 0 || quantidade == 0 || valor == null;
    }

    public boolean isValid() {
        return numero > 0 && idProduto > 0 && idCliente > 0 && quantidade > 0 && valor != null;
    }
}
