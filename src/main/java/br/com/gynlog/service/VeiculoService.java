package br.com.gynlog.service;

import br.com.gynlog.model.Veiculo;
import br.com.gynlog.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository repo;

    public void salvar(Veiculo v) throws IOException {
        if (v.getPlaca() == null || v.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("A placa é obrigatória!");
        }
        repo.salvar(v);
    }

    public void atualizar(Veiculo v) throws IOException {
        repo.atualizar(v);
    }

    public void excluir(int id) throws IOException {
        repo.excluir(id);
    }

    public List<Veiculo> listar() throws IOException {
        return repo.buscarTodos();
    }

    public Veiculo buscarPorId(int id) throws IOException {
        return listar().stream()
                .filter(v -> v.getIdVeiculo() == id)
                .findFirst()
                .orElse(null);
    }
}