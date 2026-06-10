package br.com.gynlog.view;

import br.com.gynlog.model.Despesa;
import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.DespesaService;
import br.com.gynlog.service.TipoDespesaService;
import br.com.gynlog.service.VeiculoService;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PainelDespesas extends JPanel {

    private final DespesaService service;
    private final VeiculoService veiculoService;
    private final TipoDespesaService tipoService;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JComboBox<Veiculo> cbVeiculo;
    private JComboBox<TipoDespesa> cbTipo;
    private JTextField txtDescricao, txtData, txtValor;
    private JLabel lblIdSelecionado, lblAviso;
    private JButton btnEditar, btnExcluir;

    public PainelDespesas(ConfigurableApplicationContext context) {
        this.service        = context.getBean(DespesaService.class);
        this.veiculoService = context.getBean(VeiculoService.class);
        this.tipoService    = context.getBean(TipoDespesaService.class);
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

        JLabel lblTitulo = new JLabel("Lançamento de Despesas");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoes.setBackground(Tema.CONTEUDO_BG);
        JButton btnSalvar  = Tema.botaoPrimario("+ Lançar");
        btnEditar = Tema.botaoSecundario("Salvar edição");
        btnEditar.setEnabled(false);
        btnExcluir = Tema.botaoPerigo("Excluir");
        JButton btnLimpar  = Tema.botaoSecundario("Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnEditar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        topo.add(lblTitulo, BorderLayout.NORTH);
        topo.add(painelBotoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Veículo", "Tipo", "Descrição", "Data", "Valor", "Origem"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        Tema.estilizarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(45);
        tabela.getColumnModel().getColumn(6).setMaxWidth(80);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                boolean automatica = "Auto".equals(modeloTabela.getValueAt(row, 6));
                if (!isSelected) {
                    setBackground(automatica ? new Color(245, 245, 248) : (row % 2 == 0 ? Tema.TABELA_LINHA_PAR : Tema.TABELA_LINHA_IMPAR));
                    setForeground(automatica ? new Color(160, 163, 175) : Tema.TEXTO_TITULO);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

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

        lblIdSelecionado = new JLabel("Nova despesa");
        lblIdSelecionado.setFont(Tema.FONTE_SUBTITULO);
        lblIdSelecionado.setForeground(Tema.TEXTO_TITULO);
        lblIdSelecionado.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbVeiculo    = new JComboBox<>(); cbVeiculo.setFont(Tema.FONTE_CAMPO);
        cbTipo       = new JComboBox<>(); cbTipo.setFont(Tema.FONTE_CAMPO);
        txtDescricao = Tema.campo();
        txtData      = Tema.campo(); txtData.setText(LocalDate.now().format(FMT));
        txtValor     = Tema.campo();

        txtDescricao.setDocument(new LimiteCaracteres(100));

        form.add(lblIdSelecionado);
        form.add(Box.createVerticalStrut(16));
        form.add(campoCombo("Veículo *", cbVeiculo));
        form.add(Box.createVerticalStrut(10));
        form.add(campoCombo("Tipo de Despesa *", cbTipo));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Descrição *", txtDescricao));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Data (dd/MM/aaaa) *", txtData));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Valor (R$) *", txtValor));

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
            for (Veiculo v : veiculoService.listar()) cbVeiculo.addItem(v);
            cbTipo.removeAllItems();
            for (TipoDespesa t : tipoService.listar()) cbTipo.addItem(t);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar combos: " + e.getMessage());
        }
    }

    private void salvar() {
        try {
            Despesa d = montarDespesa(0);
            service.salvar(d);
            JOptionPane.showMessageDialog(this, "Despesa lançada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizar() {
        String idTxt = lblIdSelecionado.getClientProperty("id") != null
                ? lblIdSelecionado.getClientProperty("id").toString() : "";
        if (idTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Despesa d = montarDespesa(Integer.parseInt(idTxt));
            service.atualizar(d);
            JOptionPane.showMessageDialog(this, "Despesa atualizada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        String idTxt = lblIdSelecionado.getClientProperty("id") != null
                ? lblIdSelecionado.getClientProperty("id").toString() : "";
        if (idTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione uma despesa na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Confirma a exclusão?",
                "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            service.excluir(Integer.parseInt(idTxt));
            limparFormulario();
            carregarTabela();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Despesa montarDespesa(int id) throws Exception {
        Veiculo vSel = (Veiculo) cbVeiculo.getSelectedItem();
        TipoDespesa tSel = (TipoDespesa) cbTipo.getSelectedItem();
        if (vSel == null || tSel == null) throw new IllegalArgumentException("Selecione veículo e tipo.");
        LocalDate data = LocalDate.parse(txtData.getText().trim(), FMT);
        double valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
        return new Despesa(id, vSel.getIdVeiculo(), tSel, txtDescricao.getText().trim(), data, valor, false);
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            for (Despesa d : service.listar()) {
                String veiculo = "–";
                try { veiculo = veiculoService.buscarPorId(d.getIdVeiculo()).toString(); } catch (Exception ignored) {}
                modeloTabela.addRow(new Object[]{
                        d.getIdDespesa(), veiculo,
                        d.getTipoDespesa(), d.getDescricao(),
                        d.getData().format(FMT),
                        String.format("R$ %.2f", d.getValor()),
                        d.isGeradaPorAbastecimento() ? "Auto" : "Manual"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (int) modeloTabela.getValueAt(row, 0);
        try {
            Despesa d = service.buscarPorId(id);
            if (d == null) return;
            lblIdSelecionado.setText("Editando ID: " + d.getIdDespesa());
            lblIdSelecionado.putClientProperty("id", String.valueOf(d.getIdDespesa()));
            for (int i = 0; i < cbVeiculo.getItemCount(); i++) {
                if (cbVeiculo.getItemAt(i).getIdVeiculo() == d.getIdVeiculo()) { cbVeiculo.setSelectedIndex(i); break; }
            }
            for (int i = 0; i < cbTipo.getItemCount(); i++) {
                if (cbTipo.getItemAt(i).getIdTipoDespesa() == d.getTipoDespesa().getIdTipoDespesa()) { cbTipo.setSelectedIndex(i); break; }
            }
            txtDescricao.setText(d.getDescricao());
            txtData.setText(d.getData().format(FMT));
            txtValor.setText(String.format("%.2f", d.getValor()));
            if (d.isGeradaPorAbastecimento()) {
                btnEditar.setEnabled(false);
                btnExcluir.setEnabled(false);
            } else {
                btnEditar.setEnabled(true);
                btnExcluir.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        lblIdSelecionado.setText("Nova despesa");
        lblIdSelecionado.putClientProperty("id", "");
        txtDescricao.setText(""); txtData.setText(LocalDate.now().format(FMT)); txtValor.setText("");
        if (cbVeiculo.getItemCount() > 0) cbVeiculo.setSelectedIndex(0);
        if (cbTipo.getItemCount() > 0) cbTipo.setSelectedIndex(0);
        tabela.clearSelection();
        btnEditar.setEnabled(false);
    }

    static class LimiteCaracteres extends javax.swing.text.PlainDocument {
        private final int limite;
        LimiteCaracteres(int limite) { this.limite = limite; }
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limite) super.insertString(offs, str, a);
        }
    }
}