package br.com.gynlog.view;

import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TelaResultadoRelatorio extends JFrame {

    private final List<?> dadosBrutos;
    private final JTable table;
    private final String tituloRelatorio;

    public TelaResultadoRelatorio(String titulo, String[] colunas, Object[][] dadosVisual, String resumoFinal, List<?> dadosBrutos) {
        this.tituloRelatorio = titulo;
        this.dadosBrutos = dadosBrutos;

        setTitle("Relatório: " + titulo);
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitulo, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(dadosVisual, colunas);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (resumoFinal != null) {
            rodape.add(new JLabel(resumoFinal));
        }
        add(rodape, BorderLayout.SOUTH);

        exportarAutomaticamente();
    }

    // --- LÓGICA DE EXPORTAÇÃO MANTIDA IGUAL (PERFEITA) ---
    private void exportarAutomaticamente() {
        try {
            String nomeLimpo = tituloRelatorio.replace(" ", "_").replace("/", "-").replace(":", "");
            String nomeArquivo = "Relatorio_" + nomeLimpo + ".txt";
            File arquivo = new File(nomeArquivo);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
                boolean exportouBruto = false;
                if (dadosBrutos != null && !dadosBrutos.isEmpty()) {
                    Object itemTeste = dadosBrutos.get(0);
                    if (itemTeste instanceof Movimentacao) {
                        writer.write("ID;ID_Veiculo;Tipo_Despesa;Descricao;Data;Valor");
                        writer.newLine();
                        for (Object item : dadosBrutos) writer.write(((Movimentacao) item).toCsvLine() + "\n");
                        exportouBruto = true;
                    } else if (itemTeste instanceof Veiculo) {
                        writer.write("ID;Placa;Marca;Modelo;Ano;Status;Tipo");
                        writer.newLine();
                        for (Object item : dadosBrutos) writer.write(((Veiculo) item).toCsvLine() + "\n");
                        exportouBruto = true;
                    }
                }

                if (!exportouBruto) {
                    for (int i = 0; i < table.getColumnCount(); i++) writer.write(table.getColumnName(i) + ";");
                    writer.newLine();
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount(); j++) {
                            Object valor = table.getValueAt(i, j);
                            String texto = (valor != null) ? valor.toString().replace("R$ ", "").replace(".", "").replace(",", ".") : "";
                            writer.write(texto + ";");
                        }
                        writer.newLine();
                    }
                }
            }
            System.out.println("Arquivo gerado: " + arquivo.getAbsolutePath());
        } catch (IOException e) { e.printStackTrace(); }
    }
}