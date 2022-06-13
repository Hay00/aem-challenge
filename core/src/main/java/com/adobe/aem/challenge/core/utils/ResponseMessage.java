package com.adobe.aem.challenge.core.utils;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseMessage {
    private String message;

    public String toJson() {
        return new Gson().toJson(this);
    }
}
