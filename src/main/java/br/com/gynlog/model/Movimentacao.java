package br.com.gynlog.model;

import br.com.gynlog.enums.TipoDespesaEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimentacao {

    private int idMovimentacao;
    private int idVeiculo;
    private TipoDespesaEnum tipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String toCsvLine() {
        return idMovimentacao + ";" +
                idVeiculo + ";" +
                tipoDespesa.name() + ";" +  // <-- salva o ENUM como texto
                descricao + ";" +
                data.format(FORMATO_DATA) + ";" +
                valor;
    }

    public static Movimentacao fromString(String linha) {
        String[] partes = linha.split(";");

        return new Movimentacao(
                Integer.parseInt(partes[0]),
                Integer.parseInt(partes[1]),
                TipoDespesaEnum.valueOf(partes[2]),  // <-- reconstrói o ENUM
                partes[3],
                LocalDate.parse(partes[4], FORMATO_DATA),
                Double.parseDouble(partes[5])
        );
    }
}
