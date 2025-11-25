package br.com.gynlog.application;

import br.com.gynlog.model.entity.Veiculo;
import br.com.gynlog.util.GerenciadorArquivos;
import java.io.IOException;

public class MainTeste {
    public static void main(String[] args) {
        System.out.println("--- INICIANDO TESTE ---");

        Veiculo v1 = new Veiculo(1, "ABC-1234", "Fiat", "Uno", 2010, true);
        System.out.println("Veículo criado: " + v1.toString());

        try {
            System.out.println("Tentando salvar...");
            GerenciadorArquivos.salvarLinha("veiculos.txt", v1.toCsvLine());
            System.out.println("SUCESSO: Veículo salvo no arquivo!");

            System.out.println("\n--- LENDO ARQUIVO ---");
            for (String linha : GerenciadorArquivos.lerTodasLinhas("veiculos.txt")) {
                System.out.println("Linha lida do arquivo: " + linha);
            }

        } catch (IOException e) {
            System.err.println("ERRO CRÍTICO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}