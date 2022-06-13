package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;
import com.adobe.aem.challenge.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = ProductDao.class)
public class ProductDaoImpl implements ProductDao {

    @Reference
    private DatabaseService databaseService;

    @Override
    public List<Product> getAll(boolean orderByPrice, String contains) throws SQLException {
        String sql = "SELECT * FROM products";
        if (orderByPrice) sql += " ORDER BY preco";
        // TODO: Validar implementação do contains
        if (contains != null && !contains.isEmpty()) sql += " WHERE nome LIKE '%" + contains + "%'";

        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<Product> productList = new ArrayList<>();
            while (rs.next()) {
                Product product = this.toModel(rs);
                productList.add(product);
            }
            return productList;
        }
    }

    @Override
    public Product getById(int id) throws SQLException, ProductNotFoundException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            if (rs.next()) {
                return this.toModel(rs);
            }
            throw new ProductNotFoundException("Product not found");
        }
    }

    @Override
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO products (nome, categoria, preco) VALUES (?, ?, ?)";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getCategoria());
            ps.setBigDecimal(3, product.getPreco());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                product.setId(rs.getInt(1));
                return product;
            } else {
                throw new SQLException("No ID obtained on creation");
            }
        }
    }


    @Override
    public Product update(int id, Product product) throws SQLException {
        String sql = "UPDATE products SET nome = ?, categoria = ?, preco = ? WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getCategoria());
            ps.setBigDecimal(3, product.getPreco());
            ps.setInt(4, id);
            ps.executeUpdate();
            return product;
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


    @Override
    public List<Product> getAllWithParams(String nome, String categoria) throws SQLException {
        String sql = "SELECT * FROM products WHERE nome LIKE ? AND categoria LIKE ?";
        try (Connection connection = databaseService.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + nome + "%");
            ps.setString(2, "%" + categoria + "%");
            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<Product> productList = new ArrayList<>();
            while (rs.next()) {
                Product product = this.toModel(rs);
                productList.add(product);
            }
            return productList;
        }
    }

    /**
     * Converts a ResultSet to a Product
     *
     * @param rs ResultSet
     * @return Product
     * @throws SQLException if any error occurs
     */
    private Product toModel(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setNome(rs.getString("nome"));
        product.setCategoria(rs.getString("categoria"));
        product.setPreco(rs.getBigDecimal("preco"));
        return product;
    }
}
