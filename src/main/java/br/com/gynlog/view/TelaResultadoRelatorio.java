package br.com.gynlog.view;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TelaResultadoRelatorio extends JFrame {

    private static final Color COR_HEADER = new Color(230, 126, 34);
    private final List<?> dadosBrutos;
    private final JTable table; // Agora usamos a tabela para exportar se precisar
    private final String tituloRelatorio;

    public TelaResultadoRelatorio(String titulo, String[] colunas, Object[][] dadosVisual, String resumoFinal, List<?> dadosBrutos) {
        this.tituloRelatorio = titulo;
        this.dadosBrutos = dadosBrutos;

        setTitle("Visualização: " + titulo);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // --- TABELA ---
        DefaultTableModel model = new DefaultTableModel(dadosVisual, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(44, 62, 80));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionBackground(new Color(241, 196, 15));

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        // --- RODAPÉ ---
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

        exportarAutomaticamente();
    }

    private void exportarAutomaticamente() {
        try {
            String nomeLimpo = tituloRelatorio.replace(" ", "_").replace("/", "-").replace(":", "");
            String nomeArquivo = "Relatorio_" + nomeLimpo + ".txt";
            File arquivo = new File(nomeArquivo);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

                // 1. TENTA EXPORTAR PELO MÉTODO BRUTO (VEICULO/MOVIMENTACAO)
                boolean exportouBruto = false;

                if (dadosBrutos != null && !dadosBrutos.isEmpty()) {
                    Object itemTeste = dadosBrutos.get(0);

                    if (itemTeste instanceof Movimentacao) {
                        writer.write("ID;ID_Veiculo;Tipo_Despesa;Descricao;Data;Valor");
                        writer.newLine();
                        for (Object item : dadosBrutos) writer.write(((Movimentacao) item).toCsvLine() + "\n");
                        exportouBruto = true;
                    }
                    else if (itemTeste instanceof Veiculo) {
                        writer.write("ID;Placa;Marca;Modelo;Ano;Status;Tipo");
                        writer.newLine();
                        for (Object item : dadosBrutos) writer.write(((Veiculo) item).toCsvLine() + "\n");
                        exportouBruto = true;
                    }
                }

                // 2. SE NÃO EXPORTOU BRUTO (CASO DAS MATRIZES), EXPORTA A TABELA VISUAL
                if (!exportouBruto) {
                    // Escreve Cabeçalho da Tabela
                    for (int i = 0; i < table.getColumnCount(); i++) {
                        writer.write(table.getColumnName(i) + ";");
                    }
                    writer.newLine();

                    // Escreve Linhas da Tabela
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object valor = table.getValueAt(i, j);
                            // Remove o "R$ " para ficar CSV puro se for dinheiro
                            String texto = (valor != null) ? valor.toString().replace("R$ ", "").replace(".", "").replace(",", ".") : "";
                            writer.write(texto + ";");
                        }
                        writer.newLine();
                    }
                }
            }

            System.out.println("✅ [AUTO] Arquivo gerado: " + arquivo.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro ao exportar: " + e.getMessage());
        }
    }
}