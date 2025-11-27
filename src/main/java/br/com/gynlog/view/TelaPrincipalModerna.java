package br.com.gynlog.view;

import br.com.gynlog.model.Usuario;
import br.com.gynlog.enums.TipoUsuario;
import br.com.gynlog.service.MovimentacaoService;
import br.com.gynlog.service.VeiculoService;
import br.com.gynlog.service.UsuarioService; // Importante
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaPrincipalModerna extends JFrame {

    private final Usuario usuarioLogado;
    private final ConfigurableApplicationContext context;

    // Cores
    private static final Color COR_PRIMARY = new Color(41, 128, 185);
    private static final Color COR_BG = new Color(236, 240, 241);
    private static final Color COR_DARK = new Color(44, 62, 80);

    public TelaPrincipalModerna(Usuario usuario, ConfigurableApplicationContext context) {
        this.usuarioLogado = usuario;
        this.context = context;
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("GynLog - Sistema de Controle de Frota");
        setSize(1100, 700); // Aumentei um pouco a largura para caber 4 botões
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_BG);
    }

    private void criarComponentes() {
        // --- HEADER ---
        JPanel painelHeader = new JPanel(new BorderLayout());
        painelHeader.setBackground(COR_PRIMARY);
        painelHeader.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblTitulo = new JLabel("🚚 GynLog - Controle de Frota");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        painelHeader.add(lblTitulo, BorderLayout.WEST);

        JLabel lblUser = new JLabel("Olá, " + usuarioLogado.getNome());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> sair());

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        direita.setBackground(COR_PRIMARY);
        direita.add(lblUser);
        direita.add(btnSair);
        painelHeader.add(direita, BorderLayout.EAST);

        add(painelHeader, BorderLayout.NORTH);

        // --- CONTEÚDO (MENU) ---
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(COR_BG);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Ajuste do Grid: (1, 0) significa 1 linha e colunas infinitas (automático)
        JPanel painelCards = new JPanel(new GridLayout(1, 0, 20, 0));
        painelCards.setBackground(COR_BG);

        // Botão 1: Veículos
        painelCards.add(criarBotaoMenu("🚗", "Veículos", new Color(52, 152, 219), e -> abrirCadastroVeiculo()));

        // Botão 2: Despesas
        painelCards.add(criarBotaoMenu("💰", "Despesas", new Color(155, 89, 182), e -> abrirLancamentoMovimentacao()));

        // Botões de Gerente
        if(usuarioLogado.getTipo() == TipoUsuario.GERENTE) {
            // Botão 3: Usuários (NOVO)
            painelCards.add(criarBotaoMenu("👥", "Usuários", new Color(46, 204, 113), e -> abrirGerenciarUsuarios()));

            // Botão 4: Relatórios
            painelCards.add(criarBotaoMenu("📊", "Relatórios", new Color(230, 126, 34), e -> abrirRelatorios()));
        } else {
            // Se for funcionário, mostra um bloco vazio ou aviso
            JButton btnBloq = new JButton("<html><center><h3>Área Gerencial<br>Bloqueada</h3></center></html>");
            btnBloq.setEnabled(false);
            btnBloq.setBackground(Color.GRAY);
            painelCards.add(btnBloq);
        }

        painelConteudo.add(painelCards);
        add(painelConteudo, BorderLayout.CENTER);
    }

    private JButton criarBotaoMenu(String icone, String texto, Color cor, ActionListener acao) {
        // Layout do botão com BorderLayout para melhor controle
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(acao);

        // Painel interno para organizar ícone e texto - CENTRALIZADO
        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        conteudo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona espaço flexível antes (empurra conteúdo para o centro)
        conteudo.add(Box.createVerticalGlue());

        // Label do ícone - BRANCO
        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        lblIcone.setForeground(Color.WHITE); // GARANTE QUE SEJA BRANCO
        lblIcone.setAlignmentX(Component.CENTER_ALIGNMENT);

        conteudo.add(lblIcone);
        conteudo.add(Box.createVerticalStrut(15));

        // Label do texto - BRANCO
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Arial", Font.BOLD, 16));
        lblTexto.setForeground(Color.WHITE); // GARANTE QUE SEJA BRANCO
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        conteudo.add(lblTexto);

        // Adiciona espaço flexível depois (empurra conteúdo para o centro)
        conteudo.add(Box.createVerticalGlue());

        btn.add(conteudo, BorderLayout.CENTER);

        return btn;
    }

    // --- MÉTODOS DE AÇÃO (INTEGRAÇÃO SPRING) ---

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
        // Pega o service de usuário do contexto
        UsuarioService us = context.getBean(UsuarioService.class);
        new TelaGerenciarUsuarios(us, usuarioLogado).setVisible(true);
    }

    private void abrirRelatorios() {
        if(usuarioLogado.getTipo() == TipoUsuario.FUNCIONARIO) {
            JOptionPane.showMessageDialog(this, "Acesso Negado.");
            return;
        }

        String[] opcoes = {
                "1. Despesas por Veículo",
                "2. Total Mensal da Frota",
                "3. Gastos com Combustível (Mês)",
                "4. Somatório IPVA (Ano)",
                "5. Veículos Inativos",
                "6. Multas por Veículo (Ano)"
        };

        String escolha = (String) JOptionPane.showInputDialog(
                this,
                "Selecione o Relatório:",
                "Relatórios Gerenciais",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        if (escolha != null) {
            switch (escolha.charAt(0)) { // Pega o número da opção (1, 2, 3...)
                case '1' -> relatorioDespesasPorVeiculo();
                case '2' -> relatorioTotalMensal();
                case '3' -> relatorioCombustivel();
                case '4' -> relatorioIpva();
                case '5' -> relatorioInativos();
                case '6' -> relatorioMultas();
            }
        }
    }

    private void sair() {
        dispose();
        new TelaLogin(context).setVisible(true);
    }

    private void gerarRelatorioInativos() {
        try {
            // Pega todos os veículos
            java.util.List<br.com.gynlog.model.Veiculo> lista =
                    context.getBean(br.com.gynlog.service.VeiculoService.class).listar();

            // Filtra só os inativos
            var inativos = lista.stream().filter(v -> !v.isAtivo()).toList();

            // Prepara os dados para a tabela
            String[] colunas = {"ID", "Placa", "Modelo", "Marca"};
            Object[][] dados = new Object[inativos.size()][4];

            for(int i=0; i<inativos.size(); i++) {
                var v = inativos.get(i);
                dados[i][0] = v.getIdVeiculo();
                dados[i][1] = v.getPlaca();
                dados[i][2] = v.getModelo();
                dados[i][3] = v.getMarca();
            }

            // Abre a tela genérica
            new TelaResultadoRelatorio("Relatório de Veículos Inativos", colunas, dados, "Total: " + inativos.size()).setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    private void relatorioDespesasPorVeiculo() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Digite a PLACA do veículo:");
            if(placa == null || placa.isEmpty()) return;

            // Busca movimentações e filtra pela placa
            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> {
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch (Exception e) { return false; }
                    }).toList();

            String[] colunas = {"Data", "Tipo", "Descrição", "Valor"};
            Object[][] dados = new Object[lista.size()][4];
            double total = 0;

            for (int i = 0; i < lista.size(); i++) {
                var m = lista.get(i);
                dados[i][0] = m.getData();
                dados[i][1] = m.getTipoDespesa();
                dados[i][2] = m.getDescricao();
                dados[i][3] = String.format("R$ %.2f", m.getValor());
                total += m.getValor();
            }

            new TelaResultadoRelatorio("Extrato: " + placa.toUpperCase(), colunas, dados, String.format("Total Geral: R$ %.2f", total)).setVisible(true);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    // 2. Somatório geral de todas as despesas da frota em um determinado mês
    private void relatorioTotalMensal() {
        try {
            String mesAno = JOptionPane.showInputDialog(this, "Digite o Mês/Ano (ex: 11/2025):");
            if(mesAno == null || !mesAno.contains("/")) return;
            int mes = Integer.parseInt(mesAno.split("/")[0]);
            int ano = Integer.parseInt(mesAno.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes && m.getData().getYear() == ano)
                    .toList();

            exibirRelatorioSimples("Despesas de " + mesAno, lista);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida!"); }
    }

    // 3. Total de gastos da frota com combustível em um determinado mês
    private void relatorioCombustivel() {
        try {
            String mesAno = JOptionPane.showInputDialog(this, "Digite o Mês/Ano (ex: 11/2025):");
            if(mesAno == null || !mesAno.contains("/")) return;
            int mes = Integer.parseInt(mesAno.split("/")[0]);
            int ano = Integer.parseInt(mesAno.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes
                            && m.getData().getYear() == ano
                            && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.COMBUSTIVEL)
                    .toList();

            exibirRelatorioSimples("Combustível em " + mesAno, lista);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida!"); }
    }

    // 4. Somatório do IPVA de um determinado ano de toda a frota
    private void relatorioIpva() {
        try {
            String anoStr = JOptionPane.showInputDialog(this, "Digite o Ano (ex: 2025):");
            if(anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano
                            && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.IPVA)
                    .toList();

            exibirRelatorioSimples("IPVA de " + ano, lista);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Ano inválido!"); }
    }

    // 5. Listar todos os veículos inativos na frota
    private void relatorioInativos() {
        try {
            var inativos = context.getBean(VeiculoService.class).listar().stream()
                    .filter(v -> !v.isAtivo())
                    .toList();

            String[] colunas = {"ID", "Placa", "Modelo", "Marca"};
            Object[][] dados = new Object[inativos.size()][4];

            for(int i=0; i<inativos.size(); i++) {
                var v = inativos.get(i);
                dados[i][0] = v.getIdVeiculo();
                dados[i][1] = v.getPlaca();
                dados[i][2] = v.getModelo();
                dados[i][3] = v.getMarca();
            }

            new TelaResultadoRelatorio("Veículos Inativos", colunas, dados, "Qtd: " + inativos.size()).setVisible(true);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    // 6. Relatório das multas pagas por veículo em um determinado ano
    private void relatorioMultas() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Digite a PLACA:");
            String anoStr = JOptionPane.showInputDialog(this, "Digite o Ano:");
            if(placa == null || anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano
                            && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.MULTA)
                    .filter(m -> { // Filtra placa
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch(Exception e) { return false; }
                    }).toList();

            exibirRelatorioSimples("Multas " + placa + " em " + ano, lista);

        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Dados inválidos!"); }
    }

    // Auxiliar para não repetir código
    private void exibirRelatorioSimples(String titulo, java.util.List<br.com.gynlog.model.Movimentacao> lista) {
        String[] colunas = {"Data", "Tipo", "Descrição", "Valor"};
        Object[][] dados = new Object[lista.size()][4];
        double total = 0;

        for (int i = 0; i < lista.size(); i++) {
            var m = lista.get(i);
            dados[i][0] = m.getData();
            dados[i][1] = m.getTipoDespesa();
            dados[i][2] = m.getDescricao();
            dados[i][3] = String.format("R$ %.2f", m.getValor());
            total += m.getValor();
        }
        new TelaResultadoRelatorio(titulo, colunas, dados, String.format("Total: R$ %.2f", total)).setVisible(true);
    }
}