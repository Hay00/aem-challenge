package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductService {
    List<Product> getProducts(boolean orderByPrice, String contains) throws SQLException;

    Product getById(int id) throws SQLException;

    Product save(Product product) throws SQLException;

    List<Product> save(List<Product> products) throws SQLException;

    void update(Product product) throws SQLException, ProductNotFoundException;

    void delete(int id) throws SQLException, ProductNotFoundException;

    void delete(List<Integer> ids) throws SQLException;

    String strToJson(Object obj);
}