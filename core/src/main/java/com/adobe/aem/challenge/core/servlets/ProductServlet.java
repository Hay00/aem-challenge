package com.adobe.aem.challenge.core.servlets;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Product;
import com.adobe.aem.challenge.core.service.ProductService;
import com.adobe.aem.challenge.core.utils.RequestResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/app/products",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE,
        SLING_SERVLET_EXTENSIONS + "=" + "json"
})
public class ProductServlet extends SlingAllMethodsServlet {

    private static final String INVALID_ID = "Invalid id";

    private final ProductService productService;

    @Activate
    public ProductServlet(@Reference ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            if (req.getParameter("id") != null) {
                Product product = productService.getById(Integer.parseInt(req.getParameter("id")));
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(product));
            } else {
                boolean orderByPrice = req.getParameter("sort") != null && req.getParameter("sort").equals("preco");
                String contains = req.getParameter("contains") != null ? req.getParameter("contains") : "";
                List<Product> products = productService.getProducts(orderByPrice, contains);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(products));
            }
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            String jsonBody = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
            if (jsonBody.contains("[")) {
                // Array of products from json
                List<Product> products = new ObjectMapper().readValue(jsonBody, new TypeReference<List<Product>>() {
                });
                productService.save(products);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(products));
            } else {
                // Just one product from json
                Product newProduct = new ObjectMapper().readValue(jsonBody, Product.class);
                productService.save(newProduct);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(newProduct));
            }
        } catch (JacksonException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid product");
        } catch (RequiredFieldsException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            Product product = new ObjectMapper().readValue(req.getReader(), Product.class);
            productService.update(product);
            RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(product));
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (JacksonException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid product data");
        } catch (RequiredFieldsException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            List<Integer> ids = new ObjectMapper().readValue(req.getReader(), new TypeReference<List<Integer>>() {
            });
            productService.delete(ids);
            RequestResponse.send(resp, HttpServletResponse.SC_OK);
        } catch (JacksonException | NumberFormatException | NullPointerException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
