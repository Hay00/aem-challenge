package com.adobe.aem.challenge.core.models;

import lombok.*;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = {Resource.class})
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private int id;
    private String nome;

    public boolean isValid() {
        return id > 0 && nome != null && !nome.isEmpty();
    }
}
