package br.com.gynlog.controller;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.util.GerenciadorArquivos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoController {

    private static final String ARQUIVO_MOVIMENTACOES = "movimentacoes.txt";

    public void salvar(Movimentacao mov) throws IOException {
        if (mov.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero!");
        }
        if (mov.getDescricao() == null || mov.getDescricao().isEmpty()) {
            throw new IllegalArgumentException("A descrição é obrigatória!");
        }

        if (mov.getIdMovimentacao() == 0) {
            int novoId = GerenciadorArquivos.gerarProximoId(ARQUIVO_MOVIMENTACOES);
            mov.setIdMovimentacao(novoId);
            GerenciadorArquivos.salvarLinha(ARQUIVO_MOVIMENTACOES, mov.toCsvLine());
        } else {
            editar(mov);
        }
    }

    private void editar(Movimentacao movEditada) throws IOException {
        List<Movimentacao> listaAtual = listarTodas();
        List<String> novasLinhas = new ArrayList<>();
        boolean encontrou = false;

        for (Movimentacao m : listaAtual) {
            if (m.getIdMovimentacao() == movEditada.getIdMovimentacao()) {
                novasLinhas.add(movEditada.toCsvLine());
                encontrou = true;
            } else {
                novasLinhas.add(m.toCsvLine());
            }
        }

        if (encontrou) {
            GerenciadorArquivos.reescreverArquivo(ARQUIVO_MOVIMENTACOES, novasLinhas);
        } else {
            throw new IllegalArgumentException("Movimentação não encontrada para edição (ID: " + movEditada.getIdMovimentacao() + ")");
        }
    }

    public void excluir(int idMovimentacao) throws IOException {
        List<Movimentacao> listaAtual = listarTodas();
        List<String> novasLinhas = new ArrayList<>();
        boolean encontrou = false;

        for (Movimentacao m : listaAtual) {
            if (m.getIdMovimentacao() == idMovimentacao) {
                encontrou = true;
            } else {
                novasLinhas.add(m.toCsvLine());
            }
        }

        if (encontrou) {
            GerenciadorArquivos.reescreverArquivo(ARQUIVO_MOVIMENTACOES, novasLinhas);
        } else {
            throw new IllegalArgumentException("Movimentação não encontrada para exclusão.");
        }
    }

    public List<Movimentacao> listarTodas() throws IOException {
        List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_MOVIMENTACOES);
        List<Movimentacao> lista = new ArrayList<>();

        for (String linha : linhas) {
            try {
                lista.add(Movimentacao.fromString(linha));
            } catch (Exception e) {
                System.err.println("Erro ao ler movimentação: " + e.getMessage());
            }
        }
        return lista;
    }


    public List<Movimentacao> listarPorVeiculo(int idVeiculo) throws IOException {
        List<Movimentacao> todas = listarTodas();
        List<Movimentacao> filtro = new ArrayList<>();
        for (Movimentacao m : todas) {
            if (m.getIdVeiculo() == idVeiculo) {
                filtro.add(m);
            }
        }
        return filtro;
    }

    public double calcularTotalMes(int mes, int ano) throws IOException {
        double total = 0;
        for (Movimentacao m : listarTodas()) {
            if (m.getData().getMonthValue() == mes && m.getData().getYear() == ano) {
                total += m.getValor();
            }
        }
        return total;
    }

    public double calcularTotalCombustivelMes(int mes, int ano) throws IOException {
        double total = 0;
        int idCombustivel = 1;

        for (Movimentacao m : listarTodas()) {
            boolean dataBate = m.getData().getMonthValue() == mes && m.getData().getYear() == ano;
            if (dataBate && m.getIdTipoDespesa() == idCombustivel) {
                total += m.getValor();
            }
        }
        return total;
    }

    public double calcularTotalIpvaAno(int ano) throws IOException {
        double total = 0;
        int idIpva = 3;

        for (Movimentacao m : listarTodas()) {
            if (m.getData().getYear() == ano && m.getIdTipoDespesa() == idIpva) {
                total += m.getValor();
            }
        }
        return total;
    }

    public List<Movimentacao> listarMultasVeiculoAno(int idVeiculo, int ano) throws IOException {
        List<Movimentacao> resultado = new ArrayList<>();
        int idMulta = 4;

        for (Movimentacao m : listarTodas()) {
            boolean isVeiculo = m.getIdVeiculo() == idVeiculo;
            boolean isAno = m.getData().getYear() == ano;
            boolean isMulta = m.getIdTipoDespesa() == idMulta;

            if (isVeiculo && isAno && isMulta) {
                resultado.add(m);
            }
        }
        return resultado;
    }
}