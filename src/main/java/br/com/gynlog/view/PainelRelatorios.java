package br.com.gynlog.view;

import br.com.gynlog.model.Despesa;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.AbastecimentoService;
import br.com.gynlog.service.DespesaService;
import br.com.gynlog.service.VeiculoService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelRelatorios extends JPanel {

    private final DespesaService despesaService;
    private final VeiculoService veiculoService;
    private final AbastecimentoService abastecimentoService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PainelRelatorios(ConfigurableApplicationContext context) {
        this.despesaService        = context.getBean(DespesaService.class);
        this.veiculoService        = context.getBean(VeiculoService.class);
        this.abastecimentoService  = context.getBean(AbastecimentoService.class);
        setBackground(Tema.CONTEUDO_BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        construir();
    }

    private void construir() {
        JLabel lblTitulo = new JLabel("Relatórios");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(5, 2, 12, 12));
        grid.setBackground(Tema.CONTEUDO_BG);

        grid.add(cartaoRelatorio("1. Despesas por Veículo",
                "Histórico de despesas de um veículo específico, ordenado por valor.",
                e -> relatorio1()));
        grid.add(cartaoRelatorio("2. Total Mensal da Frota",
                "Somatório de todas as despesas da frota em um mês.",
                e -> relatorio2()));
        grid.add(cartaoRelatorio("3. Combustível no Mês",
                "Total gasto com combustível em um mês específico.",
                e -> relatorio3()));
        grid.add(cartaoRelatorio("4. IPVA por Ano",
                "Somatório do IPVA de toda a frota em um ano. (Recursivo)",
                e -> relatorio4()));
        grid.add(cartaoRelatorio("5. Veículos Inativos",
                "Lista todos os veículos inativos na frota.",
                e -> relatorio5()));
        grid.add(cartaoRelatorio("6. Multas por Veículo e Ano",
                "Multas pagas por um veículo específico em um ano.",
                e -> relatorio6()));
        grid.add(cartaoRelatorio("7. Média por Categoria",
                "Média de gasto por categoria de veículo.",
                e -> relatorio7()));
        grid.add(cartaoRelatorio("8. Consumo Médio por Veículo",
                "km/L médio de cada veículo com base nos abastecimentos.",
                e -> relatorio8()));
        grid.add(cartaoRelatorio("9. Custo Médio do IPVA",
                "Custo médio do IPVA por categoria em um ano.",
                e -> relatorio9()));
        grid.add(cartaoRelatorio("10. Maior e Menor Consumo",
                "Veículo com maior e menor eficiência de combustível.",
                e -> relatorio10()));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Tema.CONTEUDO_BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel cartaoRelatorio(String titulo, String descricao, java.awt.event.ActionListener acao) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Tema.PAINEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 222, 228), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_SUBTITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JLabel lblDesc = new JLabel("<html>" + descricao + "</html>");
        lblDesc.setFont(Tema.FONTE_STATUS);
        lblDesc.setForeground(Tema.TEXTO_LABEL);

        JButton btn = Tema.botaoSecundario("Gerar");
        btn.addActionListener(acao);
        btn.setPreferredSize(new Dimension(80, 28));

        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rodape.setBackground(Tema.PAINEL_BG);
        rodape.add(btn);

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblDesc, BorderLayout.CENTER);
        card.add(rodape, BorderLayout.SOUTH);
        return card;
    }

    private void relatorio1() {
        Veiculo veiculo = selecionarVeiculo();
        if (veiculo == null) return;
        try {
            List<Despesa> lista = despesaService.listarPorVeiculo(veiculo.getIdVeiculo());
            String[] colunas = {"Data", "Tipo", "Descrição", "Valor"};
            Object[][] dados = new Object[lista.size()][4];
            double total = 0;
            for (int i = 0; i < lista.size(); i++) {
                Despesa d = lista.get(i);
                dados[i] = new Object[]{d.getData().format(FMT), d.getTipoDespesa(), d.getDescricao(), String.format("R$ %.2f", d.getValor())};
                total += d.getValor();
            }
            new TelaResultadoRelatorio("Despesas — " + veiculo, colunas, dados,
                    String.format("Total: R$ %.2f", total)).setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio2() {
        int[] mesAno = pedirMesAno(); if (mesAno == null) return;
        try {
            List<Despesa> lista = despesaService.listarPorMes(mesAno[0], mesAno[1]);
            exibirDespesas("Total Mensal " + mesAno[0] + "/" + mesAno[1], lista);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio3() {
        int[] mesAno = pedirMesAno(); if (mesAno == null) return;
        try {
            List<Despesa> lista = despesaService.listarCombustivelPorMes(mesAno[0], mesAno[1]);
            exibirDespesas("Combustível " + mesAno[0] + "/" + mesAno[1], lista);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio4() {
        String anoStr = JOptionPane.showInputDialog(this, "Informe o ano:", "IPVA por Ano", JOptionPane.QUESTION_MESSAGE);
        if (anoStr == null || anoStr.isBlank()) return;
        try {
            int ano = Integer.parseInt(anoStr.trim());
            List<Despesa> lista = despesaService.listarIpvaPorAno(ano);
            double totalRecursivo = despesaService.somarIpvaRecursivo(lista, 0);

            String[] colunas = {"Veículo", "Data", "Descrição", "Valor"};
            Object[][] dados = new Object[lista.size()][4];
            for (int i = 0; i < lista.size(); i++) {
                Despesa d = lista.get(i);
                String placa = "–";
                try { placa = veiculoService.buscarPorId(d.getIdVeiculo()).getPlaca(); } catch (Exception ignored) {}
                dados[i] = new Object[]{placa, d.getData().format(FMT), d.getDescricao(), String.format("R$ %.2f", d.getValor())};
            }
            new TelaResultadoRelatorio("IPVA " + ano, colunas, dados,
                    String.format("Total IPVA %d (cálculo recursivo): R$ %.2f", ano, totalRecursivo)).setVisible(true);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio5() {
        try {
            List<Veiculo> lista = veiculoService.listarInativos(); // usa a FilaVeiculo internamente
            String[] colunas = {"ID", "Placa", "Categoria", "Marca", "Modelo", "Ano"};
            Object[][] dados = new Object[lista.size()][6];
            for (int i = 0; i < lista.size(); i++) {
                Veiculo v = lista.get(i);
                dados[i] = new Object[]{v.getIdVeiculo(), v.getPlaca(), v.getCategoria(), v.getMarca(), v.getModelo(), v.getAnoFabricacao()};
            }
            new TelaResultadoRelatorio("Veículos Inativos", colunas, dados,
                    "Total de inativos: " + lista.size()).setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio6() {
        Veiculo veiculo = selecionarVeiculo(); if (veiculo == null) return;
        String anoStr = JOptionPane.showInputDialog(this, "Informe o ano:", "Multas por Veículo", JOptionPane.QUESTION_MESSAGE);
        if (anoStr == null || anoStr.isBlank()) return;
        try {
            List<Despesa> lista = despesaService.listarMultasPorVeiculoAno(veiculo.getIdVeiculo(), Integer.parseInt(anoStr.trim()));
            exibirDespesas("Multas — " + veiculo.getPlaca() + " / " + anoStr, lista);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio7() {
        try {
            List<Object[]> lista = despesaService.listarMediaGastoPorCategoria();
            String[] colunas = {"Categoria", "Média de Gasto (R$)"};
            Object[][] dados = new Object[lista.size()][2];
            for (int i = 0; i < lista.size(); i++) {
                dados[i] = new Object[]{lista.get(i)[0], String.format("R$ %.2f", lista.get(i)[1])};
            }
            new TelaResultadoRelatorio("Média de Gasto por Categoria", colunas, dados, null).setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio8() {
        try {
            List<Object[]> lista = abastecimentoService.listarConsumoMedioPorVeiculo();
            String[] colunas = {"Modelo", "Placa", "Média km/L", "Total km", "Total Litros"};
            Object[][] dados = new Object[lista.size()][5];
            for (int i = 0; i < lista.size(); i++) {
                Object[] r = lista.get(i);
                dados[i] = new Object[]{
                        r[1], r[2],
                        String.format("%.2f km/L", r[3]),
                        String.format("%.0f km", r[4]),
                        String.format("%.3f L", r[5])
                };
            }
            new TelaResultadoRelatorio("Consumo Médio por Veículo", colunas, dados,
                    "Ordenado do maior para o menor consumo.").setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio9() {
        String anoStr = JOptionPane.showInputDialog(this, "Informe o ano:", "Custo Médio IPVA", JOptionPane.QUESTION_MESSAGE);
        if (anoStr == null || anoStr.isBlank()) return;
        try {
            List<Object[]> lista = despesaService.listarMediaIpvaPorAno(Integer.parseInt(anoStr.trim()));
            String[] colunas = {"Categoria", "Total IPVA", "Qtd Veículos", "Média IPVA"};
            Object[][] dados = new Object[lista.size()][4];
            for (int i = 0; i < lista.size(); i++) {
                Object[] r = lista.get(i);
                dados[i] = new Object[]{r[0], String.format("R$ %.2f", r[1]), r[2], String.format("R$ %.2f", r[3])};
            }
            new TelaResultadoRelatorio("Custo Médio IPVA " + anoStr, colunas, dados, null).setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private void relatorio10() {
        try {
            List<Object[]> lista = abastecimentoService.listarConsumoMedioPorVeiculo();
            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Sem dados suficientes de abastecimento.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Object[] maior = lista.get(0);
            Object[] menor = lista.get(lista.size() - 1);

            String[] colunas = {"Posição", "Modelo", "Placa", "Média km/L"};
            Object[][] dados = {
                    {"🏆 Maior consumo", maior[1], maior[2], String.format("%.2f km/L", maior[3])},
                    {"⚠ Menor consumo",  menor[1], menor[2], String.format("%.2f km/L", menor[3])}
            };
            new TelaResultadoRelatorio("Maior e Menor Consumo da Frota", colunas, dados, null).setVisible(true);
        } catch (Exception e) { erro(e); }
    }

    private Veiculo selecionarVeiculo() {
        try {
            List<Veiculo> veiculos = veiculoService.listar();
            if (veiculos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum veículo cadastrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return (Veiculo) JOptionPane.showInputDialog(this,
                    "Selecione o veículo:", "Selecionar Veículo",
                    JOptionPane.QUESTION_MESSAGE, null,
                    veiculos.toArray(), veiculos.get(0));
        } catch (Exception e) { erro(e); return null; }
    }

    private int[] pedirMesAno() {
        String input = JOptionPane.showInputDialog(this, "Informe Mês/Ano (MM/AAAA):", "Período", JOptionPane.QUESTION_MESSAGE);
        if (input == null || input.isBlank()) return null;
        try {
            String[] partes = input.trim().split("/");
            return new int[]{Integer.parseInt(partes[0]), Integer.parseInt(partes[1])};
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Formato inválido. Use MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void exibirDespesas(String titulo, List<Despesa> lista) {
        String[] colunas = {"Veículo", "Tipo", "Descrição", "Data", "Valor"};
        Object[][] dados = new Object[lista.size()][5];
        double total = 0;
        for (int i = 0; i < lista.size(); i++) {
            Despesa d = lista.get(i);
            String placa = "–";
            try { placa = veiculoService.buscarPorId(d.getIdVeiculo()).getPlaca(); } catch (Exception ignored) {}
            dados[i] = new Object[]{placa, d.getTipoDespesa(), d.getDescricao(), d.getData().format(FMT), String.format("R$ %.2f", d.getValor())};
            total += d.getValor();
        }
        new TelaResultadoRelatorio(titulo, colunas, dados, String.format("Total: R$ %.2f", total)).setVisible(true);
    }

    private void erro(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }
}