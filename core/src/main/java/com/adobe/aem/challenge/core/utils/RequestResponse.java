package com.adobe.aem.challenge.core.utils;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestResponse {

    private static final String CONTENT_TYPE = "application/json";

    private RequestResponse() {
    }

    public static void send(SlingHttpServletResponse resp, int statusCode) {
        resp.setContentType(CONTENT_TYPE);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
    }

    public static void send(SlingHttpServletResponse resp, int statusCode, String body) throws IOException {
        resp.setContentType(CONTENT_TYPE);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
        resp.getWriter().write(body);
    }

    public static void errorMessage(SlingHttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setContentType(CONTENT_TYPE);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setStatus(statusCode);
        resp.getWriter().write(new ResponseMessage(message).toJson());
    }
}
