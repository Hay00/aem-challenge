package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.models.Client;

import java.sql.SQLException;
import java.util.List;

public interface ClientService {

    List<Client> getClients() throws SQLException;

    Client getById(int id) throws SQLException;

    Client save(Client client) throws SQLException;

    List<Client> save(List<Client> clients) throws SQLException;

    void update(Client client) throws SQLException;

    void delete(int id) throws SQLException;

    void delete(List<Integer> ids) throws SQLException;

    String strToJson(Object obj);
}
