package br.com.gynlog.app;

import br.com.gynlog.util.BackupService;

import javax.swing.*;

public class GynLogApp {

    public static void main(String[] args) {
        System.out.println("Iniciando sistema GynLog...");
        BackupService.realizarBackup();

        TipoDespesaController tipoController = new TipoDespesaController();
        tipoController.inicializarTiposPadrao();

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao definir tema visual: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame("GynLog - Sistema de Frota");
            janela.setSize(800, 600);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setLocationRelativeTo(null); // Centraliza na tela

            JLabel label = new JLabel("O Backend está rodando! Aguardando telas do Dev 3...", SwingConstants.CENTER);
            janela.add(label);

            janela.setVisible(true);
        });
    }
}