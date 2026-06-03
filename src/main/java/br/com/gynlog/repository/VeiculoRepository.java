package br.com.gynlog.repository;

import br.com.gynlog.enums.CategoriaVeiculo;
import br.com.gynlog.model.Veiculo;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VeiculoRepository {

    private static final String ARQUIVO = "veiculos.txt";

    public void salvar(Veiculo v) throws Exception {
        List<Veiculo> lista = buscarTodos();
        int maxId = lista.stream().mapToInt(Veiculo::getIdVeiculo).max().orElse(0);
        v.setIdVeiculo(maxId + 1);
        lista.add(v);
        gravarTodos(lista);
    }

    public List<Veiculo> buscarTodos() throws Exception {
        List<Veiculo> lista = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            String[] dados = linha.split(";");
            if (dados.length == 7) {
                lista.add(new Veiculo(
                        Integer.parseInt(dados[0]), dados[1],
                        CategoriaVeiculo.valueOf(dados[2]), dados[3], dados[4],
                        Integer.parseInt(dados[5]), Boolean.parseBoolean(dados[6])
                ));
            }
        }
        return lista;
    }

    public Veiculo buscarPorId(int id) throws Exception {
        return buscarTodos().stream().filter(v -> v.getIdVeiculo() == id).findFirst().orElse(null);
    }

    public List<Veiculo> buscarInativos() throws Exception {
        return buscarTodos().stream().filter(v -> !v.isAtivo()).collect(Collectors.toList());
    }

    public List<Veiculo> buscarPorCategoria(CategoriaVeiculo categoria) throws Exception {
        return buscarTodos().stream().filter(v -> v.getCategoria() == categoria).collect(Collectors.toList());
    }

    public void atualizar(Veiculo v) throws Exception {
        List<Veiculo> lista = buscarTodos();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdVeiculo() == v.getIdVeiculo()) {
                lista.set(i, v);
                break;
            }
        }
        gravarTodos(lista);
    }

    public void excluir(int id) throws Exception {
        List<Veiculo> lista = buscarTodos();
        lista.removeIf(v -> v.getIdVeiculo() == id);
        gravarTodos(lista);
    }

    private void gravarTodos(List<Veiculo> lista) throws Exception {
        List<String> linhas = lista.stream().map(v -> String.join(";",
                String.valueOf(v.getIdVeiculo()), v.getPlaca(), v.getCategoria().name(),
                v.getMarca(), v.getModelo(), String.valueOf(v.getAnoFabricacao()),
                String.valueOf(v.isAtivo())
        )).collect(Collectors.toList());
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}