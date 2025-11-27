package br.com.gynlog.service;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.repository.MovimentacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class MovimentacaoService {

    @Autowired
    private MovimentacaoRepository repo;

    public void salvar(Movimentacao m) throws IOException {
        validar(m);
        repo.salvar(m);
    }

    public void atualizar(Movimentacao m) throws IOException {
        validar(m);
        repo.atualizar(m);
    }

    public void excluir(int id) throws IOException {
        repo.excluir(id);
    }

    // --- ESSE ERA O MÉTODO QUE FALTAVA E CAUSOU OS ERROS ---
    public List<Movimentacao> listar() throws IOException {
        return repo.buscarTodos();
    }
    // -------------------------------------------------------

    private void validar(Movimentacao m) {
        if (m.getValor() <= 0) throw new IllegalArgumentException("Valor inválido");
        if (m.getDescricao() == null || m.getDescricao().trim().isEmpty()) throw new IllegalArgumentException("Descrição obrigatória");
    }
}