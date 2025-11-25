package br.com.gynlog.model;

public class Veiculo {
    private int idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private boolean ativo;

    public Veiculo(int idVeiculo, String placa, String marca, String modelo, int anoFabricacao, boolean ativo) {
        this.idVeiculo = idVeiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.ativo = ativo;
    }

    public String toCsvLine() {
        String status = ativo ? "ATIVO" : "INATIVO";
        return idVeiculo + ";" + placa + ";" + marca + ";" + modelo + ";" + anoFabricacao + ";" + status;
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

    public static Veiculo fromString(String linha) {
        String[] partes = linha.split(";");
        // O split quebra o texto: "1;ABC;Fiat..." vira ["1", "ABC", "Fiat"...]

        return new Veiculo(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                Integer.parseInt(partes[4]),
                partes[5].equals("ATIVO")
        );
    }

    @Override
    public String toString() {
        return modelo + " (" + placa + ")";
    }
}