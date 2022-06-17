package com.adobe.aem.challenge.core.servlets;

import com.adobe.aem.challenge.core.exceptions.ClientNotFoundException;
import com.adobe.aem.challenge.core.exceptions.InvoiceNotFoundException;
import com.adobe.aem.challenge.core.exceptions.ProductNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Invoice;
import com.adobe.aem.challenge.core.responses.InvoiceResponse;
import com.adobe.aem.challenge.core.service.InvoiceService;
import com.adobe.aem.challenge.core.utils.RequestResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/app/notas_fiscais",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
        SLING_SERVLET_EXTENSIONS + "=" + "json"
})
public class InvoiceServlet extends SlingAllMethodsServlet {

    private final InvoiceService invoiceService;

    @Activate
    public InvoiceServlet(@Reference InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            if (req.getParameter("numero") != null) {
                List<InvoiceResponse> invoices = invoiceService.getByNumero(Integer.parseInt(req.getParameter("numero")));
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(invoices));
            } else if (req.getParameter("cliente") != null) {
                List<InvoiceResponse> invoices = invoiceService.getByClient(Integer.parseInt(req.getParameter("cliente")));
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(invoices));
            } else {
                List<Invoice> invoices = invoiceService.getAll();
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(invoices));
            }
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid id");
        } catch (InvoiceNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            Invoice invoice = new ObjectMapper().readValue(req.getInputStream(), Invoice.class);
            invoiceService.save(invoice);
            RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(invoice));
        } catch (JacksonException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid invoice");
        } catch (RequiredFieldsException | ProductNotFoundException | ClientNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
