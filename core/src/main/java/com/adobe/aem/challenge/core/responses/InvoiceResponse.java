package com.adobe.aem.challenge.core.responses;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceResponse {

    private int numero;
    private String produto;
    private String cliente;
    private int quantidade;
    private BigDecimal valor;

}
