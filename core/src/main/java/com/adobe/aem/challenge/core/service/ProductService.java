package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    List<Product> getProducts(boolean orderByPrice, String contains) throws SQLException;

    Product getProduct(int id) throws SQLException;

    Product save(Product product) throws SQLException;

    Product update(int id, Product product) throws SQLException, ProductNotFoundException;

    void delete(int id) throws SQLException, ProductNotFoundException;

    String strToJson(Object obj);
}