package br.com.gynlog.view;

import br.com.gynlog.enums.TipoVeiculo; // Import Novo
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.VeiculoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCadastroVeiculo extends JFrame {

    private JTextField txtId, txtPlaca, txtMarca, txtModelo, txtAno;
    private JComboBox<TipoVeiculo> cbTipo; // O NOVO BOTÃO DE OPÇÕES
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final VeiculoService service;

    // Cores Modernas
    private static final Color COR_HEADER = new Color(52, 152, 219);
    private static final Color COR_BG = new Color(236, 240, 241);
    private static final Color COR_BTN_SALVAR = new Color(46, 204, 113);
    private static final Color COR_BTN_EXCLUIR = new Color(231, 76, 60);
    private static final Color COR_BTN_LIMPAR = new Color(149, 165, 166);

    public TelaCadastroVeiculo(VeiculoService service) {
        this.service = service;
        configurarJanela();
        criarComponentes();
        carregarDados();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Veículos");
        setSize(1000, 700); // Aumentei um pouco a altura
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_BG);
    }

    private void criarComponentes() {
        // HEADER
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(COR_HEADER);
        JLabel lblTitulo = new JLabel("🚗 Cadastro de Veículos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // FORMULÁRIO
        JPanel painelCentral = new JPanel(new BorderLayout(15, 15));
        painelCentral.setBackground(COR_BG);
        painelCentral.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Ajustei o Grid para caber o novo campo (4 linhas)
        JPanel form = new JPanel(new GridLayout(4, 4, 15, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        txtId = criarTextField(false);
        txtPlaca = criarTextField(true);
        cbTipo = new JComboBox<>(TipoVeiculo.values()); // Popula com CARRO, MOTO...
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTipo.setBackground(Color.WHITE);

        txtMarca = criarTextField(true);
        txtModelo = criarTextField(true);
        txtAno = criarTextField(true);

        chkAtivo = new JCheckBox("Veículo Ativo");
        chkAtivo.setBackground(Color.WHITE);
        chkAtivo.setSelected(true);
        chkAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        adicionarCampo(form, "ID:", txtId);
        adicionarCampo(form, "Tipo:", cbTipo); // Adicionado aqui
        adicionarCampo(form, "Placa:", txtPlaca);
        adicionarCampo(form, "Marca:", txtMarca);
        adicionarCampo(form, "Modelo:", txtModelo);
        adicionarCampo(form, "Ano:", txtAno);

        form.add(new JLabel("")); // Espaço vazio para alinhar
        form.add(chkAtivo);

        painelCentral.add(form, BorderLayout.NORTH);

        // TABELA (Adicionei a coluna Tipo)
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Tipo", "Placa", "Marca", "Modelo", "Ano", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setBackground(new Color(44, 62, 80));
        tabela.getTableHeader().setForeground(Color.WHITE);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setSelectionBackground(new Color(52, 152, 219));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { preencherFormulario(); }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Color.WHITE);
        painelCentral.add(scroll, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);

        // BOTÕES
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        botoes.setBackground(Color.WHITE);
        botoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        botoes.add(criarBotao("Limpar", COR_BTN_LIMPAR, e -> limparFormulario()));
        botoes.add(criarBotao("Excluir", COR_BTN_EXCLUIR, e -> excluir()));
        botoes.add(criarBotao("Salvar Veículo", COR_BTN_SALVAR, e -> salvar()));

        add(botoes, BorderLayout.SOUTH);
    }

    private JTextField criarTextField(boolean editavel) {
        JTextField txt = new JTextField();
        txt.setEditable(editavel);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (!editavel) txt.setBackground(new Color(240, 240, 240));
        return txt;
    }

    private void adicionarCampo(JPanel p, String label, Component c) {
        JPanel item = new JPanel(new BorderLayout(0, 5));
        item.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(100, 100, 100));
        item.add(lbl, BorderLayout.NORTH);
        item.add(c, BorderLayout.CENTER);
        p.add(item);
    }

    private JButton criarBotao(String texto, Color cor, java.awt.event.ActionListener acao) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 40));
        btn.addActionListener(acao);
        return btn;
    }

    private void salvar() {
        try {
            Veiculo v = new Veiculo();
            v.setIdVeiculo(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            v.setPlaca(txtPlaca.getText().toUpperCase());
            v.setMarca(txtMarca.getText());
            v.setModelo(txtModelo.getText());
            v.setAnoFabricacao(Integer.parseInt(txtAno.getText()));
            v.setAtivo(chkAtivo.isSelected());

            // PEGA O TIPO SELECIONADO
            v.setTipo((TipoVeiculo) cbTipo.getSelectedItem());

            if(v.getIdVeiculo() == 0) service.salvar(v);
            else service.atualizar(v);

            JOptionPane.showMessageDialog(this, "Salvo com sucesso!");
            limparFormulario();
            carregarDados();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void excluir() {
        try {
            if(!txtId.getText().isEmpty()) {
                service.excluir(Integer.parseInt(txtId.getText()));
                JOptionPane.showMessageDialog(this, "Excluído!");
                limparFormulario();
                carregarDados();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void carregarDados() {
        try {
            modeloTabela.setRowCount(0);
            List<Veiculo> lista = service.listar();
            for (Veiculo v : lista) {
                modeloTabela.addRow(new Object[]{
                        v.getIdVeiculo(),
                        v.getTipo(), // Mostra na tabela
                        v.getPlaca(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getAnoFabricacao(),
                        v.isAtivo() ? "ATIVO" : "INATIVO"
                });
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if(row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());

            // Seleciona o tipo no combo
            Object tipoVal = tabela.getValueAt(row, 1);
            if(tipoVal instanceof TipoVeiculo) {
                cbTipo.setSelectedItem(tipoVal);
            }

            txtPlaca.setText(tabela.getValueAt(row, 2).toString());
            txtMarca.setText(tabela.getValueAt(row, 3).toString());
            txtModelo.setText(tabela.getValueAt(row, 4).toString());
            txtAno.setText(tabela.getValueAt(row, 5).toString());
            chkAtivo.setSelected(tabela.getValueAt(row, 6).toString().equals("ATIVO"));
        }
    }

    private void limparFormulario() {
        txtId.setText(""); txtPlaca.setText(""); txtMarca.setText(""); txtModelo.setText(""); txtAno.setText("");
        cbTipo.setSelectedIndex(0);
        chkAtivo.setSelected(true);
    }
}