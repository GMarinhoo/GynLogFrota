package br.com.gynlog.enums;

public enum TipoUsuario {
    GERENTE("Gerente"),
    FUNCIONARIO("Funcionário");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao; // Retorna o texto amigável para o ComboBox
    }
}