package br.com.gynlog.view;

import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.VeiculoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCadastroVeiculo extends JFrame {

    private JTextField txtId, txtPlaca, txtMarca, txtModelo, txtAno;
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final VeiculoService service;

    // Adição de Cores
    private static final Color COR_HEADER = new Color(52, 152, 219); // Azul Veículo
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
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_BG);
    }

    private void criarComponentes() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(COR_HEADER);
        JLabel lblTitulo = new JLabel("🚗 Cadastro de Veículos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Formulario
        JPanel painelCentral = new JPanel(new BorderLayout(15, 15));
        painelCentral.setBackground(COR_BG);
        painelCentral.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        // Estilizando Campos
        txtId = criarTextField(false);
        txtPlaca = criarTextField(true);
        txtMarca = criarTextField(true);
        txtModelo = criarTextField(true);
        txtAno = criarTextField(true);
        chkAtivo = new JCheckBox("Veículo Ativo");
        chkAtivo.setBackground(Color.WHITE);
        chkAtivo.setSelected(true);
        chkAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        adicionarCampo(form, "ID:", txtId);
        adicionarCampo(form, "Placa:", txtPlaca);
        adicionarCampo(form, "Marca:", txtMarca);
        adicionarCampo(form, "Modelo:", txtModelo);
        adicionarCampo(form, "Ano:", txtAno);
        form.add(chkAtivo); // Checkbox ocupa o espaço do label+campo

        painelCentral.add(form, BorderLayout.NORTH);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Placa", "Marca", "Modelo", "Ano", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(25);
        tabela.getTableHeader().setBackground(new Color(44, 62, 80)); // Cabeçalho escuro
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

        // Botões
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        botoes.setBackground(Color.WHITE);
        botoes.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)));

        botoes.add(criarBotao("Limpar", COR_BTN_LIMPAR, e -> limparFormulario()));
        botoes.add(criarBotao("Excluir", COR_BTN_EXCLUIR, e -> excluir()));
        botoes.add(criarBotao("Salvar Veículo", COR_BTN_SALVAR, e -> salvar()));

        add(botoes, BorderLayout.SOUTH);
    }

    // Métodos Auxiliares de Estilo
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
                modeloTabela.addRow(new Object[]{v.getIdVeiculo(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnoFabricacao(), v.isAtivo() ? "ATIVO" : "INATIVO"});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if(row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());
            txtPlaca.setText(tabela.getValueAt(row, 1).toString());
            txtMarca.setText(tabela.getValueAt(row, 2).toString());
            txtModelo.setText(tabela.getValueAt(row, 3).toString());
            txtAno.setText(tabela.getValueAt(row, 4).toString());
            chkAtivo.setSelected(tabela.getValueAt(row, 5).toString().equals("ATIVO"));
        }
    }

    private void limparFormulario() {
        txtId.setText(""); txtPlaca.setText(""); txtMarca.setText(""); txtModelo.setText(""); txtAno.setText("");
        chkAtivo.setSelected(true);
    }
}