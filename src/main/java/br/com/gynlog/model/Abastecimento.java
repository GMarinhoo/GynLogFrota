package br.com.gynlog.model;

import java.time.LocalDate;

public class Abastecimento {

    private int idAbastecimento;
    private int idVeiculo;
    private LocalDate data;
    private double odometro;
    private double qtdLitros;
    private double valorTotal;
    private transient double kmRodados;
    private transient double kmPorLitro;

    public Abastecimento() {}

    public Abastecimento(int idAbastecimento, int idVeiculo, LocalDate data,
                         double odometro, double qtdLitros, double valorTotal) {
        this.idAbastecimento = idAbastecimento;
        this.idVeiculo = idVeiculo;
        this.data = data;
        this.odometro = odometro;
        this.qtdLitros = qtdLitros;
        this.valorTotal = valorTotal;
    }

    public int getIdAbastecimento() { return idAbastecimento; }
    public void setIdAbastecimento(int idAbastecimento) { this.idAbastecimento = idAbastecimento; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getOdometro() { return odometro; }
    public void setOdometro(double odometro) { this.odometro = odometro; }

    public double getQtdLitros() { return qtdLitros; }
    public void setQtdLitros(double qtdLitros) { this.qtdLitros = qtdLitros; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public double getKmRodados() { return kmRodados; }
    public void setKmRodados(double kmRodados) { this.kmRodados = kmRodados; }

    public double getKmPorLitro() { return kmPorLitro; }
    public void setKmPorLitro(double kmPorLitro) { this.kmPorLitro = kmPorLitro; }
}
