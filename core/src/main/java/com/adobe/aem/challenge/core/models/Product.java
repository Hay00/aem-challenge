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
public class Product {

    private int id;
    private String nome;
    private String categoria;
    private BigDecimal preco;

    public boolean isValid() {
        return id > 0 && nome != null && !nome.isEmpty() && categoria != null && !categoria.isEmpty() && preco != null;
    }
}
