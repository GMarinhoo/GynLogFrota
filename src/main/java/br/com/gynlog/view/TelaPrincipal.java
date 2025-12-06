package br.com.gynlog.view;

import br.com.gynlog.model.Usuario;
import br.com.gynlog.enums.TipoUsuario;
import br.com.gynlog.service.MatematicaService;
import br.com.gynlog.service.MovimentacaoService;
import br.com.gynlog.service.VeiculoService;
import br.com.gynlog.service.UsuarioService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;
    private final ConfigurableApplicationContext context;

    public TelaPrincipal(Usuario usuario, ConfigurableApplicationContext context) {
        this.usuarioLogado = usuario;
        this.context = context;
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Menu Principal - GynLog");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void criarComponentes() {
        JPanel painelTopo = new JPanel();
        painelTopo.add(new JLabel("Usuário: " + usuarioLogado.getNome() + " [" + usuarioLogado.getTipo() + "]"));
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(4, 2, 10, 10));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnVeiculos = new JButton("Gerenciar Veículos");
        btnVeiculos.addActionListener(e -> abrirCadastroVeiculo());
        painelBotoes.add(btnVeiculos);

        JButton btnDespesas = new JButton("Lançar Despesas");
        btnDespesas.addActionListener(e -> abrirLancamentoMovimentacao());
        painelBotoes.add(btnDespesas);

        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        JButton btnRelatorios = new JButton("Relatórios");

        if (usuarioLogado.getTipo() == TipoUsuario.GERENTE) {
            btnUsuarios.addActionListener(e -> abrirGerenciarUsuarios());
            btnRelatorios.addActionListener(e -> abrirRelatorios());
        } else {
            btnUsuarios.setEnabled(false);
            btnRelatorios.setEnabled(false);
        }

        painelBotoes.add(btnUsuarios);
        painelBotoes.add(btnRelatorios);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> sair());
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.CENTER);
    }

    private void abrirCadastroVeiculo() {
        VeiculoService service = context.getBean(VeiculoService.class);
        new TelaCadastroVeiculo(service).setVisible(true);
    }

    private void abrirLancamentoMovimentacao() {
        VeiculoService vs = context.getBean(VeiculoService.class);
        MovimentacaoService ms = context.getBean(MovimentacaoService.class);
        new TelaLancamentoMovimentacao(ms, vs).setVisible(true);
    }

    private void abrirGerenciarUsuarios() {
        UsuarioService us = context.getBean(UsuarioService.class);
        new TelaGerenciarUsuarios(us, usuarioLogado).setVisible(true);
    }

    private void abrirRelatorios() {
        String[] opcoes = {
                "1. Despesas por Veículo", "2. Total Mensal", "3. Combustível (Mês)",
                "4. IPVA (Ano)", "5. Veículos Inativos", "6. Multas (Ano)", "7. Matrizes"
        };

        String escolha = (String) JOptionPane.showInputDialog(this, "Escolha:", "Relatórios", JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha != null) {
            switch (escolha.charAt(0)) {
                case '1' -> relatorioDespesasPorVeiculo();
                case '2' -> relatorioTotalMensal();
                case '3' -> relatorioCombustivel();
                case '4' -> relatorioIpva();
                case '5' -> relatorioInativos();
                case '6' -> relatorioMultas();
                case '7' -> relatorioMatematico();
            }
        }
    }

    // --- MÉTODOS DE RELATÓRIO QUE FALTAVAM ---

    private void relatorioDespesasPorVeiculo() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Digite a Placa:");
            if (placa == null || placa.isEmpty()) return;

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> {
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch (Exception e) { return false; }
                    }).toList();

            exibirRelatorioSimples("Extrato " + placa, lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void relatorioTotalMensal() {
        try {
            String data = JOptionPane.showInputDialog(this, "Mês/Ano (MM/AAAA):");
            if (data == null) return;
            int mes = Integer.parseInt(data.split("/")[0]);
            int ano = Integer.parseInt(data.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes && m.getData().getYear() == ano)
                    .toList();
            exibirRelatorioSimples("Total " + data.replace("/", "-"), lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida"); }
    }

    private void relatorioCombustivel() {
        try {
            String data = JOptionPane.showInputDialog(this, "Mês/Ano (MM/AAAA):");
            if (data == null) return;
            int mes = Integer.parseInt(data.split("/")[0]);
            int ano = Integer.parseInt(data.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes &&
                            m.getData().getYear() == ano &&
                            m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.COMBUSTIVEL)
                    .toList();
            exibirRelatorioSimples("Combustivel " + data.replace("/", "-"), lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida"); }
    }

    private void relatorioIpva() {
        try {
            String anoStr = JOptionPane.showInputDialog(this, "Ano:");
            if (anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano &&
                            m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.IPVA)
                    .toList();
            exibirRelatorioSimples("IPVA " + ano, lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Ano inválido"); }
    }

    private void relatorioMultas() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Placa:");
            String anoStr = JOptionPane.showInputDialog(this, "Ano:");
            if (placa == null || anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano &&
                            m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.MULTA)
                    .filter(m -> {
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch (Exception e) { return false; }
                    }).toList();
            exibirRelatorioSimples("Multas " + placa + " " + ano, lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro"); }
    }

    private void relatorioInativos() {
        try {
            var inativos = context.getBean(VeiculoService.class).listar().stream()
                    .filter(v -> !v.isAtivo()).toList();

            String[] colunas = {"ID", "Placa", "Modelo", "Marca"};
            Object[][] dados = new Object[inativos.size()][4];
            for(int i=0; i<inativos.size(); i++) {
                var v = inativos.get(i);
                dados[i][0] = v.getIdVeiculo();
                dados[i][1] = v.getPlaca();
                dados[i][2] = v.getModelo();
                dados[i][3] = v.getMarca();
            }
            new TelaResultadoRelatorio("Inativos", colunas, dados, "Qtd: " + inativos.size(), inativos).setVisible(true);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void relatorioMatematico() {
        try {
            MatematicaService mathService = context.getBean(MatematicaService.class);
            var resultado = mathService.gerarRelatorioCompleto();

            String[] ops = {"Matriz A", "Matriz B", "Matriz C"};
            int i = JOptionPane.showOptionDialog(this, "Qual matriz?", "Matemática", 0, 3, null, ops, ops[0]);

            if(i >= 0) {
                MatematicaService.DadosMatriz d = (i==0) ? resultado.matrizA : (i==1) ? resultado.matrizB : resultado.matrizC;

                int lin = d.dados.length;
                int col = d.colunas.length;
                Object[][] dadosStr = new Object[lin][col];
                for(int r=0; r<lin; r++) {
                    dadosStr[r][0] = d.dados[r][0];
                    for(int c=1; c<col; c++) dadosStr[r][c] = String.valueOf(d.dados[r][c]);
                }
                new TelaResultadoRelatorio(d.titulo, d.colunas, dadosStr, d.resumo, java.util.List.of()).setVisible(true);
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void exibirRelatorioSimples(String titulo, java.util.List<br.com.gynlog.model.Movimentacao> lista) {
        String[] colunas = {"Data", "Tipo", "Descrição", "Valor"};
        Object[][] dados = new Object[lista.size()][4];
        double total = 0;
        for (int i = 0; i < lista.size(); i++) {
            var m = lista.get(i);
            dados[i][0] = m.getData();
            dados[i][1] = m.getTipoDespesa();
            dados[i][2] = m.getDescricao();
            dados[i][3] = "R$ " + m.getValor();
            total += m.getValor();
        }
        new TelaResultadoRelatorio(titulo, colunas, dados, "Total: R$ " + total, lista).setVisible(true);
    }

    private void sair() {
        dispose();
        new TelaLogin(context).setVisible(true);
    }
}