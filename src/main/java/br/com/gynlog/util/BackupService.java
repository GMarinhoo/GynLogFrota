package br.com.gynlog.util;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupService {

    private static final String[] ARQUIVOS_PARA_BACKUP = {
            "veiculos.txt",
            "movimentacoes.txt",
            "tipos_despesa.txt"
    };

    public static void realizarBackup() {
        try {
            Path pastaBackup = Paths.get("backup");
            if (!Files.exists(pastaBackup)) {
                Files.createDirectory(pastaBackup);
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));

            for (String nomeArquivo : ARQUIVOS_PARA_BACKUP) {
                Path origem = Paths.get(nomeArquivo);
                if (Files.exists(origem)) {
                    Path destino = pastaBackup.resolve("backup_" + timestamp + "_" + nomeArquivo);
                    Files.copy(origem, destino, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("[BACKUP] Arquivo copiado: " + destino);
                }
            }
        } catch (IOException e) {
            System.err.println("[BACKUP] Falha ao realizar backup: " + e.getMessage());
        }
    }
}