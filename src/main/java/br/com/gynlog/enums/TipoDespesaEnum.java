package br.com.gynlog.enums;

public enum TipoDespesaEnum {

    COMBUSTIVEL("Combustível"),
    MANUTENCAO("Manutenção"),
    IPVA("IPVA"),
    MULTA("Multa"),
    OUTROS("Outros");

    private String descricao;

    TipoDespesaEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}