package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao {
    List<Product> getAll(boolean orderByPrice, String contains) throws SQLException;

    List<Product> getAllWithParams(String nome, String categoria) throws SQLException;

    Product getById(int id) throws SQLException, ProductNotFoundException;

    Product save(Product product) throws SQLException;

    Product update(int id, Product product) throws SQLException;

    void delete(int id) throws SQLException;

}
