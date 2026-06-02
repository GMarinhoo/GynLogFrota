package br.com.gynlog.model;

import java.time.LocalDate;

public class Despesa {

    private int idDespesa;
    private int idVeiculo;
    private TipoDespesa tipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;
    private boolean geradaPorAbastecimento;

    public Despesa() {}

    public Despesa(int idDespesa, int idVeiculo, TipoDespesa tipoDespesa,
                   String descricao, LocalDate data, double valor, boolean geradaPorAbastecimento) {
        this.idDespesa = idDespesa;
        this.idVeiculo = idVeiculo;
        this.tipoDespesa = tipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.geradaPorAbastecimento = geradaPorAbastecimento;
    }

    public int getIdDespesa() { return idDespesa; }
    public void setIdDespesa(int idDespesa) { this.idDespesa = idDespesa; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public TipoDespesa getTipoDespesa() { return tipoDespesa; }
    public void setTipoDespesa(TipoDespesa tipoDespesa) { this.tipoDespesa = tipoDespesa; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public boolean isGeradaPorAbastecimento() { return geradaPorAbastecimento; }
    public void setGeradaPorAbastecimento(boolean geradaPorAbastecimento) {
        this.geradaPorAbastecimento = geradaPorAbastecimento;
    }
}
