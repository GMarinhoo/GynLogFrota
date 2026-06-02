package br.com.gynlog.repository;

import br.com.gynlog.model.TipoDespesa;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TipoDespesaRepository {

    public List<TipoDespesa> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM tipo_despesa ORDER BY descricao";
        List<TipoDespesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public TipoDespesa buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tipo_despesa WHERE id_tipo_despesa = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void salvar(TipoDespesa t) throws SQLException {
        String sql = "INSERT INTO tipo_despesa (descricao) VALUES (?)";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getDescricao().trim());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) t.setIdTipoDespesa(rs.getInt(1));
            }
        }
    }

    public void atualizar(TipoDespesa t) throws SQLException {
        String sql = "UPDATE tipo_despesa SET descricao=? WHERE id_tipo_despesa=?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getDescricao().trim());
            ps.setInt(2, t.getIdTipoDespesa());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tipo_despesa WHERE id_tipo_despesa = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private TipoDespesa mapear(ResultSet rs) throws SQLException {
        return new TipoDespesa(
                rs.getInt("id_tipo_despesa"),
                rs.getString("descricao")
        );
    }
}
