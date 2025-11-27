package br.com.gynlog.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaResultadoRelatorio extends JFrame {

    private static final Color COR_HEADER = new Color(230, 126, 34); // Laranja Relatório

    public TelaResultadoRelatorio(String titulo, String[] colunas, Object[][] dados, String resumoFinal) {
        setTitle(titulo);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header Moderno
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Tabela Estilizada
        DefaultTableModel model = new DefaultTableModel(dados, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(new Color(241, 196, 15)); // Amarelo seleção

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        // Rodapé
        if (resumoFinal != null) {
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setBackground(new Color(236, 240, 241));
            footer.setBorder(new EmptyBorder(10, 20, 10, 20));

            JLabel lblResumo = new JLabel(resumoFinal);
            lblResumo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblResumo.setForeground(new Color(44, 62, 80));
            footer.add(lblResumo);
            add(footer, BorderLayout.SOUTH);
        }
    }
}