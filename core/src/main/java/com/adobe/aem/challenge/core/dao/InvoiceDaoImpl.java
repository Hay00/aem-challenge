package com.adobe.aem.challenge.core.dao;

import com.adobe.aem.challenge.core.models.Invoice;
import com.adobe.aem.challenge.core.responses.InvoiceResponse;
import com.adobe.aem.challenge.core.service.DatabaseService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true, service = InvoiceDao.class)
public class InvoiceDaoImpl implements InvoiceDao {

    @Reference
    private DatabaseService databaseService;

    @Override
    public List<Invoice> getAll() throws SQLException {
        String sql = "SELECT * FROM notas_fiscais";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<Invoice> invoices = new ArrayList<>();

            while (rs.next()) invoices.add(this.toModel(rs));
            return invoices;
        }
    }

    @Override
    public List<InvoiceResponse> getByNumero(int id) throws SQLException {
        String sql = "select nf.numero, cl.nome as cliente, pr.nome as produto, nf.quantidade, nf.valor " +
                "from notas_fiscais as nf " +
                "inner join products as pr on pr.id = nf.idproduto " +
                "inner join clients as cl on cl.id = nf.idcliente " +
                "where nf.numero = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();

            List<InvoiceResponse> invoices = new ArrayList<>();

            while (rs.next()) invoices.add(this.toResponse(rs));
            return invoices;
        }
    }

    @Override
    public List<InvoiceResponse> getByClient(int id) throws SQLException {
        String sql = "select nf.numero, cl.nome as cliente, pr.nome as produto, nf.quantidade, nf.valor " +
                "from notas_fiscais as nf \n" +
                "inner join products as pr on pr.id = nf.idproduto\n" +
                "inner join clients as cl on cl.id = nf.idcliente\n" +
                "where nf.idcliente = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            List<InvoiceResponse> invoices = new ArrayList<>();

            while (rs.next()) invoices.add(this.toResponse(rs));
            return invoices;
        }
    }

    @Override
    public void save(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO notas_fiscais (numero, idcliente, idproduto, quantidade, valor) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, invoice.getNumero());
            ps.setInt(2, invoice.getIdCliente());
            ps.setInt(3, invoice.getIdProduto());
            ps.setInt(4, invoice.getQuantidade());
            ps.setBigDecimal(5, invoice.getValor());
            ps.executeUpdate();
        }
    }

    private Invoice toModel(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setNumero(rs.getInt("numero"));
        invoice.setIdProduto(rs.getInt("idProduto"));
        invoice.setIdCliente(rs.getInt("idCliente"));
        invoice.setQuantidade(rs.getInt("quantidade"));
        invoice.setValor(rs.getBigDecimal("valor"));
        return invoice;
    }

    private InvoiceResponse toResponse(ResultSet rs) throws SQLException {
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setNumero(rs.getInt("numero"));
        invoiceResponse.setProduto(rs.getString("produto"));
        invoiceResponse.setCliente(rs.getString("cliente"));
        invoiceResponse.setQuantidade(rs.getInt("quantidade"));
        invoiceResponse.setValor(rs.getBigDecimal("valor"));
        return invoiceResponse;
    }
}
