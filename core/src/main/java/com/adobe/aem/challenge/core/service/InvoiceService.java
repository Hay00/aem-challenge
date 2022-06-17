package com.adobe.aem.challenge.core.service;

import com.adobe.aem.challenge.core.models.Invoice;
import com.adobe.aem.challenge.core.responses.InvoiceResponse;

import java.sql.SQLException;
import java.util.List;

public interface InvoiceService {

    List<Invoice> getAll() throws SQLException;

    List<InvoiceResponse> getByNumero(int id) throws SQLException;

    List<InvoiceResponse> getByClient(int id) throws SQLException;

    void save(Invoice invoice) throws SQLException;
}
