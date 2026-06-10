package br.com.gynlog.repository;

import br.com.gynlog.model.Despesa;
import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.model.Veiculo;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DespesaRepository {

    private static final String ARQUIVO = "despesas.txt";

    public void salvar(Despesa d) throws Exception {
        List<Despesa> lista = buscarTodasIncluindoDeletadas();
        int maxId = lista.stream().mapToInt(Despesa::getIdDespesa).max().orElse(0);
        d.setIdDespesa(maxId + 1);
        d.setDeletado(false);
        lista.add(d);
        gravarTodos(lista);
    }

    public List<Despesa> buscarTodos() throws Exception {
        return buscarTodasIncluindoDeletadas().stream()
                .filter(d -> !d.isDeletado())
                .sorted((d1, d2) -> d2.getData().compareTo(d1.getData()))
                .collect(Collectors.toList());
    }

    private List<Despesa> buscarTodasIncluindoDeletadas() throws Exception {
        List<Despesa> lista = new ArrayList<>();
        TipoDespesaRepository tipoRepo = new TipoDespesaRepository();
        List<TipoDespesa> tipos = tipoRepo.buscarTodos();

        for (String linha : ArquivoUtil.lerLinhas(ARQUIVO)) {
            String[] d = linha.split(";");
            if (d.length >= 7) {
                int idTipo = Integer.parseInt(d[2]);
                TipoDespesa tipo = tipos.stream().filter(t -> t.getIdTipoDespesa() == idTipo).findFirst()
                        .orElse(new TipoDespesa(idTipo, "Desconhecido"));
                Despesa despesa = new Despesa(
                        Integer.parseInt(d[0]), Integer.parseInt(d[1]), tipo,
                        d[3], LocalDate.parse(d[4]), Double.parseDouble(d[5]),
                        Boolean.parseBoolean(d[6])
                );
                despesa.setDeletado(d.length >= 8 && Boolean.parseBoolean(d[7]));
                lista.add(despesa);
            }
        }
        return lista;
    }

    public Despesa buscarPorId(int id) throws Exception {
        return buscarTodos().stream().filter(d -> d.getIdDespesa() == id).findFirst().orElse(null);
    }

    public void atualizar(Despesa d) throws Exception {
        List<Despesa> lista = buscarTodasIncluindoDeletadas();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getIdDespesa() == d.getIdDespesa()) {
                lista.set(i, d);
                break;
            }
        }
        gravarTodos(lista);
    }

    /**
     * Soft delete — marca o registro como deletado no TXT, nunca remove a linha.
     */
    public void excluir(int id) throws Exception {
        List<Despesa> lista = buscarTodasIncluindoDeletadas();
        for (Despesa d : lista) {
            if (d.getIdDespesa() == id) {
                d.setDeletado(true);
                break;
            }
        }
        gravarTodos(lista);
    }

    public List<Despesa> buscarPorVeiculo(int idVeiculo) throws Exception {
        return buscarTodos().stream().filter(d -> d.getIdVeiculo() == idVeiculo).collect(Collectors.toList());
    }

    /**
     * Busca a despesa gerada automaticamente por um abastecimento específico.
     * Critério: mesmo veículo + mesmo valor + mesma data + geradaPorAbastecimento = true.
     * Usado pelo AbastecimentoService para propagar o soft delete ao excluir um abastecimento.
     */
    public Optional<Despesa> buscarDespesaDoAbastecimento(int idVeiculo, LocalDate data, double valor) throws Exception {
        return buscarTodasIncluindoDeletadas().stream()
                .filter(d -> d.getIdVeiculo() == idVeiculo
                        && d.isGeradaPorAbastecimento()
                        && !d.isDeletado()
                        && d.getData().equals(data)
                        && Math.abs(d.getValor() - valor) < 0.01)
                .findFirst();
    }

    public List<Despesa> buscarPorMes(int mes, int ano) throws Exception {
        return buscarTodos().stream()
                .filter(d -> d.getData().getMonthValue() == mes && d.getData().getYear() == ano)
                .sorted(Comparator.comparing(Despesa::getData))
                .collect(Collectors.toList());
    }

    public List<Despesa> buscarCombustivelPorMes(int mes, int ano) throws Exception {
        return buscarTodos().stream()
                .filter(d -> d.getTipoDespesa().getIdTipoDespesa() == TipoDespesa.ID_COMBUSTIVEL)
                .filter(d -> d.getData().getMonthValue() == mes && d.getData().getYear() == ano)
                .sorted(Comparator.comparing(Despesa::getData))
                .collect(Collectors.toList());
    }

    public List<Despesa> buscarIpvaPorAno(int ano) throws Exception {
        return buscarTodos().stream()
                .filter(d -> d.getTipoDespesa().getIdTipoDespesa() == TipoDespesa.ID_IPVA && d.getData().getYear() == ano)
                .sorted(Comparator.comparing(Despesa::getData))
                .collect(Collectors.toList());
    }

    public List<Despesa> buscarMultasPorVeiculoAno(int idVeiculo, int ano) throws Exception {
        return buscarTodos().stream()
                .filter(d -> d.getIdVeiculo() == idVeiculo
                        && d.getTipoDespesa().getIdTipoDespesa() == TipoDespesa.ID_MULTA
                        && d.getData().getYear() == ano)
                .sorted(Comparator.comparing(Despesa::getData))
                .collect(Collectors.toList());
    }

    public List<Object[]> buscarMediaGastoPorCategoria() throws Exception {
        VeiculoRepository vRepo = new VeiculoRepository();
        List<Veiculo> veiculos = vRepo.buscarTodos();
        Map<String, List<Despesa>> agrupado = new HashMap<>();

        for (Despesa d : buscarTodos()) {
            Veiculo v = veiculos.stream().filter(ve -> ve.getIdVeiculo() == d.getIdVeiculo()).findFirst().orElse(null);
            if (v != null) {
                agrupado.computeIfAbsent(v.getCategoria().name(), k -> new ArrayList<>()).add(d);
            }
        }

        List<Object[]> res = new ArrayList<>();
        for (Map.Entry<String, List<Despesa>> entry : agrupado.entrySet()) {
            double media = entry.getValue().stream().mapToDouble(Despesa::getValor).average().orElse(0.0);
            res.add(new Object[]{entry.getKey(), media});
        }
        res.sort((a, b) -> Double.compare((Double) b[1], (Double) a[1]));
        return res;
    }

    public List<Object[]> buscarMediaIpvaPorAno(int ano) throws Exception {
        VeiculoRepository vRepo = new VeiculoRepository();
        List<Veiculo> veiculos = vRepo.buscarTodos();
        List<Despesa> ipvas = buscarIpvaPorAno(ano);
        Map<String, List<Despesa>> agrupado = new HashMap<>();

        for (Despesa d : ipvas) {
            Veiculo v = veiculos.stream().filter(ve -> ve.getIdVeiculo() == d.getIdVeiculo()).findFirst().orElse(null);
            if (v != null) {
                agrupado.computeIfAbsent(v.getCategoria().name(), k -> new ArrayList<>()).add(d);
            }
        }

        List<Object[]> res = new ArrayList<>();
        for (Map.Entry<String, List<Despesa>> entry : agrupado.entrySet()) {
            double total = entry.getValue().stream().mapToDouble(Despesa::getValor).sum();
            int qtd = entry.getValue().size();
            res.add(new Object[]{entry.getKey(), total, qtd, total / qtd});
        }
        return res;
    }

    private void gravarTodos(List<Despesa> lista) throws Exception {
        List<String> linhas = lista.stream().map(d -> String.join(";",
                String.valueOf(d.getIdDespesa()), String.valueOf(d.getIdVeiculo()),
                String.valueOf(d.getTipoDespesa().getIdTipoDespesa()), d.getDescricao(),
                d.getData().toString(), String.valueOf(d.getValor()),
                String.valueOf(d.isGeradaPorAbastecimento()), String.valueOf(d.isDeletado())
        )).collect(Collectors.toList());
        ArquivoUtil.escreverLinhas(ARQUIVO, linhas);
    }
}