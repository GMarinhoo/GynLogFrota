package br.com.gynlog.app;

import javax.swing.*;

public class GynLogApp {

    public static void main(String[] args) {

        System.out.println("Iniciando sistema GynLog...");


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
            janela.setLocationRelativeTo(null);

            JLabel label = new JLabel("Backend carregado! Telas ainda não implementadas.", SwingConstants.CENTER);
            janela.add(label);

            janela.setVisible(true);
        });
    }
}
