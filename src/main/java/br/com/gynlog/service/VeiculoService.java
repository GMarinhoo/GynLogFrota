package br.com.gynlog.service;

import br.com.gynlog.enums.CategoriaVeiculo;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repo;

    public void salvar(Veiculo v) throws SQLException {
        validar(v);
        repo.salvar(v);
    }

    public void atualizar(Veiculo v) throws SQLException {
        validar(v);
        repo.atualizar(v);
    }

    public void excluir(int id) throws SQLException {
        repo.excluir(id);
    }

    public List<Veiculo> listar() throws SQLException {
        return repo.buscarTodos();
    }

    public Veiculo buscarPorId(int id) throws SQLException {
        return repo.buscarPorId(id);
    }

    public List<Veiculo> listarInativos() throws SQLException {
        return repo.buscarInativos();
    }

    public List<Veiculo> listarPorCategoria(CategoriaVeiculo categoria) throws SQLException {
        return repo.buscarPorCategoria(categoria);
    }
    
    private void validar(Veiculo v) {
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty())
            throw new IllegalArgumentException("A placa é obrigatória.");

        if (v.getPlaca().trim().length() > 8)
            throw new IllegalArgumentException("A placa deve ter no máximo 8 caracteres.");

        if (v.getMarca() == null || v.getMarca().trim().isEmpty())
            throw new IllegalArgumentException("A marca é obrigatória.");

        if (v.getModelo() == null || v.getModelo().trim().isEmpty())
            throw new IllegalArgumentException("O modelo é obrigatório.");

        if (v.getCategoria() == null)
            throw new IllegalArgumentException("A categoria é obrigatória.");

        int anoAtual = Year.now().getValue();
        if (v.getAnoFabricacao() < 1950 || v.getAnoFabricacao() > anoAtual + 1)
            throw new IllegalArgumentException("Ano de fabricação inválido (1950 a " + (anoAtual + 1) + ").");
    }
}
