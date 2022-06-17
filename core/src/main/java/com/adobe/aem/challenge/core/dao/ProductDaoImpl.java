package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.models.Product;
import com.adobe.aem.challenge.core.service.DatabaseService;
import com.adobe.aem.challenge.core.utils.SQLCloseable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = ProductDao.class)
public class ProductDaoImpl implements ProductDao, Serializable {

    @Reference
    private DatabaseService databaseService;

    @Override
    public List<Product> getAll(boolean orderByPrice, String contains) throws SQLException {
        String sql = "SELECT * FROM products";
        if (orderByPrice) sql += " ORDER BY preco";
        if (contains != null && !contains.isEmpty()) sql += " WHERE nome LIKE ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (contains != null && !contains.isEmpty()) {
                ps.setString(1, "%" + contains + "%");
            }
            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<Product> productList = new ArrayList<>();

            while (rs.next()) productList.add(this.toModel(rs));
            return productList;
        }
    }

    @Override
    public Product getById(int id) throws SQLException, ProductNotFoundException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) return this.toModel(rs);
            throw new ProductNotFoundException("Product not found");
        }
    }

    @Override
    public void save(Product product) throws SQLException {
        String sql = "INSERT INTO products (nome, categoria, preco) VALUES (?, ?, ?)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getCategoria());
            ps.setBigDecimal(3, product.getPreco());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                product.setId(rs.getInt(1));
            else
                throw new SQLException("No ID obtained on creation");
        }
    }

    @Override
    public void save(List<Product> products) throws SQLException {
        String sql = "INSERT INTO products (nome, categoria, preco) VALUES (?, ?, ?)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Product product : products) {
                ps.setString(1, product.getNome());
                ps.setString(2, product.getCategoria());
                ps.setBigDecimal(3, product.getPreco());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                    product.setId(rs.getInt(1));
                else
                    throw new SQLException("No ID obtained on creation");
            }
        }
    }


    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET nome = ?, categoria = ?, preco = ? WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getCategoria());
            ps.setBigDecimal(3, product.getPreco());
            ps.setInt(4, product.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int deleted = ps.executeUpdate();

            if (deleted == 0) throw new ProductNotFoundException("Product id {" + id + "} not found");
        }
    }

    @Override
    public void delete(List<Integer> products) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             SQLCloseable finish = conn::rollback) {

            int deleted = 0;
            conn.setAutoCommit(false);
            for (Integer id : products) {
                ps.setInt(1, id);
                deleted = ps.executeUpdate();

                if (deleted == 0) throw new ProductNotFoundException("Product id {" + id + "} not found");
            }
            conn.commit();
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
