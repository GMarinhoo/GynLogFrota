package br.com.gynlog.model.enums;

public class TipoDespesa {
    private int idTipoDespesa;
    private String descricao;

    public TipoDespesa(int idTipoDespesa, String descricao) {
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
    }

    public String toCsvLine() {
        return idTipoDespesa + ";" + descricao;
    }

    public int getIdTipoDespesa() { return idTipoDespesa; }
    public void setIdTipoDespesa(int idTipoDespesa) { this.idTipoDespesa = idTipoDespesa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public static TipoDespesa fromString(String linha) {
        String[] partes = linha.split(";");
        return new TipoDespesa(
                Integer.parseInt(partes[0]),
                partes[1]
        );
    }

    @Override
    public String toString() {
        return descricao;
    }
}