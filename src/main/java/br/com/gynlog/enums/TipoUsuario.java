package br.com.gynlog.enums;

public enum TipoUsuario {
    GERENTE("Gerente"),
    FUNCIONARIO("Funcionário");

    private String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}