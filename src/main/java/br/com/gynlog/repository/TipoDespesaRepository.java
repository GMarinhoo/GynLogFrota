package br.com.gynlog.repository;

import br.com.gynlog.model.TipoDespesa;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TipoDespesaRepository {

    private static final String ARQUIVO = "tipos_despesa.txt";

    public List<TipoDespesa> buscarTodos() throws Exception {
        List<String> linhas = ArquivoUtil.lerLinhas(ARQUIVO);
        if (linhas.isEmpty()) {
            List<TipoDespesa> padroes = Arrays.asList(
                    new TipoDespesa(TipoDespesa.ID_COMBUSTIVEL, "Combustível"),
                    new TipoDespesa(TipoDespesa.ID_IPVA, "IPVA"),
                    new TipoDespesa(TipoDespesa.ID_MULTA, "Multa")
            );
            gravarTodos(padroes);
            return new ArrayList<>(padroes);
        }

        List<TipoDespesa> lista = new ArrayList<>();
        for (String linha : linhas) {
            String[] dados = linha.split(";");
            if (dados.length == 2) {
                lista.add(new TipoDespesa(Integer.parseInt(dados[0]), dados[1]));
            }
        }
        return lista;
    }

    public TipoDespesa buscarPorId(int id) throws Exception {
        return buscarTodos().stream().filter(t -> t.getIdTipoDespesa() == id).findFirst().orElse(null);
    }

    public void salvar(TipoDespesa t) throws Exception {
        List<TipoDespesa> lista = buscarTodos();
        int maxId = lista.stream().mapToInt(TipoDespesa::getIdTipoDespesa).max().orElse(0);
        t.setIdTipoDespesa(Math.max(maxId, 5) + 1);
        lista.add(t);
        gravarTodos(lista);
    }

    public void atualizar(TipoDespesa t) throws Exception {
        List<TipoDespesa> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdTipoDespesa() == t.getIdTipoDespesa()) {
                lista.set(i, t);
                break;
            }
        }
        gravarTodos(lista);
    }

    public void excluir(int id) throws Exception {
        List<TipoDespesa> lista = buscarTodos();
        lista.removeIf(t -> t.getIdTipoDespesa() == id);
        gravarTodos(lista);
    }

    private void gravarTodos(List<TipoDespesa> lista) throws Exception {
        List<String> linhas = lista.stream().map(t -> String.join(";",
                String.valueOf(t.getIdTipoDespesa()), t.getDescricao()
        )).collect(Collectors.toList());
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}