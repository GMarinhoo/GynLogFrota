package br.com.gynlog.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movimentacao {
    private int idMovimentacao;
    private int idVeiculo;
    private int idTipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Movimentacao(int idMovimentacao, int idVeiculo, int idTipoDespesa, String descricao, LocalDate data, double valor) {
        this.idMovimentacao = idMovimentacao;
        this.idVeiculo = idVeiculo;
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
    }

    public String toCsvLine() {
        return idMovimentacao + ";" +
                idVeiculo + ";" +
                idTipoDespesa + ";" +
                descricao + ";" +
                data.format(FORMATO_DATA) + ";" +
                valor;
    }

    public int getIdMovimentacao() { return idMovimentacao; }
    public void setIdMovimentacao(int idMovimentacao) { this.idMovimentacao = idMovimentacao; }
    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public int getIdTipoDespesa() { return idTipoDespesa; }
    public void setIdTipoDespesa(int idTipoDespesa) { this.idTipoDespesa = idTipoDespesa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public static Movimentacao fromString(String linha) {
        String[] partes = linha.split(";");
        return new Movimentacao(
                Integer.parseInt(partes[0]),
                Integer.parseInt(partes[1]),
                Integer.parseInt(partes[2]),
                partes[3],
                LocalDate.parse(partes[4], FORMATO_DATA),
                Double.parseDouble(partes[5])
        );
    }
}