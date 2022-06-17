package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.dao.ClientDao;
import com.adobe.aem.challenge.core.dao.InvoiceDao;
import com.adobe.aem.challenge.core.dao.ProductDao;
import com.adobe.aem.challenge.core.exceptions.ClientNotFoundException;
import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Invoice;
import com.adobe.aem.challenge.core.responses.InvoiceResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.SQLException;
import java.util.List;

@Component(immediate = true, service = InvoiceService.class)
public class InvoiceServiceImpl implements InvoiceService {

    @Reference
    private DatabaseService databaseService;

    @Reference
    private InvoiceDao invoiceDao;

    @Reference
    private ProductDao productDao;

    @Reference
    private ClientDao clientDao;

    @Override
    public List<Invoice> getAll() throws SQLException {
        return invoiceDao.getAll();
    }

    @Override
    public List<InvoiceResponse> getByNumero(int id) throws SQLException {
        return invoiceDao.getByNumero(id);
    }

    @Override
    public List<InvoiceResponse> getByClient(int id) throws SQLException {
        return invoiceDao.getByClient(id);
    }

    @Override
    public void save(Invoice invoice) throws SQLException, ClientNotFoundException, ProductNotFoundException, RequiredFieldsException {
        if (invoice.hasMissingFields())
            throw new RequiredFieldsException("Missing required fields in invoice");

        clientDao.getById(invoice.getIdCliente());
        productDao.getById(invoice.getIdProduto());
        invoiceDao.save(invoice);
    }
}
