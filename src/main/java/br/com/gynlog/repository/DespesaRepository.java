package br.com.gynlog.repository;

import br.com.gynlog.model.Despesa;
import br.com.gynlog.model.TipoDespesa;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DespesaRepository {

    public void salvar(Despesa d) throws SQLException {
        String sql = "INSERT INTO despesa (id_veiculo, id_tipo_despesa, descricao, data, valor, gerada_por_abastecimento) VALUES (?,?,?,?,?,?)";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdVeiculo());
            ps.setInt(2, d.getTipoDespesa().getIdTipoDespesa());
            ps.setString(3, d.getDescricao().trim());
            ps.setDate(4, Date.valueOf(d.getData()));
            ps.setDouble(5, d.getValor());
            ps.setBoolean(6, d.isGeradaPorAbastecimento());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) d.setIdDespesa(rs.getInt(1));
            }
        }
    }

    public List<Despesa> buscarTodos() throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "ORDER BY d.data DESC";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Despesa buscarPorId(int id) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE d.id_despesa = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void atualizar(Despesa d) throws SQLException {
        String sql = "UPDATE despesa SET id_veiculo=?, id_tipo_despesa=?, descricao=?, data=?, valor=? WHERE id_despesa=?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdVeiculo());
            ps.setInt(2, d.getTipoDespesa().getIdTipoDespesa());
            ps.setString(3, d.getDescricao().trim());
            ps.setDate(4, Date.valueOf(d.getData()));
            ps.setDouble(5, d.getValor());
            ps.setInt(6, d.getIdDespesa());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM despesa WHERE id_despesa = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Despesa> buscarPorVeiculo(int idVeiculo) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE d.id_veiculo = ? ORDER BY d.data DESC";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Despesa> buscarPorMes(int mes, int ano) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE MONTH(d.data) = ? AND YEAR(d.data) = ? ORDER BY d.data";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Despesa> buscarCombustivelPorMes(int mes, int ano) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE d.id_tipo_despesa = ? AND MONTH(d.data) = ? AND YEAR(d.data) = ? ORDER BY d.data";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, TipoDespesa.ID_COMBUSTIVEL);
            ps.setInt(2, mes);
            ps.setInt(3, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Despesa> buscarIpvaPorAno(int ano) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE d.id_tipo_despesa = ? AND YEAR(d.data) = ? ORDER BY d.data";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, TipoDespesa.ID_IPVA);
            ps.setInt(2, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Despesa> buscarMultasPorVeiculoAno(int idVeiculo, int ano) throws SQLException {
        String sql = "SELECT d.*, t.descricao AS tipo_desc FROM despesa d " +
                     "JOIN tipo_despesa t ON d.id_tipo_despesa = t.id_tipo_despesa " +
                     "WHERE d.id_veiculo = ? AND d.id_tipo_despesa = ? AND YEAR(d.data) = ? ORDER BY d.data";
        List<Despesa> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            ps.setInt(2, TipoDespesa.ID_MULTA);
            ps.setInt(3, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<Object[]> buscarMediaGastoPorCategoria() throws SQLException {
        String sql = "SELECT v.categoria, AVG(d.valor) AS media_gasto " +
                     "FROM despesa d " +
                     "JOIN veiculo v ON d.id_veiculo = v.id_veiculo " +
                     "GROUP BY v.categoria " +
                     "ORDER BY media_gasto DESC";
        List<Object[]> resultado = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Object[]{
                        rs.getString("categoria"),
                        rs.getDouble("media_gasto")
                });
            }
        }
        return resultado;
    }

    public List<Object[]> buscarMediaIpvaPorAno(int ano) throws SQLException {
        String sql = "SELECT v.categoria, SUM(d.valor) AS total_ipva, COUNT(d.id_despesa) AS qtd, AVG(d.valor) AS media_ipva " +
                     "FROM despesa d " +
                     "JOIN veiculo v ON d.id_veiculo = v.id_veiculo " +
                     "WHERE d.id_tipo_despesa = ? AND YEAR(d.data) = ? " +
                     "GROUP BY v.categoria " +
                     "ORDER BY v.categoria";
        List<Object[]> resultado = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, TipoDespesa.ID_IPVA);
            ps.setInt(2, ano);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(new Object[]{
                            rs.getString("categoria"),
                            rs.getDouble("total_ipva"),
                            rs.getInt("qtd"),
                            rs.getDouble("media_ipva")
                    });
                }
            }
        }
        return resultado;
    }
    
    private Despesa mapear(ResultSet rs) throws SQLException {
        TipoDespesa tipo = new TipoDespesa(
                rs.getInt("id_tipo_despesa"),
                rs.getString("tipo_desc")
        );
        LocalDate data = rs.getDate("data").toLocalDate();

        return new Despesa(
                rs.getInt("id_despesa"),
                rs.getInt("id_veiculo"),
                tipo,
                rs.getString("descricao"),
                data,
                rs.getDouble("valor"),
                rs.getBoolean("gerada_por_abastecimento")
        );
    }
}
