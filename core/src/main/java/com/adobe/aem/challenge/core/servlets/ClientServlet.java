package com.adobe.aem.challenge.core.servlets;

import com.adobe.aem.challenge.core.exceptions.ClientNotFoundException;
import com.adobe.aem.challenge.core.exceptions.RequiredFieldsException;
import com.adobe.aem.challenge.core.models.Client;
import com.adobe.aem.challenge.core.service.ClientService;
import com.adobe.aem.challenge.core.utils.RequestResponse;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.apache.sling.api.servlets.ServletResolverConstants.*;

@Component(service = {Servlet.class}, property = {
        SLING_SERVLET_PATHS + "=" + "/bin/app/clients",
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_GET,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_POST,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_PUT,
        SLING_SERVLET_METHODS + "=" + HttpConstants.METHOD_DELETE,
        SLING_SERVLET_EXTENSIONS + "=" + "json"
})
public class ClientServlet extends SlingAllMethodsServlet {

    private static final String INVALID_ID = "Invalid id";

    private final ClientService clientService;

    @Activate
    public ClientServlet(@Reference ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            if (req.getParameter("id") != null) {
                Client client = clientService.getById(Integer.parseInt(req.getParameter("id")));
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(client));
            } else {
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(clientService.getClients()));
            }
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (ClientNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            String jsonBody = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
            if (jsonBody.contains("[")) {
                // Array of clients from json
                List<Client> clients = new ObjectMapper().readValue(jsonBody, new TypeReference<List<Client>>() {
                });
                clientService.save(clients);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(clients));
            } else {
                // Just one client from json
                Client newClient = new ObjectMapper().readValue(jsonBody, Client.class);
                clientService.save(newClient);
                RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(newClient));
            }
        } catch (JacksonException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid client");
        } catch (RequiredFieldsException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPut(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            Client client = new ObjectMapper().readValue(req.getReader(), Client.class);
            clientService.update(client);
            RequestResponse.send(resp, HttpServletResponse.SC_OK, new Gson().toJson(client));
        } catch (NumberFormatException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (JacksonException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid client data");
        } catch (RequiredFieldsException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ClientNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doDelete(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) throws IOException {
        try {
            List<Integer> ids = new ObjectMapper().readValue(req.getReader(), new TypeReference<List<Integer>>() {
            });
            clientService.delete(ids);
            RequestResponse.send(resp, HttpServletResponse.SC_OK);
        } catch (JacksonException | NumberFormatException | NullPointerException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_BAD_REQUEST, INVALID_ID);
        } catch (ClientNotFoundException e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            RequestResponse.errorMessage(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
