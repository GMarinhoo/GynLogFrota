package br.com.gynlog.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TelaResultadoRelatorio extends JFrame {

    private final String titulo;
    private final String[] colunas;
    private final Object[][] dados;
    private final String rodape;
    private JTable tabela;

    public TelaResultadoRelatorio(String titulo, String[] colunas, Object[][] dados, String rodape) {
        this.titulo  = titulo;
        this.colunas = colunas;
        this.dados   = dados;
        this.rodape  = rodape;
        construir();
    }

    private void construir() {
        setTitle("Relatório: " + titulo);
        setSize(820, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Tema.CONTEUDO_BG);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.PAINEL_BG);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JButton btnExportar = Tema.botaoSecundario("Exportar CSV");
        btnExportar.addActionListener(e -> exportarCSV());

        topo.add(lblTitulo, BorderLayout.WEST);
        topo.add(btnExportar, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        DefaultTableModel modelo = new DefaultTableModel(dados, colunas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        Tema.estilizarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        scroll.getViewport().setBackground(Tema.PAINEL_BG);
        add(scroll, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new BorderLayout());
        painelRodape.setBackground(Tema.PAINEL_BG);
        painelRodape.setBorder(BorderFactory.createEmptyBorder(10, 20, 14, 20));

        if (rodape != null && !rodape.isBlank()) {
            JLabel lblRodape = new JLabel(rodape);
            lblRodape.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblRodape.setForeground(Tema.TEXTO_TITULO);
            painelRodape.add(lblRodape, BorderLayout.WEST);
        }

        JLabel lblQtd = new JLabel("Registros: " + dados.length);
        lblQtd.setFont(Tema.FONTE_STATUS);
        lblQtd.setForeground(Tema.TEXTO_LABEL);
        painelRodape.add(lblQtd, BorderLayout.EAST);

        add(painelRodape, BorderLayout.SOUTH);
    }

    private void exportarCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("Relatorio_" + titulo.replace(" ", "_").replace("/", "-") + ".csv"));
        fc.setDialogTitle("Salvar relatório como CSV");
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File arquivo = fc.getSelectedFile();
        if (!arquivo.getName().endsWith(".csv"))
            arquivo = new File(arquivo.getAbsolutePath() + ".csv");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivo))) {
            StringBuilder header = new StringBuilder();
            for (int i = 0; i < colunas.length; i++) {
                header.append(colunas[i]);
                if (i < colunas.length - 1) header.append(";");
            }
            bw.write(header.toString());
            bw.newLine();

            for (Object[] linha : dados) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < linha.length; i++) {
                    String val = linha[i] != null ? linha[i].toString() : "";
                    sb.append(val);
                    if (i < linha.length - 1) sb.append(";");
                }
                bw.write(sb.toString());
                bw.newLine();
            }

            if (rodape != null && !rodape.isBlank()) {
                bw.newLine();
                bw.write(rodape);
                bw.newLine();
            }

            JOptionPane.showMessageDialog(this,
                    "Arquivo exportado com sucesso!\n" + arquivo.getAbsolutePath(),
                    "Exportado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}