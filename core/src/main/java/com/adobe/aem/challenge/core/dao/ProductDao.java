package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao {
    List<Product> getAll(boolean orderByPrice, String contains) throws SQLException;

    Product getById(int id) throws SQLException, ProductNotFoundException;

    void save(Product product) throws SQLException;

    void save(List<Product> products) throws SQLException;

    void update(Product product) throws SQLException;

    void delete(int id) throws SQLException;

    void delete(List<Integer> products) throws SQLException;
}
