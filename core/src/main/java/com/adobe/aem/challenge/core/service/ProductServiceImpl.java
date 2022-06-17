package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.dao.ProductDao;
import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Product;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Component(immediate = true, service = ProductService.class)
public class ProductServiceImpl implements ProductService, Serializable {

    @Reference
    private DatabaseService databaseService;

    @Reference
    private ProductDao productDao;

    @Override
    public List<Product> getProducts(boolean orderByPrice, String contains) throws SQLException {
        return productDao.getAll(orderByPrice, contains);
    }

    @Override
    public Product getById(int id) throws SQLException, ProductNotFoundException {
        return productDao.getById(id);
    }

    @Override
    public Product save(Product product) throws SQLException, RequiredFieldsException {
        if (product.hasMissingFields())
            throw new RequiredFieldsException("Missing required fields in product");
        productDao.save(product);
        return product;
    }

    @Override
    public List<Product> save(List<Product> products) throws SQLException, RequiredFieldsException {
        for (Product product : products) {
            if (product.hasMissingFields())
                throw new RequiredFieldsException("Missing required fields in product {" + products.indexOf(product) + "}");
        }
        productDao.save(products);
        return products;
    }

    @Override
    public void update(Product product) throws SQLException, ProductNotFoundException, RequiredFieldsException {
        if (!product.isValid())
            throw new RequiredFieldsException("Missing required fields or id in product");

        productDao.getById(product.getId());
        productDao.update(product);
    }

    @Override
    public void delete(int id) throws SQLException, ProductNotFoundException {
        productDao.getById(id);
        productDao.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) throws SQLException, ProductNotFoundException {
        productDao.delete(ids);
    }

    @Override
    public String strToJson(Object obj) {
        return new Gson().toJson(obj);
    }
}
