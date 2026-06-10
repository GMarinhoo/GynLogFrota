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
        List<Veiculo> lista = buscarTodosIncluindoDeletados();
        int maxId = lista.stream().mapToInt(Veiculo::getIdVeiculo).max().orElse(0);
        v.setIdVeiculo(maxId + 1);
        v.setDeletado(false);
        lista.add(v);
        gravarTodos(lista);
    }

    public List<Veiculo> buscarTodos() throws Exception {
        return buscarTodosIncluindoDeletados().stream()
                .filter(v -> !v.isDeletado())
                .collect(Collectors.toList());
    }

    private List<Veiculo> buscarTodosIncluindoDeletados() throws Exception {
        List<Veiculo> lista = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            String[] d = linha.split(";");
            if (d.length >= 7) {
                Veiculo v = new Veiculo(
                        Integer.parseInt(d[0]), d[1],
                        CategoriaVeiculo.valueOf(d[2]), d[3], d[4],
                        Integer.parseInt(d[5]), Boolean.parseBoolean(d[6])
                );
                v.setDeletado(d.length >= 8 && Boolean.parseBoolean(d[7]));
                lista.add(v);
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
        List<Veiculo> lista = buscarTodosIncluindoDeletados();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdVeiculo() == v.getIdVeiculo()) {
                lista.set(i, v);
                break;
            }
        }
        gravarTodos(lista);
    }

    public void excluir(int id) throws Exception {
        List<Veiculo> lista = buscarTodosIncluindoDeletados();
        for (Veiculo v : lista) {
            if (v.getIdVeiculo() == id) {
                v.setDeletado(true);
                break;
            }
        }
        gravarTodos(lista);
    }

    private void gravarTodos(List<Veiculo> lista) throws Exception {
        List<String> linhas = lista.stream().map(v -> String.join(";",
                String.valueOf(v.getIdVeiculo()), v.getPlaca(), v.getCategoria().name(),
                v.getMarca(), v.getModelo(), String.valueOf(v.getAnoFabricacao()),
                String.valueOf(v.isAtivo()), String.valueOf(v.isDeletado())
        )).collect(Collectors.toList());
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}