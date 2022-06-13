package com.adobe.aem.challenge.core.servlets;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;
import com.adobe.aem.challenge.core.service.ProductService;
import com.adobe.aem.challenge.core.utils.RequestResponse;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Reference
    private ProductService productService;

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            if (req.getParameter("id") != null) {
                Product product = productService.getProduct(Integer.parseInt(req.getParameter("id")));
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(product));
            } else {
                boolean orderByPrice = req.getParameter("sort") != null && req.getParameter("sort").equals("preco");
                String contains = req.getParameter("contains") != null ? req.getParameter("contains") : "";
                List<Product> products = productService.getProducts(orderByPrice, contains);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(products));
            }
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
        try {
            Product newProduct = new ObjectMapper().readValue(req.getReader(), Product.class);
            Product savedProduct = productService.save(newProduct);
            resp.getWriter().write(productService.strToJson(savedProduct));
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
        try {
            Product product = new ObjectMapper().readValue(req.getReader(), Product.class);
            if (!product.isValid()) {
                RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid product data");
                return;
            }

            int id = Integer.parseInt(req.getParameter("id"));
            Product updatedProduct = productService.update(id, product);
            RequestResponse.send(resp, HttpServletResponse.SC_OK, productService.strToJson(updatedProduct));
        } catch (StreamReadException | DatabindException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid product data");
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(SlingHttpServletRequest req, SlingHttpServletResponse resp) throws IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            productService.delete(id);
            RequestResponse.send(resp, HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
        } catch (ProductNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
