package br.com.gynlog.view;

import br.com.gynlog.enums.TipoVeiculo;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.VeiculoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaCadastroVeiculo extends JFrame {

    private JTextField txtId, txtPlaca, txtMarca, txtModelo, txtAno;
    private JComboBox<TipoVeiculo> cbTipo;
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final VeiculoService service;

    public TelaCadastroVeiculo(VeiculoService service) {
        this.service = service;
        setTitle("Cadastro de Veículos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Painel Superior (Formulário)
        JPanel painelForm = new JPanel(new GridLayout(4, 4, 5, 5));

        txtId = new JTextField(); txtId.setEditable(false);
        txtPlaca = new JTextField();
        txtMarca = new JTextField();
        txtModelo = new JTextField();
        txtAno = new JTextField();
        cbTipo = new JComboBox<>(TipoVeiculo.values());
        chkAtivo = new JCheckBox("Ativo", true);

        painelForm.add(new JLabel("ID:")); painelForm.add(txtId);
        painelForm.add(new JLabel("Tipo:")); painelForm.add(cbTipo);
        painelForm.add(new JLabel("Placa:")); painelForm.add(txtPlaca);
        painelForm.add(new JLabel("Marca:")); painelForm.add(txtMarca);
        painelForm.add(new JLabel("Modelo:")); painelForm.add(txtModelo);
        painelForm.add(new JLabel("Ano:")); painelForm.add(txtAno);
        painelForm.add(new JLabel("Status:")); painelForm.add(chkAtivo);

        add(painelForm, BorderLayout.NORTH);

        // Tabela Simples
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Tipo", "Placa", "Marca", "Modelo", "Ano", "Status"}, 0);
        tabela = new JTable(modeloTabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Evento clique
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { preencherFormulario(); }
        });

        // Botões Simples
        JPanel painelBotoes = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        add(painelBotoes, BorderLayout.SOUTH);

        carregarDados();
    }

    // --- MÉTODOS DE LÓGICA (MANTIDOS IGUAIS) ---
    // Copie os métodos salvar(), excluir(), carregarDados(), preencherFormulario(), limparFormulario()
    // do código anterior. A lógica não muda nada, só o visual acima.

    private void salvar() {
        try {
            Veiculo v = new Veiculo();
            v.setIdVeiculo(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            v.setPlaca(txtPlaca.getText());
            v.setMarca(txtMarca.getText());
            v.setModelo(txtModelo.getText());
            v.setAnoFabricacao(Integer.parseInt(txtAno.getText()));
            v.setAtivo(chkAtivo.isSelected());
            v.setTipo((TipoVeiculo) cbTipo.getSelectedItem());

            if(v.getIdVeiculo() == 0) service.salvar(v);
            else service.atualizar(v);

            JOptionPane.showMessageDialog(this, "Salvo!");
            limparFormulario();
            carregarDados();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void excluir() {
        try {
            if(!txtId.getText().isEmpty()) {
                service.excluir(Integer.parseInt(txtId.getText()));
                limparFormulario();
                carregarDados();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void carregarDados() {
        modeloTabela.setRowCount(0);
        try {
            List<Veiculo> lista = service.listar();
            for (Veiculo v : lista) {
                modeloTabela.addRow(new Object[]{v.getIdVeiculo(), v.getTipo(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnoFabricacao(), v.isAtivo() ? "ATIVO" : "INATIVO"});
            }
        } catch (Exception e) {}
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if(row != -1) {
            txtId.setText(tabela.getValueAt(row, 0).toString());
            cbTipo.setSelectedItem(tabela.getValueAt(row, 1));
            txtPlaca.setText(tabela.getValueAt(row, 2).toString());
            txtMarca.setText(tabela.getValueAt(row, 3).toString());
            txtModelo.setText(tabela.getValueAt(row, 4).toString());
            txtAno.setText(tabela.getValueAt(row, 5).toString());
            chkAtivo.setSelected(tabela.getValueAt(row, 6).toString().equals("ATIVO"));
        }
    }

    private void limparFormulario() {
        txtId.setText(""); txtPlaca.setText(""); txtMarca.setText(""); txtModelo.setText(""); txtAno.setText("");
        cbTipo.setSelectedIndex(0); chkAtivo.setSelected(true);
    }
}