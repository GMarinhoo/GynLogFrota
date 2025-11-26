package br.com.gynlog.service;

import br.com.gynlog.dao.VeiculoDao;
import br.com.gynlog.model.Veiculo;

import java.io.IOException;
import java.util.List;

public class VeiculoService {

    private final VeiculoDao dao = new VeiculoDao();

    // Salvar com validação
    public void salvar(Veiculo v) throws IOException {
        validarVeiculo(v);
        dao.salvar(v);
    }

    // Validações
    private void validarVeiculo(Veiculo v) throws IOException {

        if (v.getPlaca() == null || v.getPlaca().isBlank())
            throw new IllegalArgumentException("Placa inválida.");

        int anoAtual = java.time.Year.now().getValue();
        if (v.getAnoFabricacao() < 1950 || v.getAnoFabricacao() > anoAtual)
            throw new IllegalArgumentException("Ano de fabricação inválido.");

        if (placaExiste(v.getPlaca()))
            throw new IllegalArgumentException("Placa já cadastrada.");
    }

    public boolean placaExiste(String placa) throws IOException {
        return dao.buscarTodos()
                .stream()
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa));
    }

    public List<Veiculo> listar() throws IOException {
        return dao.buscarTodos();
    }

    public void atualizar(Veiculo v) throws IOException {
        validarVeiculo(v);
        dao.atualizar(v);
    }

    public void excluir(int id) throws IOException {
        dao.excluir(id);
    }
}
