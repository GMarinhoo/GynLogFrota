package br.com.gynlog.service;

import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.repository.TipoDespesaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class TipoDespesaService {

    private static final int ID_MAXIMO_PROTEGIDO = 5;

    @Autowired
    private TipoDespesaRepository repo;

    public List<TipoDespesa> listar() throws Exception {
        return repo.buscarTodos();
    }

    public TipoDespesa buscarPorId(int id) throws Exception {
        return repo.buscarPorId(id);
    }

    public void salvar(TipoDespesa t) throws Exception {
        validar(t);
        repo.salvar(t);
    }

    public void atualizar(TipoDespesa t) throws Exception {
        validar(t);
        repo.atualizar(t);
    }

    public void excluir(int id) throws Exception {
        if (id <= ID_MAXIMO_PROTEGIDO)
            throw new IllegalArgumentException("Os tipos padrão do sistema não podem ser excluídos.");
        repo.excluir(id);
    }

    private void validar(TipoDespesa t) {
        if (t.getDescricao() == null || t.getDescricao().trim().isEmpty())
            throw new IllegalArgumentException("A descrição do tipo de despesa é obrigatória.");
        if (t.getDescricao().trim().length() > 50)
            throw new IllegalArgumentException("A descrição deve ter no máximo 50 caracteres.");
    }
}
