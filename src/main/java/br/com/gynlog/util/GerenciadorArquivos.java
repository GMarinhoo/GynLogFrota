package br.com.gynlog.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivos {

    public static void salvarLinha(String nomeArquivo, String linha) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, true))) {
            writer.write(linha);
            writer.newLine();
        }
    }

    public static List<String> lerTodasLinhas(String nomeArquivo) throws IOException {
        List<String> linhas = new ArrayList<>();
        File arquivo = new File(nomeArquivo);

        if (!arquivo.exists()) {
            return linhas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }
        }
        return linhas;
    }
}