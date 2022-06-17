package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.dao.ClientDao;
import com.adobe.aem.challenge.core.exceptions.ClientNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Client;
import com.google.gson.Gson;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Component(immediate = true, service = ClientService.class)
public class ClientServiceImpl implements ClientService, Serializable {

    @Reference
    private DatabaseService databaseService;

    @Reference
    private ClientDao clientDao;

    @Override
    public List<Client> getClients() throws SQLException {
        return clientDao.getAll();
    }

    @Override
    public Client getById(int id) throws SQLException, ClientNotFoundException {
        return clientDao.getById(id);
    }

    @Override
    public Client save(Client client) throws SQLException, RequiredFieldsException {
        if (client.hasMissingFields())
            throw new RequiredFieldsException("Missing required fields in client");

        clientDao.save(client);
        return client;
    }

    @Override
    public List<Client> save(List<Client> clients) throws SQLException,RequiredFieldsException {
        for (Client client : clients) {
            if (client.hasMissingFields())
                throw new RequiredFieldsException("Missing required fields in client {" + clients.indexOf(client) + "}");
        }
        clientDao.save(clients);
        return clients;
    }

    @Override
    public void update(Client client) throws SQLException, ClientNotFoundException, RequiredFieldsException {
        if (!client.isValid())
            throw new RequiredFieldsException("Missing required fields or id in client");

        clientDao.getById(client.getId());
        clientDao.update(client);
    }

    @Override
    public void delete(int id) throws SQLException, ClientNotFoundException {
        clientDao.getById(id);
        clientDao.delete(id);
    }

    @Override
    public void delete(List<Integer> ids) throws SQLException, ClientNotFoundException {
        clientDao.delete(ids);
    }

    @Override
    public String strToJson(Object obj) {
        return new Gson().toJson(obj);
    }
}
