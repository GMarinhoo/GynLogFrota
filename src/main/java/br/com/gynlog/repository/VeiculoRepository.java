package br.com.gynlog.repository;

import br.com.gynlog.enums.CategoriaVeiculo;
import br.com.gynlog.model.Veiculo;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class VeiculoRepository {

    public void salvar(Veiculo v) throws SQLException {
        String sql = "INSERT INTO veiculo (placa, categoria, marca, modelo, ano_fab, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, v.getPlaca().toUpperCase().trim());
            ps.setString(2, v.getCategoria().name());
            ps.setString(3, v.getMarca().trim());
            ps.setString(4, v.getModelo().trim());
            ps.setInt(5, v.getAnoFabricacao());
            ps.setBoolean(6, v.isAtivo());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) v.setIdVeiculo(rs.getInt(1));
            }
        }
    }

    public List<Veiculo> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM veiculo ORDER BY modelo";
        List<Veiculo> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Veiculo buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM veiculo WHERE id_veiculo = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Veiculo> buscarInativos() throws SQLException {
        String sql = "SELECT * FROM veiculo WHERE status = 0 ORDER BY modelo";
        List<Veiculo> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Veiculo> buscarPorCategoria(CategoriaVeiculo categoria) throws SQLException {
        String sql = "SELECT * FROM veiculo WHERE categoria = ? ORDER BY modelo";
        List<Veiculo> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, categoria.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void atualizar(Veiculo v) throws SQLException {
        String sql = "UPDATE veiculo SET placa=?, categoria=?, marca=?, modelo=?, ano_fab=?, status=? WHERE id_veiculo=?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca().toUpperCase().trim());
            ps.setString(2, v.getCategoria().name());
            ps.setString(3, v.getMarca().trim());
            ps.setString(4, v.getModelo().trim());
            ps.setInt(5, v.getAnoFabricacao());
            ps.setBoolean(6, v.isAtivo());
            ps.setInt(7, v.getIdVeiculo());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM veiculo WHERE id_veiculo = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Veiculo mapear(ResultSet rs) throws SQLException {
        return new Veiculo(
                rs.getInt("id_veiculo"),
                rs.getString("placa"),
                CategoriaVeiculo.valueOf(rs.getString("categoria")),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getInt("ano_fab"),
                rs.getBoolean("status")
        );
    }
}
