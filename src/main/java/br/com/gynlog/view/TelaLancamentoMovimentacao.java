package br.com.gynlog.view;

import br.com.gynlog.enums.TipoDespesaEnum;
import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.MovimentacaoService;
import br.com.gynlog.service.VeiculoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaLancamentoMovimentacao extends JFrame {

    private JTextField txtId, txtDescricao, txtValor, txtData;
    private JComboBox<String> cbVeiculo;
    private JComboBox<TipoDespesaEnum> cbTipoDespesa;
    private final MovimentacaoService movService;
    private final VeiculoService veiculoService;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Cores
    private static final Color COR_HEADER = new Color(155, 89, 182); // Roxo Despesa
    private static final Color COR_BG = new Color(236, 240, 241);
    private static final Color COR_BTN_SALVAR = new Color(46, 204, 113);

    public TelaLancamentoMovimentacao(MovimentacaoService ms, VeiculoService vs) {
        this.movService = ms;
        this.veiculoService = vs;
        configurarJanela();
        criarComponentes();
        carregarCombos();
    }

    private void configurarJanela() {
        setTitle("Lançamento de Despesas");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COR_BG);
    }

    private void criarComponentes() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(COR_HEADER);
        JLabel lblTitulo = new JLabel("💰 Nova Despesa");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Formulario
        JPanel painelCentral = new JPanel(new BorderLayout());
        painelCentral.setBackground(COR_BG);
        painelCentral.setBorder(new EmptyBorder(20, 40, 20, 40));

        JPanel form = new JPanel(new GridLayout(6, 2, 20, 15)); // Grid ajustado
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        txtId = criarTextField(false);
        cbVeiculo = new JComboBox<>();
        cbTipoDespesa = new JComboBox<>(TipoDespesaEnum.values());
        txtValor = criarTextField(true);
        txtData = criarTextField(true);
        txtData.setText(LocalDate.now().format(fmt));
        txtDescricao = criarTextField(true);

        estilizarCombo(cbVeiculo);
        estilizarCombo(cbTipoDespesa);

        // Adicionando na Ordem
        adicionarLinha(form, "ID:", txtId);
        adicionarLinha(form, "Veículo:", cbVeiculo);
        adicionarLinha(form, "Tipo:", cbTipoDespesa);
        adicionarLinha(form, "Valor (R$):", txtValor);
        adicionarLinha(form, "Data (dd/mm/aaaa):", txtData);
        adicionarLinha(form, "Descrição:", txtDescricao);

        painelCentral.add(form, BorderLayout.CENTER);
        add(painelCentral, BorderLayout.CENTER);

        // Botão Salvar
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        rodape.setBackground(Color.WHITE);

        JButton btnSalvar = new JButton("Confirmar Lançamento");
        btnSalvar.setBackground(COR_BTN_SALVAR);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBorderPainted(false);
        btnSalvar.setPreferredSize(new Dimension(220, 45));
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> salvar());

        rodape.add(btnSalvar);
        add(rodape, BorderLayout.SOUTH);
    }

    private JTextField criarTextField(boolean editavel) {
        JTextField txt = new JTextField();
        txt.setEditable(editavel);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        if (!editavel) txt.setBackground(new Color(245, 245, 245));
        return txt;
    }

    private void estilizarCombo(JComboBox box) {
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        box.setBackground(Color.WHITE);
    }

    private void adicionarLinha(JPanel p, String label, Component c) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(100, 100, 100));
        p.add(lbl);
        p.add(c);
    }

    // Lógica
    private void carregarCombos() {
        try {
            cbVeiculo.removeAllItems();
            List<Veiculo> veiculos = veiculoService.listar();
            for(Veiculo v : veiculos) {
                // Removi o IF de ativo para permitir multas em inativos
                String status = v.isAtivo() ? "" : " (Inativo)";
                cbVeiculo.addItem(v.getIdVeiculo() + " - " + v.getPlaca() + status);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void salvar() {
        try {
            if(cbVeiculo.getSelectedItem() == null) return;
            int idVeiculo = Integer.parseInt(cbVeiculo.getSelectedItem().toString().split(" - ")[0]);

            Movimentacao m = new Movimentacao();
            m.setIdVeiculo(idVeiculo);
            m.setTipoDespesa((TipoDespesaEnum) cbTipoDespesa.getSelectedItem());
            m.setDescricao(txtDescricao.getText());
            m.setValor(Double.parseDouble(txtValor.getText().replace(",", ".")));
            m.setData(LocalDate.parse(txtData.getText(), fmt));

            movService.salvar(m);
            JOptionPane.showMessageDialog(this, "Despesa lançada com sucesso!");
            dispose();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}