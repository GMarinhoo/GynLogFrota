package br.com.gynlog.repository;

import br.com.gynlog.model.Abastecimento;
import br.com.gynlog.model.Veiculo;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class AbastecimentoRepository {

    private static final String ARQUIVO = "abastecimentos.txt";

    public void salvar(Abastecimento a) throws Exception {
        List<Abastecimento> lista = buscarTodosIncluindoDeletados();
        int maxId = lista.stream().mapToInt(Abastecimento::getIdAbastecimento).max().orElse(0);
        a.setIdAbastecimento(maxId + 1);
        a.setDeletado(false);
        lista.add(a);
        gravarTodos(lista);
    }

    /**
     * Retorna apenas abastecimentos não deletados — usado pela interface.
     */
    public List<Abastecimento> buscarTodos() throws Exception {
        return buscarTodosIncluindoDeletados().stream()
                .filter(a -> !a.isDeletado())
                .sorted((a1, a2) -> a2.getData().compareTo(a1.getData()))
                .collect(Collectors.toList());
    }

    private List<Abastecimento> buscarTodosIncluindoDeletados() throws Exception {
        List<Abastecimento> lista = new ArrayList<>();
        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            String[] d = linha.split(";");
            if (d.length >= 6) {
                Abastecimento a = new Abastecimento(
                        Integer.parseInt(d[0]), Integer.parseInt(d[1]),
                        LocalDate.parse(d[2]), Double.parseDouble(d[3]),
                        Double.parseDouble(d[4]), Double.parseDouble(d[5])
                );
                a.setDeletado(d.length >= 7 && Boolean.parseBoolean(d[6]));
                lista.add(a);
            }
        }
        return lista;
    }

    public Abastecimento buscarPorId(int id) throws Exception {
        return buscarTodos().stream().filter(a -> a.getIdAbastecimento() == id).findFirst().orElse(null);
    }

    public List<Abastecimento> buscarPorVeiculo(int idVeiculo) throws Exception {
        return buscarTodos().stream()
                .filter(a -> a.getIdVeiculo() == idVeiculo)
                .sorted(Comparator.comparing(Abastecimento::getData).thenComparing(Abastecimento::getOdometro))
                .collect(Collectors.toList());
    }

    public Abastecimento buscarAnterior(int idVeiculo, double odometroAtual) throws Exception {
        return buscarTodos().stream()
                .filter(a -> a.getIdVeiculo() == idVeiculo && a.getOdometro() < odometroAtual)
                .max(Comparator.comparing(Abastecimento::getOdometro))
                .orElse(null);
    }

    public List<Object[]> buscarConsumoMedioPorVeiculo() throws Exception {
        VeiculoRepository vRepo = new VeiculoRepository();
        List<Veiculo> veiculos = vRepo.buscarTodos();
        Map<Integer, List<Abastecimento>> agrupado = buscarTodos().stream()
                .collect(Collectors.groupingBy(Abastecimento::getIdVeiculo));

        List<Object[]> resultado = new ArrayList<>();
        for (Map.Entry<Integer, List<Abastecimento>> entry : agrupado.entrySet()) {
            List<Abastecimento> lista = entry.getValue();
            if (lista.size() <= 1) continue;
            lista.sort(Comparator.comparing(Abastecimento::getOdometro));

            double totalKm = lista.get(lista.size() - 1).getOdometro() - lista.get(0).getOdometro();
            double totalLitros = lista.subList(1, lista.size())
                    .stream().mapToDouble(Abastecimento::getQtdLitros).sum();
            double media = totalLitros > 0 ? totalKm / totalLitros : 0;

            Veiculo v = veiculos.stream().filter(ve -> ve.getIdVeiculo() == entry.getKey()).findFirst().orElse(null);
            if (v != null) {
                resultado.add(new Object[]{v.getIdVeiculo(), v.getModelo(), v.getPlaca(), media, totalKm, totalLitros});
            }
        }
        resultado.sort((a, b) -> Double.compare((Double) b[3], (Double) a[3]));
        return resultado;
    }

    public void atualizar(Abastecimento a) throws Exception {
        List<Abastecimento> lista = buscarTodosIncluindoDeletados();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdAbastecimento() == a.getIdAbastecimento()) {
                lista.set(i, a);
                break;
            }
        }
        gravarTodos(lista);
    }

    public void excluir(int id) throws Exception {
        List<Abastecimento> lista = buscarTodosIncluindoDeletados();
        for (Abastecimento a : lista) {
            if (a.getIdAbastecimento() == id) {
                a.setDeletado(true);
                break;
            }
        }
        gravarTodos(lista);
    }

    private void gravarTodos(List<Abastecimento> lista) throws Exception {
        List<String> linhas = lista.stream().map(a -> String.join(";",
                String.valueOf(a.getIdAbastecimento()), String.valueOf(a.getIdVeiculo()),
                a.getData().toString(), String.valueOf(a.getOdometro()),
                String.valueOf(a.getQtdLitros()), String.valueOf(a.getValorTotal()),
                String.valueOf(a.isDeletado())
        )).collect(Collectors.toList());
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}