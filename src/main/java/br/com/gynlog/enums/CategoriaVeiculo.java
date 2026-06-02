package br.com.gynlog.enums;

public enum CategoriaVeiculo {
    CARRO("Carro"),
    MOTO("Moto"),
    CAMINHAO("Caminhão"),
    VAN("Van"),
    CAMINHONETE("Caminhonete"),
    ONIBUS("Ônibus"),
    UTILITARIO("Utilitário");

    private final String descricao;

    CategoriaVeiculo(String descricao) {
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
