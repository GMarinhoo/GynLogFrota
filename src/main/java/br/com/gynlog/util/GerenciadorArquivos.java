package br.com.gynlog.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorArquivos {

    public static void salvarLinha(String nomeArquivo, String linha) throws IOException {
        File arquivo = new File(nomeArquivo);
        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo, true))) {
            writer.write(linha);
            writer.newLine();
        }
    }

    public static List<String> lerTodasLinhas(String nomeArquivo) throws IOException {
        List<String> linhas = new ArrayList<>();
        File arquivo = new File(nomeArquivo);

        if (!arquivo.exists()) {
            arquivo.createNewFile();
            return linhas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (!linha.trim().isEmpty()) {
                    linhas.add(linha);
                }
            }
        }
        return linhas;
    }

    public static void reescreverArquivo(String nomeArquivo, List<String> linhas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo, false))) {
            for (String linha : linhas) {
                writer.write(linha);
                writer.newLine();
            }
        }
    }

    public static int gerarProximoId(String nomeArquivo) throws IOException {
        List<String> linhas = lerTodasLinhas(nomeArquivo);
        int maxId = 0;

        for (String linha : linhas) {
            try {
                String[] partes = linha.split(";");
                if (partes.length > 0) {
                    int idAtual = Integer.parseInt(partes[0]);
                    if (idAtual > maxId) {
                        maxId = idAtual;
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        return maxId + 1;
    }
}