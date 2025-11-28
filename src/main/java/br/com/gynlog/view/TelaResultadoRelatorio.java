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
    private final String tituloRelatorio;

    public TelaResultadoRelatorio(String titulo, String[] colunas, Object[][] dadosVisual, String resumoFinal, List<?> dadosBrutos) {
        this.tituloRelatorio = titulo;
        this.dadosBrutos = dadosBrutos;

        setTitle("Visualização: " + titulo);
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(COR_HEADER);
        header.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblTitulo = new JLabel(titulo.toUpperCase());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Tabela
        DefaultTableModel model = new DefaultTableModel(dadosVisual, colunas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
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

        // Exportação Automática
        exportarAutomaticamente();
    }

    private void exportarAutomaticamente() {
        try {
            String nomeLimpo = tituloRelatorio.replace(" ", "_").replace("/", "-");
            String nomeArquivo = "Relatorio_" + nomeLimpo + ".txt";
            File arquivo = new File(nomeArquivo);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {

                // Escreve as Colunas
                if (!dadosBrutos.isEmpty()) {
                    Object primeiroItem = dadosBrutos.get(0);

                    if (primeiroItem instanceof Movimentacao) {
                        // Cabeçalho para Despesas
                        writer.write("ID;ID_Veiculo;Tipo_Despesa;Descricao;Data;Valor");
                        writer.newLine();
                    } else if (primeiroItem instanceof Veiculo) {
                        // Cabeçalho para Veículos
                        writer.write("ID;Placa;Marca;Modelo;Ano;Status");
                        writer.newLine();
                    }
                }

                // Escreve os dados
                for (Object item : dadosBrutos) {
                    if (item instanceof Movimentacao) {
                        writer.write(((Movimentacao) item).toCsvLine());
                    } else if (item instanceof Veiculo) {
                        writer.write(((Veiculo) item).toCsvLine());
                    }
                    writer.newLine();
                }
            }

            System.out.println("✅ [AUTO] Arquivo gerado com colunas: " + arquivo.getAbsolutePath());

            // Abre o arquivo sozinho para conferir
            /*if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(arquivo);
            }
             */

        } catch (IOException e) {
            System.err.println("Erro ao exportar: " + e.getMessage());
        }
    }
}