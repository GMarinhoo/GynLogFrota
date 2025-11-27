package br.com.gynlog.model;

import br.com.gynlog.enums.TipoDespesaEnum;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movimentacao {
    private int idMovimentacao;
    private int idVeiculo;
    private TipoDespesaEnum tipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Movimentacao() {}

    public Movimentacao(int idMovimentacao, int idVeiculo, TipoDespesaEnum tipoDespesa, String descricao, LocalDate data, double valor) {
        this.idMovimentacao = idMovimentacao;
        this.idVeiculo = idVeiculo;
        this.tipoDespesa = tipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
    }

    // --- GETTERS (CRITICOS PARA OS RELATÓRIOS) ---
    public int getIdMovimentacao() { return idMovimentacao; }
    public void setIdMovimentacao(int idMovimentacao) { this.idMovimentacao = idMovimentacao; }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public TipoDespesaEnum getTipoDespesa() { return tipoDespesa; }
    public void setTipoDespesa(TipoDespesaEnum tipoDespesa) { this.tipoDespesa = tipoDespesa; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String toCsvLine() {
        return idMovimentacao + ";" + idVeiculo + ";" + (tipoDespesa!=null?tipoDespesa.name():"OUTROS") + ";" + descricao + ";" + (data!=null?data.format(FMT):"") + ";" + valor;
    }

    public static Movimentacao fromString(String linha) {
        String[] p = linha.split(";");
        if (p.length < 6) return null;
        return new Movimentacao(Integer.parseInt(p[0]), Integer.parseInt(p[1]), TipoDespesaEnum.valueOf(p[2]), p[3], LocalDate.parse(p[4], FMT), Double.parseDouble(p[5]));
    }
}