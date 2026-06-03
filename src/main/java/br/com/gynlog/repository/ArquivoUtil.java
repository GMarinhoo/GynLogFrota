package br.com.gynlog.repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ArquivoUtil {
    private static final String DIRETORIO = "dados/";

    static {
        new File(DIRETORIO).mkdirs();
    }

    public static List<String> lerLinhas(String nomeArquivo) throws IOException {
        File arquivo = new File(DIRETORIO + nomeArquivo);
        if (!arquivo.exists()) {
            arquivo.createNewFile();
            return new ArrayList<>();
        }
        return Files.readAllLines(Paths.get(arquivo.toURI()));
    }

    public static void escreverLinhas(String nomeArquivo, List<String> linhas) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DIRETORIO + nomeArquivo))) {
            for (String linha : linhas) {
                bw.write(linha);
                bw.newLine();
            }
        }
    }
}