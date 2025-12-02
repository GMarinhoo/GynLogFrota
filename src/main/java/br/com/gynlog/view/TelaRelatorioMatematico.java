package br.com.gynlog.view;

import br.com.gynlog.service.MatematicaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TelaRelatorioMatematico extends JFrame {

    private static final Color COR_HEADER = new Color(142, 68, 173); // Roxo

    public TelaRelatorioMatematico(MatematicaService.RelatorioMatematicoDTO dados) {
        setTitle("Fundamentos Matemáticos - Matrizes de Custo");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel lblTitulo = new JLabel("CÁLCULO MATRICIAL DE CUSTOS");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Abas
        JTabbedPane abas = new JTabbedPane();
        abas.setFont(new Font("Segoe UI", Font.BOLD, 14));

        abas.addTab("Matriz A (Quantidade)", criarPainelTabela(dados.matrizA));
        abas.addTab("Matriz B (Custo Médio)", criarPainelTabela(dados.matrizB));
        abas.addTab("Matriz C (Resultado)", criarPainelTabela(dados.matrizC));

        add(abas, BorderLayout.CENTER);
    }

    private JPanel criarPainelTabela(MatematicaService.DadosMatriz dados) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Título Interno
        JLabel lblTitulo = new JLabel(dados.titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        painel.add(lblTitulo, BorderLayout.NORTH);

        // Tabela
        DefaultTableModel model = new DefaultTableModel(dados.dados, dados.colunas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);

        painel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Rodapé (para Matriz C)
        if (dados.resumo != null) {
            JLabel lblResumo = new JLabel(dados.resumo, SwingConstants.RIGHT);
            lblResumo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblResumo.setForeground(new Color(44, 62, 80));
            lblResumo.setBorder(new EmptyBorder(10, 0, 0, 0));
            painel.add(lblResumo, BorderLayout.SOUTH);
        }

        return painel;
    }
}