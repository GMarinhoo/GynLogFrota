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
import java.awt.event.*;

public class TelaPrincipal extends JFrame {

    private final Usuario usuarioLogado;
    private final ConfigurableApplicationContext context;

    // Cores
    private static final Color COR_PRIMARY = new Color(41, 128, 185);
    private static final Color COR_BG = new Color(236, 240, 241);

    public TelaPrincipal(Usuario usuario, ConfigurableApplicationContext context) {
        this.usuarioLogado = usuario;
        this.context = context;
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("GynLog - Sistema de Controle de Frota");
        setSize(1100, 700);
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
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        painelHeader.add(lblTitulo, BorderLayout.WEST);

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        direita.setBackground(COR_PRIMARY);

        JLabel lblUser = new JLabel("Olá, " + usuarioLogado.getNome());
        lblUser.setForeground(Color.WHITE);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSair.setBackground(Color.WHITE);
        btnSair.setForeground(COR_PRIMARY);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> sair());

        direita.add(lblUser);
        direita.add(btnSair);
        painelHeader.add(direita, BorderLayout.EAST);

        add(painelHeader, BorderLayout.NORTH);

        // --- MENU ---
        JPanel painelConteudo = new JPanel();
        painelConteudo.setLayout(new BoxLayout(painelConteudo, BoxLayout.Y_AXIS));
        painelConteudo.setBackground(COR_BG);
        painelConteudo.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Grid com 4 colunas (agora ficou limpo de novo, sem o botão roxo solto)
        JPanel painelCards = new JPanel(new GridLayout(1, 0, 30, 0));
        painelCards.setBackground(COR_BG);

        painelCards.add(criarBotaoMenu("🚗", "Veículos", new Color(52, 152, 219), e -> abrirCadastroVeiculo()));
        painelCards.add(criarBotaoMenu("💰", "Despesas", new Color(155, 89, 182), e -> abrirLancamentoMovimentacao()));

        if(usuarioLogado.getTipo() == TipoUsuario.GERENTE) {
            painelCards.add(criarBotaoMenu("👥", "Usuários", new Color(46, 204, 113), e -> abrirGerenciarUsuarios()));
            // Agora o botão de Matrizes SUMIU daqui e foi para dentro de Relatórios
            painelCards.add(criarBotaoMenu("📊", "Relatórios", new Color(230, 126, 34), e -> abrirRelatorios()));
        } else {
            JButton btnBloq = new JButton("<html><center><span style='font-size:40px'>🔒</span><br><br><span style='font-size:14px'>Acesso<br>Restrito</span></center></html>");
            btnBloq.setEnabled(false);
            btnBloq.setBackground(Color.LIGHT_GRAY);
            btnBloq.setBorderPainted(false);
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

        // Label do ícone - BRANCO e GRANDE
        JLabel lblIcone = new JLabel(icone);
        lblIcone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        lblIcone.setForeground(Color.WHITE); // GARANTE QUE SEJA BRANCO
        lblIcone.setAlignmentX(Component.CENTER_ALIGNMENT);

        conteudo.add(lblIcone);
        conteudo.add(Box.createVerticalStrut(15));

        // Label do texto - BRANCO e SEM QUEBRA
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTexto.setForeground(Color.WHITE); // GARANTE QUE SEJA BRANCO
        lblTexto.setAlignmentX(Component.CENTER_ALIGNMENT);

        conteudo.add(lblTexto);

        // Adiciona espaço flexível depois (empurra conteúdo para o centro)
        conteudo.add(Box.createVerticalGlue());

        btn.add(conteudo, BorderLayout.CENTER);

        return btn;
    }

    // --- MÉTODOS DE AÇÃO ---
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
        if(usuarioLogado.getTipo() == TipoUsuario.FUNCIONARIO) return;

        // ADICIONEI A OPÇÃO 7 AQUI
        String[] opcoes = {
                "1. Despesas por Veículo",
                "2. Total Mensal da Frota",
                "3. Gastos com Combustível (Mês)",
                "4. Somatório IPVA (Ano)",
                "5. Veículos Inativos",
                "6. Multas por Veículo (Ano)",
                "7. Análise Matricial de Custos" // Novo item
        };

