package br.com.gynlog.service;

import br.com.gynlog.enums.TipoDespesaEnum;
import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.repository.MovimentacaoRepository;
import br.com.gynlog.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatematicaService {

    @Autowired
    private MovimentacaoRepository movRepo;
    @Autowired
    private VeiculoRepository veiculoRepo;

    // Objeto para transportar os dados de UMA matriz
    public static class DadosMatriz {
        public String titulo;
        public String[] colunas;
        public Object[][] dados;
        public String resumo;

        public DadosMatriz(String titulo, String[] colunas, Object[][] dados, String resumo) {
            this.titulo = titulo;
            this.colunas = colunas;
            this.dados = dados;
            this.resumo = resumo;
        }
    }

    // Objeto que agrupa as 3 matrizes
    public static class RelatorioMatematicoDTO {
        public DadosMatriz matrizA;
        public DadosMatriz matrizB;
        public DadosMatriz matrizC;
    }

    public RelatorioMatematicoDTO gerarRelatorioCompleto() throws IOException {
        List<Movimentacao> todasMovs = movRepo.buscarTodos();
        List<Veiculo> todosVeiculos = veiculoRepo.buscarTodos();

        // Filtra abastecimentos
        List<Movimentacao> abastecimentos = todasMovs.stream()
                .filter(m -> m.getTipoDespesa() == TipoDespesaEnum.COMBUSTIVEL)
                .collect(Collectors.toList());

        // --- DEFINIÇÃO DE EIXOS ---
        // Linhas de A e C (Veículos)
        List<Veiculo> veiculosOrdenados = todosVeiculos.stream()
                .sorted(Comparator.comparing(Veiculo::getPlaca))
                .collect(Collectors.toList());
        int m = veiculosOrdenados.size();

        // Colunas de A e Linhas de B (Meses)
        int n = 12;
        String[] nomesMeses = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        // Colunas de B e C (Marcas)
        List<String> marcas = todosVeiculos.stream()
                .map(Veiculo::getMarca)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        int p = marcas.size();

        // --- 1. MATRIZ A (Veículos x Meses) ---
        // Quantidade de abastecimentos
        double[][] A = new double[m][n];
        Object[][] dadosA = new Object[m][n + 1]; // +1 para o nome da linha
        String[] colunasA = new String[n + 1];
        colunasA[0] = "Veículo";
        for(int i=0; i<n; i++) colunasA[i+1] = nomesMeses[i];

        for (int i = 0; i < m; i++) {
            Veiculo v = veiculosOrdenados.get(i);
            dadosA[i][0] = v.getPlaca();
            for (int j = 0; j < n; j++) {
                int mes = j + 1;
                long qtd = abastecimentos.stream()
                        .filter(mov -> mov.getIdVeiculo() == v.getIdVeiculo() && mov.getData().getMonthValue() == mes)
                        .count();
                A[i][j] = qtd;
                dadosA[i][j+1] = (int) qtd; // Exibe como inteiro
            }
        }

        // --- 2. MATRIZ B (Meses x Marcas) ---
        // Custo Médio
        double[][] B = new double[n][p];
        Object[][] dadosB = new Object[n][p + 1];
        String[] colunasB = new String[p + 1];
        colunasB[0] = "Mês";
        for(int i=0; i<p; i++) colunasB[i+1] = marcas.get(i);

        for (int i = 0; i < n; i++) { // Meses
            int mes = i + 1;
            dadosB[i][0] = nomesMeses[i];

            for (int j = 0; j < p; j++) { // Marcas
                String marca = marcas.get(j);

                // Busca abastecimentos deste mês e marca
                List<Movimentacao> movsMarca = abastecimentos.stream()
                        .filter(mov -> mov.getData().getMonthValue() == mes)
                        .filter(mov -> {
                            // Busca veículo para ver a marca
                            Veiculo v = todosVeiculos.stream().filter(vec -> vec.getIdVeiculo() == mov.getIdVeiculo()).findFirst().orElse(null);
                            return v != null && v.getMarca().equalsIgnoreCase(marca);
                        })
                        .collect(Collectors.toList());

                double soma = movsMarca.stream().mapToDouble(Movimentacao::getValor).sum();
                double media = movsMarca.isEmpty() ? 0 : soma / movsMarca.size();

                B[i][j] = media;
                dadosB[i][j+1] = String.format("R$ %.2f", media);
            }
        }

        // --- 3. MATRIZ C (Veículos x Marcas) ---
        // Multiplicação A x B
        double[][] C = new double[m][p];
        Object[][] dadosC = new Object[m][p + 1];
        String[] colunasC = new String[p + 1];
        colunasC[0] = "Veículo";
        for(int i=0; i<p; i++) colunasC[i+1] = marcas.get(i);

        double totalGeral = 0;

        for (int i = 0; i < m; i++) { // Linha de A
            dadosC[i][0] = veiculosOrdenados.get(i).getPlaca();
            for (int j = 0; j < p; j++) { // Coluna de B
                for (int k = 0; k < n; k++) { // Somatório
                    C[i][j] += A[i][k] * B[k][j];
                }
                totalGeral += C[i][j];
                dadosC[i][j+1] = String.format("R$ %.2f", C[i][j]);
            }
        }

        // Monta o retorno
        RelatorioMatematicoDTO dto = new RelatorioMatematicoDTO();
        dto.matrizA = new DadosMatriz("Matriz A: Qtd Abastecimentos (Veículo x Mês)", colunasA, dadosA, null);
        dto.matrizB = new DadosMatriz("Matriz B: Custo Médio (Mês x Marca)", colunasB, dadosB, null);
        dto.matrizC = new DadosMatriz("Matriz C: Gasto Estimado (Veículo x Marca)", colunasC, dadosC, String.format("TOTAL GERAL (Soma C): R$ %.2f", totalGeral));

        return dto;
    }
}