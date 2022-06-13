package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.dao.ProductDao;
import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.SQLException;
import java.util.List;

@Component(immediate = true, service = ProductService.class)
public class ProductServiceImpl implements ProductService {

    @Reference
    private DatabaseService databaseService;

    @Reference
    private ProductDao productDao;

    @Override
    public List<Product> getProducts(boolean orderByPrice, String contains) throws SQLException {
        return productDao.getAll(orderByPrice, contains);
    }

    @Override
    public Product getProduct(int id) throws SQLException {
        return productDao.getById(id);
    }

    @Override
    public Product save(Product product) throws SQLException {
        return productDao.save(product);
    }

    @Override
    public Product update(int id, Product product) throws SQLException, ProductNotFoundException {
        productDao.getById(id);
        return productDao.update(id, product);
    }

    @Override
    public void delete(int id) throws SQLException, ProductNotFoundException {
        productDao.getById(id);
        productDao.delete(id);
    }

    @Override
    public String strToJson(Object obj) {
        return new Gson().toJson(obj);
    }
}
