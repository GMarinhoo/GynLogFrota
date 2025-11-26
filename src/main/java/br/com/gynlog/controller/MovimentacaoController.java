package br.com.gynlog.controller;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.util.GerenciadorArquivos;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoController {

    private static final String ARQUIVO_MOVIMENTACOES = "movimentacoes.txt";

    public void salvar(Movimentacao mov) throws IOException {
        if (mov.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero!");
        }
        GerenciadorArquivos.salvarLinha(ARQUIVO_MOVIMENTACOES, mov.toCsvLine());
    }

    public List<Movimentacao> listarTodas() throws IOException {
        List<String> linhas = GerenciadorArquivos.lerTodasLinhas(ARQUIVO_MOVIMENTACOES);
        List<Movimentacao> lista = new ArrayList<>();
        for (String linha : linhas) {
            try {
                lista.add(Movimentacao.fromString(linha));
            } catch (Exception e) {
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
        int idCombustivel = 1; // ID padrão que definimos

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