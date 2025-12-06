package br.com.gynlog.view;

import br.com.gynlog.enums.TipoDespesaEnum;
import br.com.gynlog.model.Movimentacao;
import br.com.gynlog.model.Veiculo;
import br.com.gynlog.service.MovimentacaoService;
import br.com.gynlog.service.VeiculoService;

import javax.swing.*;
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

    public TelaLancamentoMovimentacao(MovimentacaoService ms, VeiculoService vs) {
        this.movService = ms;
        this.veiculoService = vs;
        setTitle("Lançamento de Despesas");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField(); txtId.setEditable(false);
        cbVeiculo = new JComboBox<>();
        cbTipoDespesa = new JComboBox<>(TipoDespesaEnum.values());
        txtValor = new JTextField();
        txtData = new JTextField(LocalDate.now().format(fmt));
        txtDescricao = new JTextField();

        form.add(new JLabel("ID:")); form.add(txtId);
        form.add(new JLabel("Veículo:")); form.add(cbVeiculo);
        form.add(new JLabel("Tipo:")); form.add(cbTipoDespesa);
        form.add(new JLabel("Valor (R$):")); form.add(txtValor);
        form.add(new JLabel("Data (dd/MM/aaaa):")); form.add(txtData);
        form.add(new JLabel("Descrição:")); form.add(txtDescricao);

        add(form, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar());
        add(btnSalvar, BorderLayout.SOUTH);

        carregarCombos();
    }

    private void carregarCombos() {
        try {
            cbVeiculo.removeAllItems();
            List<Veiculo> veiculos = veiculoService.listar();
            for(Veiculo v : veiculos) {
                // Sem filtro de ativo para permitir multas antigas
                String status = v.isAtivo() ? "" : " (Inativo)";
                cbVeiculo.addItem(v.getIdVeiculo() + " - " + v.getPlaca() + status);
            }
        } catch (Exception e) {}
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
            JOptionPane.showMessageDialog(this, "Salvo!");
            dispose();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage()); }
    }
}