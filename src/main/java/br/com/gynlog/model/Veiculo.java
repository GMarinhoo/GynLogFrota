package br.com.gynlog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Veiculo {

    private int idVeiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private boolean ativo;

    public String toCsvLine() {
        return idVeiculo + ";" +
                placa + ";" +
                marca + ";" +
                modelo + ";" +
                anoFabricacao + ";" +
                (ativo ? "ATIVO" : "INATIVO");
    }

    public static Veiculo fromString(String linha) {
        String[] partes = linha.split(";");

        if (partes.length != 6) return null; // evita erro

        return new Veiculo(
                Integer.parseInt(partes[0]),
                partes[1],
                partes[2],
                partes[3],
                Integer.parseInt(partes[4]),
                partes[5].equalsIgnoreCase("ATIVO")
        );
    }

    @Override
    public String toString() {
        return modelo + " (" + placa + ")";
    }
}
