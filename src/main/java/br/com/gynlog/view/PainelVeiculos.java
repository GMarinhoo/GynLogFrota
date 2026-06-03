package br.com.gynlog.view;

import br.com.gynlog.enums.CategoriaVeiculo;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.VeiculoService;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelVeiculos extends JPanel {

    private final VeiculoService service;

    private DefaultTableModel modeloTabela;
    private JTable tabela;

    private JTextField txtPlaca, txtMarca, txtModelo, txtAno;
    private JComboBox<CategoriaVeiculo> cbCategoria;
    private JCheckBox chkAtivo;
    private JLabel lblIdSelecionado;

    public PainelVeiculos(ConfigurableApplicationContext context) {
        this.service = context.getBean(VeiculoService.class);
        setBackground(Tema.CONTEUDO_BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        construir();
        carregarTabela();
    }

    private void construir() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Tema.CONTEUDO_BG);
        topo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        JLabel lblTitulo = new JLabel("Gerenciamento de Veículos");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoes.setBackground(Tema.CONTEUDO_BG);
        JButton btnNovo   = Tema.botaoPrimario("+ Adicionar");
        JButton btnSalvar = Tema.botaoSecundario("Salvar");
        JButton btnExcluir= Tema.botaoPerigo("Excluir");
        JButton btnLimpar = Tema.botaoSecundario("Limpar");

        btnNovo.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        topo.add(lblTitulo, BorderLayout.NORTH);
        topo.add(painelBotoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        String[] colunas = {"ID", "Placa", "Categoria", "Marca", "Modelo", "Ano", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        Tema.estilizarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(5).setMaxWidth(60);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 222, 228), 1));
        scroll.getViewport().setBackground(Tema.PAINEL_BG);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBusca.setBackground(Tema.CONTEUDO_BG);
        painelBusca.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        JTextField txtBusca = Tema.campo();
        txtBusca.setPreferredSize(new Dimension(200, 32));
        JButton btnBuscar = Tema.botaoSecundario("Buscar");
        JButton btnLimparBusca = Tema.botaoSecundario("Limpar Busca");

        btnBuscar.addActionListener(e -> realizarBusca(txtBusca.getText()));
        btnLimparBusca.addActionListener(e -> { txtBusca.setText(""); carregarTabela(); });
        txtBusca.addActionListener(e -> realizarBusca(txtBusca.getText()));

        btnBuscar.addActionListener(e -> realizarBusca(txtBusca.getText()));
        btnLimparBusca.addActionListener(e -> { txtBusca.setText(""); carregarTabela(); });

        painelBusca.add(Tema.label("Pesquisar (Placa ou Modelo):"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimparBusca);

        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(Tema.CONTEUDO_BG);
        painelCentral.add(painelBusca, BorderLayout.NORTH);
        painelCentral.add(scroll, BorderLayout.CENTER);

        JPanel form = criarFormulario();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelCentral, form);
        split.setDividerLocation(580);
        split.setBorder(null);
        split.setDividerSize(6);
        split.setBackground(Tema.CONTEUDO_BG);

        add(split, BorderLayout.CENTER);
    }

    private JPanel criarFormulario() {
        JPanel form = new JPanel();
        form.setBackground(Tema.PAINEL_BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(Tema.bordaPainel());

        lblIdSelecionado = new JLabel("Novo veículo");
        lblIdSelecionado.setFont(Tema.FONTE_SUBTITULO);
        lblIdSelecionado.setForeground(Tema.TEXTO_TITULO);
        lblIdSelecionado.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPlaca   = Tema.campo();
        txtMarca   = Tema.campo();
        txtModelo  = Tema.campo();
        txtAno     = Tema.campo();
        cbCategoria = new JComboBox<>(CategoriaVeiculo.values());
        cbCategoria.setFont(Tema.FONTE_CAMPO);
        chkAtivo   = new JCheckBox("Ativo na frota", true);
        chkAtivo.setBackground(Tema.PAINEL_BG);
        chkAtivo.setFont(Tema.FONTE_LABEL);

        txtPlaca.setDocument(new LimiteCaracteres(8));
        txtMarca.setDocument(new LimiteCaracteres(50));
        txtModelo.setDocument(new LimiteCaracteres(50));
        txtAno.setDocument(new LimiteCaracteres(4));

        form.add(lblIdSelecionado);
        form.add(Box.createVerticalStrut(16));
        form.add(campo("Placa *", txtPlaca));
        form.add(Box.createVerticalStrut(10));
        form.add(campoCombo("Categoria *", cbCategoria));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Marca *", txtMarca));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Modelo *", txtModelo));
        form.add(Box.createVerticalStrut(10));
        form.add(campo("Ano de Fabricação *", txtAno));
        form.add(Box.createVerticalStrut(10));
        form.add(chkAtivo);

        return form;
    }

    private JPanel campo(String rotulo, JTextField tf) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Tema.PAINEL_BG);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = Tema.label(rotulo);
        p.add(lbl, BorderLayout.NORTH);
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

    private void salvar() {
        try {
            Veiculo v = new Veiculo();
            String idTxt = lblIdSelecionado.getClientProperty("id") != null
                    ? lblIdSelecionado.getClientProperty("id").toString() : "";
            v.setIdVeiculo(idTxt.isEmpty() ? 0 : Integer.parseInt(idTxt));
            v.setPlaca(txtPlaca.getText().trim().toUpperCase());
            v.setCategoria((CategoriaVeiculo) cbCategoria.getSelectedItem());
            v.setMarca(txtMarca.getText().trim());
            v.setModelo(txtModelo.getText().trim());
            v.setAnoFabricacao(Integer.parseInt(txtAno.getText().trim()));
            v.setAtivo(chkAtivo.isSelected());

            if (v.getIdVeiculo() == 0) service.salvar(v);
            else                       service.atualizar(v);

            JOptionPane.showMessageDialog(this, "Veículo salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            carregarTabela();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ano de fabricação inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        String idTxt = lblIdSelecionado.getClientProperty("id") != null
                ? lblIdSelecionado.getClientProperty("id").toString() : "";
        if (idTxt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Confirma a exclusão do veículo selecionado?", "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
            List<Veiculo> lista = service.listar();
            for (Veiculo v : lista) {
                modeloTabela.addRow(new Object[]{
                        v.getIdVeiculo(),
                        v.getPlaca(),
                        v.getCategoria(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getAnoFabricacao(),
                        v.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (int) modeloTabela.getValueAt(row, 0);
        try {
            Veiculo v = service.buscarPorId(id);
            if (v == null) return;
            lblIdSelecionado.setText("Editando ID: " + v.getIdVeiculo());
            lblIdSelecionado.putClientProperty("id", String.valueOf(v.getIdVeiculo()));
            txtPlaca.setText(v.getPlaca());
            cbCategoria.setSelectedItem(v.getCategoria());
            txtMarca.setText(v.getMarca());
            txtModelo.setText(v.getModelo());
            txtAno.setText(String.valueOf(v.getAnoFabricacao()));
            chkAtivo.setSelected(v.isAtivo());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        lblIdSelecionado.setText("Novo veículo");
        lblIdSelecionado.putClientProperty("id", "");
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtAno.setText("");
        cbCategoria.setSelectedIndex(0);
        chkAtivo.setSelected(true);
        tabela.clearSelection();
    }

    private static class LimiteCaracteres extends javax.swing.text.PlainDocument {
        private final int limite;
        LimiteCaracteres(int limite) { this.limite = limite; }
        @Override
        public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if (str == null) return;
            if ((getLength() + str.length()) <= limite) super.insertString(offs, str, a);
        }
    }

    private void realizarBusca(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            carregarTabela();
            return;
        }
        modeloTabela.setRowCount(0);
        try {
            List<Veiculo> lista = service.buscaSequencial(termo);
            for (Veiculo v : lista) {
                modeloTabela.addRow(new Object[]{
                        v.getIdVeiculo(), v.getPlaca(), v.getCategoria(),
                        v.getMarca(), v.getModelo(), v.getAnoFabricacao(),
                        v.isAtivo() ? "Ativo" : "Inativo"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}