package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.models.Client;

import java.sql.SQLException;
import java.util.List;

public interface ClientDao {

    List<Client> getAll() throws SQLException;

    Client getById(int id) throws SQLException;

    void save(Client client) throws SQLException;

    void save(List<Client> clients) throws SQLException;

    void update(Client client) throws SQLException;

    void delete(int id) throws SQLException;

    void delete(List<Integer> clients) throws SQLException;
}
