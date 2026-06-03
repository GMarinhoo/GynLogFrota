package br.com.gynlog.view;

import br.com.gynlog.model.Abastecimento;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.AbastecimentoService;
import br.com.gynlog.service.VeiculoService;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PainelAbastecimentos extends JPanel {

    private final AbastecimentoService service;
    private final VeiculoService veiculoService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JComboBox<Veiculo> cbVeiculo;
    private JTextField txtData, txtOdometro, txtLitros, txtValor;
    private JLabel lblIdSelecionado;

    public PainelAbastecimentos(ConfigurableApplicationContext context) {
        this.service = context.getBean(AbastecimentoService.class);
        this.veiculoService = context.getBean(VeiculoService.class);
        setBackground(Tema.CONTEUDO_BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        construir();
        carregarCombos();
        carregarTabela();
    }

    private void construir() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CONTEUDO_BG);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel lblTitulo = new JLabel("Registro de Abastecimentos");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoes.setBackground(Tema.CONTEUDO_BG);
        JButton btnSalvar  = Tema.botaoPrimario("+ Registrar");
        JButton btnExcluir = Tema.botaoPerigo("Excluir");
        JButton btnLimpar  = Tema.botaoSecundario("Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        topo.add(lblTitulo, BorderLayout.NORTH);
        topo.add(painelBotoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Veículo", "Data", "Hodômetro (km)", "Litros", "Valor Total", "km Rodados", "km/L"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        Tema.estilizarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 222, 228), 1));
        scroll.getViewport().setBackground(Tema.PAINEL_BG);

        JPanel form = criarFormulario();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, form);
        split.setDividerLocation(580);
        split.setBorder(null);
        split.setDividerSize(6);

        add(split, BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel();
        form.setBackground(Tema.PAINEL_BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(Tema.bordaPainel());

        lblIdSelecionado = new JLabel("Novo abastecimento");
        lblIdSelecionado.setFont(Tema.FONTE_SUBTITULO);
        lblIdSelecionado.setForeground(Tema.TEXTO_TITULO);
        lblIdSelecionado.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbVeiculo   = new JComboBox<>();
        cbVeiculo.setFont(Tema.FONTE_CAMPO);
        txtData     = Tema.campo(); txtData.setText(LocalDate.now().format(FMT));
        txtOdometro = Tema.campo();
        txtLitros   = Tema.campo();
        txtValor    = Tema.campo();

        form.add(lblIdSelecionado);
        form.add(Box.createVerticalStrut(16));
        form.add(campoCombo("Veículo *", cbVeiculo));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Data (dd/MM/aaaa) *", txtData));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Hodômetro atual (km) *", txtOdometro));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Quantidade de litros *", txtLitros));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Valor total (R$) *", txtValor));

        return form;
    }

    private JPanel campo(String rotulo, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Tema.PAINEL_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Tema.label(rotulo), BorderLayout.NORTH);
        p.add(tf, BorderLayout.CENTER);
        return p;
    }

    private JPanel campoCombo(String rotulo, JComboBox<?> cb) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Tema.PAINEL_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Tema.label(rotulo), BorderLayout.NORTH);
        p.add(cb, BorderLayout.CENTER);
        return p;
    }

    private void carregarCombos() {
        try {
            cbVeiculo.removeAllItems();
            for (Veiculo v : veiculoService.listar()) {
                cbVeiculo.addItem(v);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Veiculo vSel = (Veiculo) cbVeiculo.getSelectedItem();
            if (vSel == null) {
                JOptionPane.showMessageDialog(this, "Selecione um veículo.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            LocalDate data = LocalDate.parse(txtData.getText().trim(), FMT);
            double odometro = Double.parseDouble(txtOdometro.getText().trim().replace(",", "."));
            double litros   = Double.parseDouble(txtLitros.getText().trim().replace(",", "."));
            double valor    = Double.parseDouble(txtValor.getText().trim().replace(",", "."));

            Abastecimento a = new Abastecimento(0, vSel.getIdVeiculo(), data, odometro, litros, valor);
            service.salvar(a);

            JOptionPane.showMessageDialog(this,
                    "Abastecimento registrado!\nDespesa de combustível gerada automaticamente.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hodômetro, litros e valor devem ser números válidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        String idTxt = lblIdSelecionado.getClientProperty("id") != null
                ? lblIdSelecionado.getClientProperty("id").toString() : "";
        if (idTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um abastecimento na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão do abastecimento selecionado?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            service.excluir(Integer.parseInt(idTxt));
            limparFormulario();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            List<Veiculo> veiculos = veiculoService.listar();
            List<Abastecimento> listaComConsumo = new java.util.ArrayList<>();

            for (Veiculo v : veiculos) {
                listaComConsumo.addAll(service.listarPorVeiculoComConsumo(v.getIdVeiculo()));
            }
            listaComConsumo.sort((a1, a2) -> a2.getData().compareTo(a1.getData()));

            for (Abastecimento a : listaComConsumo) {
                String veiculo = "–";
                try { veiculo = veiculoService.buscarPorId(a.getIdVeiculo()).toString(); } catch (Exception ignored) {}

                String kmRodados = a.getKmRodados() > 0 ? String.format("%.0f km", a.getKmRodados()) : "–";
                String kmL = a.getKmPorLitro() > 0 ? String.format("%.2f", a.getKmPorLitro()) : "–";

                modeloTabela.addRow(new Object[]{
                        a.getIdAbastecimento(),
                        veiculo,
                        a.getData().format(FMT),
                        String.format("%.0f", a.getOdometro()),
                        String.format("%.3f", a.getQtdLitros()),
                        String.format("R$ %.2f", a.getValorTotal()),
                        kmRodados,
                        kmL
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar abastecimentos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (int) modeloTabela.getValueAt(row, 0);
        try {
            Abastecimento a = service.buscarPorId(id);
            if (a == null) return;
            lblIdSelecionado.setText("ID: " + a.getIdAbastecimento());
            lblIdSelecionado.putClientProperty("id", String.valueOf(a.getIdAbastecimento()));
            for (int i = 0; i < cbVeiculo.getItemCount(); i++) {
                if (cbVeiculo.getItemAt(i).getIdVeiculo() == a.getIdVeiculo()) {
                    cbVeiculo.setSelectedIndex(i); break;
                }
            }
            txtData.setText(a.getData().format(FMT));
            txtOdometro.setText(String.format("%.0f", a.getOdometro()));
            txtLitros.setText(String.format("%.3f", a.getQtdLitros()));
            txtValor.setText(String.format("%.2f", a.getValorTotal()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        lblIdSelecionado.setText("Novo abastecimento");
        lblIdSelecionado.putClientProperty("id", "");
        txtData.setText(LocalDate.now().format(FMT));
        txtOdometro.setText(""); txtLitros.setText(""); txtValor.setText("");
        if (cbVeiculo.getItemCount() > 0) cbVeiculo.setSelectedIndex(0);
        tabela.clearSelection();
    }
}