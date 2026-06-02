package br.com.gynlog.repository;

import br.com.gynlog.model.Abastecimento;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AbastecimentoRepository {

    public void salvar(Abastecimento a) throws SQLException {
        String sql = "INSERT INTO abastecimento (id_veiculo, data, odometro, qtd_litros, valor_total) VALUES (?,?,?,?,?)";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdVeiculo());
            ps.setDate(2, Date.valueOf(a.getData()));
            ps.setDouble(3, a.getOdometro());
            ps.setDouble(4, a.getQtdLitros());
            ps.setDouble(5, a.getValorTotal());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdAbastecimento(rs.getInt(1));
            }
        }
    }

    public List<Abastecimento> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM abastecimento ORDER BY data DESC";
        List<Abastecimento> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Abastecimento buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM abastecimento WHERE id_abastecimento = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Abastecimento> buscarPorVeiculo(int idVeiculo) throws SQLException {
        String sql = "SELECT * FROM abastecimento WHERE id_veiculo = ? ORDER BY data ASC, odometro ASC";
        List<Abastecimento> lista = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Abastecimento buscarAnterior(int idVeiculo, double odometroAtual) throws SQLException {
        String sql = "SELECT * FROM abastecimento " +
                     "WHERE id_veiculo = ? AND odometro < ? " +
                     "ORDER BY odometro DESC LIMIT 1";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idVeiculo);
            ps.setDouble(2, odometroAtual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Object[]> buscarConsumoMedioPorVeiculo() throws SQLException {
        String sql = "SELECT " +
                     "  a.id_veiculo, " +
                     "  v.modelo, " +
                     "  v.placa, " +
                     "  AVG((a.odometro - LAG(a.odometro) OVER (PARTITION BY a.id_veiculo ORDER BY a.odometro)) / a.qtd_litros) AS media_km_litro, " +
                     "  MAX(a.odometro) - MIN(a.odometro) AS total_km, " +
                     "  SUM(a.qtd_litros) AS total_litros " +
                     "FROM abastecimento a " +
                     "JOIN veiculo v ON a.id_veiculo = v.id_veiculo " +
                     "GROUP BY a.id_veiculo, v.modelo, v.placa " +
                     "HAVING COUNT(a.id_abastecimento) > 1 " +
                     "ORDER BY media_km_litro DESC";

        List<Object[]> resultado = new ArrayList<>();
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(new Object[]{
                        rs.getInt("id_veiculo"),
                        rs.getString("modelo"),
                        rs.getString("placa"),
                        rs.getDouble("media_km_litro"),
                        rs.getDouble("total_km"),
                        rs.getDouble("total_litros")
                });
            }
        }
        return resultado;
    }

    public void atualizar(Abastecimento a) throws SQLException {
        String sql = "UPDATE abastecimento SET id_veiculo=?, data=?, odometro=?, qtd_litros=?, valor_total=? WHERE id_abastecimento=?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getIdVeiculo());
            ps.setDate(2, Date.valueOf(a.getData()));
            ps.setDouble(3, a.getOdometro());
            ps.setDouble(4, a.getQtdLitros());
            ps.setDouble(5, a.getValorTotal());
            ps.setInt(6, a.getIdAbastecimento());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM abastecimento WHERE id_abastecimento = ?";
        try (Connection con = ConexaoBanco.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Abastecimento mapear(ResultSet rs) throws SQLException {
        LocalDate data = rs.getDate("data").toLocalDate();
        return new Abastecimento(
                rs.getInt("id_abastecimento"),
                rs.getInt("id_veiculo"),
                data,
                rs.getDouble("odometro"),
                rs.getDouble("qtd_litros"),
                rs.getDouble("valor_total")
        );
    }
}
