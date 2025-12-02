package br.com.gynlog.model;

import br.com.gynlog.enums.TipoVeiculo;

public class Veiculo {
    private int idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private boolean ativo;
    private TipoVeiculo tipo;

    public Veiculo() {}

    public Veiculo(int idVeiculo, String placa, String marca, String modelo, int anoFabricacao, boolean ativo, TipoVeiculo tipo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.ativo = ativo;
        this.tipo = tipo;
    }

    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao) { this.anoFabricacao = anoFabricacao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public TipoVeiculo getTipo() { return tipo; }
    public void setTipo(TipoVeiculo tipo) { this.tipo = tipo; }

    public String toCsvLine() {
        String tipoStr = (tipo != null) ? tipo.name() : "CARRO";
        return idVeiculo + ";" + placa + ";" + marca + ";" + modelo + ";" + anoFabricacao + ";" + (ativo ? "ATIVO" : "INATIVO") + ";" + tipoStr;
    }

    public static Veiculo fromString(String linha) {
        String[] p = linha.split(";");
        if (p.length < 6) return null;

        TipoVeiculo tipoLido = TipoVeiculo.CARRO;

        if (p.length >= 7) {
            try {
                tipoLido = TipoVeiculo.valueOf(p[6]);
            } catch (Exception e) {
                tipoLido = TipoVeiculo.CARRO;
            }
        }

        return new Veiculo(
                Integer.parseInt(p[0]),
                p[1],
                p[2],
                p[3],
                Integer.parseInt(p[4]),
                "ATIVO".equalsIgnoreCase(p[5]),
                tipoLido
        );
    }

    @Override
    public String toString() { return modelo + " (" + placa + ")"; }
}