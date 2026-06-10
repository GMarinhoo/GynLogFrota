package br.com.gynlog.model;

import br.com.gynlog.enums.CategoriaVeiculo;

public class Veiculo {

    private int idVeiculo;
    private String placa;
    private CategoriaVeiculo categoria;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private boolean ativo;
    private boolean deletado; // soft delete — linha permanece no TXT, só é ocultada na interface

    public Veiculo() {}

    public Veiculo(int idVeiculo, String placa, CategoriaVeiculo categoria,
                   String marca, String modelo, int anoFabricacao, boolean ativo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.categoria = categoria;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.ativo = ativo;
        this.deletado = false;
    }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public CategoriaVeiculo getCategoria() { return categoria; }
    public void setCategoria(CategoriaVeiculo categoria) { this.categoria = categoria; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao) { this.anoFabricacao = anoFabricacao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isDeletado() { return deletado; }
    public void setDeletado(boolean deletado) { this.deletado = deletado; }

    @Override
    public String toString() {
        return modelo + " - " + placa;
    }
}