package br.com.gynlog.model;

public class TipoDespesa {

    public static final int ID_COMBUSTIVEL = 1;
    public static final int ID_IPVA        = 3;
    public static final int ID_MULTA       = 4;

    private int idTipoDespesa;
    private String descricao;

    public TipoDespesa() {}

    public TipoDespesa(int idTipoDespesa, String descricao) {
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
    }

    public int getIdTipoDespesa() { return idTipoDespesa; }
    public void setIdTipoDespesa(int idTipoDespesa) { this.idTipoDespesa = idTipoDespesa; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return descricao;
    }
}
