package br.com.gynlog.enums;

public enum TipoVeiculo {
    CARRO("Carro"),
    MOTO("Moto"),
    CAMINHAO("Caminhão"),
    VAN("Van"),
    CAMINHONETE("Caminhonete");

    private final String descricao;

    TipoVeiculo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}