        String escolha = (String) JOptionPane.showInputDialog(this, "Selecione:", "Relatórios", JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha != null) {
            switch (escolha.charAt(0)) {
                case '1' -> relatorioDespesasPorVeiculo();
                case '2' -> relatorioTotalMensal();
                case '3' -> relatorioCombustivel();
                case '4' -> relatorioIpva();
                case '5' -> relatorioInativos();
                case '6' -> relatorioMultas();
                case '7' -> relatorioMatematico(); // Chama o novo fluxo
            }
        }
    }

    // --- LÓGICA DO RELATÓRIO MATEMÁTICO (NOVO FLUXO) ---
    private void relatorioMatematico() {
        try {
            // 1. Calcula tudo antes
            MatematicaService mathService = context.getBean(MatematicaService.class);
            var resultadoCompleto = mathService.gerarRelatorioCompleto();

            // 2. Pergunta qual matriz o usuário quer ver (Dropdown)
            String[] matrizes = {
                    "Matriz A - Qtd. Abastecimentos (Veículo x Mês)",
                    "Matriz B - Custo Médio (Mês x Marca)",
                    "Matriz C - Gasto Estimado (Veículo x Marca)"
            };

            String selecao = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecione a Matriz para visualizar:",
                    "Fundamentos Matemáticos",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    matrizes,
                    matrizes[2]); // Padrão: Matriz C (a principal)

            if (selecao == null) return; // Cancelou

            // 3. Seleciona os dados com base na escolha
            MatematicaService.DadosMatriz dadosEscolhidos = null;
            String totalGeral = null;

            if (selecao.startsWith("Matriz A")) {
                dadosEscolhidos = resultadoCompleto.matrizA;
            } else if (selecao.startsWith("Matriz B")) {
                dadosEscolhidos = resultadoCompleto.matrizB;
            } else {
                dadosEscolhidos = resultadoCompleto.matrizC;
                totalGeral = resultadoCompleto.matrizC.resumo;
            }

            // 4. Formata os dados para exibir na tabela (converter double para String R$ ou Int)
            // Precisamos converter Object[][] que veio do service para String[][] formatado
            int linhas = dadosEscolhidos.dados.length;
            int colunas = dadosEscolhidos.colunas.length;
            Object[][] dadosTabela = new Object[linhas][colunas];

            for (int i = 0; i < linhas; i++) {
                dadosTabela[i][0] = dadosEscolhidos.dados[i][0]; // Nome da linha (ex: Placa ou Mês)
                for (int j = 1; j < colunas; j++) {
                    Object valor = dadosEscolhidos.dados[i][j];
                    if (valor instanceof Double) {
                        if (selecao.startsWith("Matriz A")) {
                            dadosTabela[i][j] = String.valueOf(((Double) valor).intValue()); // Qtd sem decimal
                        } else {
                            dadosTabela[i][j] = String.format("R$ %.2f", (Double) valor); // Dinheiro
                        }
                    } else {
                        dadosTabela[i][j] = valor;
                    }
                }
            }

            // 5. Abre a tela genérica (Reuso de código!)
            new TelaResultadoRelatorio(
                    dadosEscolhidos.titulo,
                    dadosEscolhidos.colunas,
                    dadosTabela,
                    totalGeral,
                    java.util.List.of() // Lista vazia (sem exportação bruta para matrizes)
            ).setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro no cálculo: " + e.getMessage());
        }
    }

    // --- OUTROS RELATÓRIOS (Mantidos) ---

    private void relatorioDespesasPorVeiculo() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Digite a PLACA:");
            if(placa == null || placa.isEmpty()) return;

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> {
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch (Exception e) { return false; }
                    }).toList();

            exibirRelatorioSimples("Extrato " + placa.toUpperCase(), lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void relatorioTotalMensal() {
        try {
            String mesAno = JOptionPane.showInputDialog(this, "Digite o Mês/Ano (ex: 11/2025):");
            if(mesAno == null) return;
            int mes = Integer.parseInt(mesAno.split("/")[0]);
            int ano = Integer.parseInt(mesAno.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes && m.getData().getYear() == ano)
                    .toList();
            exibirRelatorioSimples("Despesas " + mesAno.replace("/", "-"), lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida!"); }
    }

    private void relatorioCombustivel() {
        try {
            String mesAno = JOptionPane.showInputDialog(this, "Digite o Mês/Ano (ex: 11/2025):");
            if(mesAno == null) return;
            int mes = Integer.parseInt(mesAno.split("/")[0]);
            int ano = Integer.parseInt(mesAno.split("/")[1]);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getMonthValue() == mes && m.getData().getYear() == ano && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.COMBUSTIVEL)
                    .toList();
            exibirRelatorioSimples("Combustivel " + mesAno.replace("/", "-"), lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Data inválida!"); }
    }

    private void relatorioIpva() {
        try {
            String anoStr = JOptionPane.showInputDialog(this, "Digite o Ano (ex: 2025):");
            if(anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.IPVA)
                    .toList();
            exibirRelatorioSimples("IPVA " + ano, lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Ano inválido!"); }
    }

    private void relatorioMultas() {
        try {
            String placa = JOptionPane.showInputDialog(this, "Digite a PLACA:");
            String anoStr = JOptionPane.showInputDialog(this, "Digite o Ano:");
            if(placa == null || anoStr == null) return;
            int ano = Integer.parseInt(anoStr);

            var lista = context.getBean(MovimentacaoService.class).listar().stream()
                    .filter(m -> m.getData().getYear() == ano && m.getTipoDespesa() == br.com.gynlog.enums.TipoDespesaEnum.MULTA)
                    .filter(m -> {
                        try {
                            var v = context.getBean(VeiculoService.class).buscarPorId(m.getIdVeiculo());
                            return v != null && v.getPlaca().equalsIgnoreCase(placa);
                        } catch(Exception e) { return false; }
                    }).toList();
            exibirRelatorioSimples("Multas " + placa + " " + ano, lista);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Dados inválidos!"); }
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
            dados[i][3] = String.format("R$ %.2f", m.getValor());
            total += m.getValor();
        }
        new TelaResultadoRelatorio(titulo, colunas, dados, String.format("Total: R$ %.2f", total), lista).setVisible(true);
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
            new TelaResultadoRelatorio("Veiculos Inativos", colunas, dados, "Qtd: " + inativos.size(), inativos).setVisible(true);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }

    private void sair() {
        dispose();
        new br.com.gynlog.view.TelaLogin(context).setVisible(true);
    }
}