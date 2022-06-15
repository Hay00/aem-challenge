package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.exceptions.ClientNotFoundException;
import com.adobe.aem.challenge.core.models.Client;
import com.adobe.aem.challenge.core.service.DatabaseService;
import com.adobe.aem.challenge.core.utils.SQLCloseable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = ClientDao.class)
public class ClientDaoImpl implements ClientDao, Serializable {

    @Reference
    private DatabaseService databaseService;

    @Override
    public List<Client> getAll() throws SQLException {
        String sql = "SELECT * FROM clients";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<Client> clients = new ArrayList<>();

            while (rs.next()) clients.add(this.toModel(rs));
            return clients;
        }
    }

    @Override
    public Client getById(int id) throws SQLException {
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();

            if (rs.next()) return this.toModel(rs);
            throw new ClientNotFoundException("Client not found");
        }
    }

    @Override
    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO clients (nome) VALUES (?)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, client.getNome());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                client.setId(rs.getInt(1));
            else
                throw new SQLException("No ID obtained on creation");
        }
    }

    @Override
    public void save(List<Client> clients) throws SQLException {
        String sql = "INSERT INTO clients (nome) VALUES (?)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (Client client : clients) {
                ps.setString(1, client.getNome());
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next())
                    client.setId(rs.getInt(1));
                else
                    throw new SQLException("No ID obtained on creation");
            }
        }
    }

    @Override
    public void update(Client client) throws SQLException {
        String sql = "UPDATE clients SET nome = ? WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getNome());
            ps.setInt(2, client.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int deleted = ps.executeUpdate();

            if (deleted == 0) throw new ClientNotFoundException("Client id {" + id + "} not found");
        }
    }

    @Override
    public void delete(List<Integer> clients) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             SQLCloseable finish = conn::rollback) {

            int deleted = 0;
            conn.setAutoCommit(false);
            for (Integer id : clients) {
                ps.setInt(1, id);
                deleted = ps.executeUpdate();

                if (deleted == 0) throw new ClientNotFoundException("Client id {" + id + "} not found");
            }
            conn.commit();
        }
    }

    /**
     * Convert a ResultSet to a Client
     *
     * @param rs ResultSet
     * @return Client
     * @throws SQLException if any error occurs
     */
    private Client toModel(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("id"));
        client.setNome(rs.getString("nome"));
        return client;
    }
}
