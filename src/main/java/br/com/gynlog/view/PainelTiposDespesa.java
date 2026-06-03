package br.com.gynlog.view;

import br.com.gynlog.model.TipoDespesa;
import br.com.gynlog.service.TipoDespesaService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelTiposDespesa extends JPanel {

    private final TipoDespesaService service;
    private DefaultTableModel modeloTabela;
    private JTable tabela;
    private JTextField txtDescricao;
    private JLabel lblIdSelecionado;

    public PainelTiposDespesa(ConfigurableApplicationContext context) {
        this.service = context.getBean(TipoDespesaService.class);
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

        JLabel lblTitulo = new JLabel("Tipos de Despesa");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        painelBotoes.setBackground(Tema.CONTEUDO_BG);
        JButton btnSalvar  = Tema.botaoPrimario("+ Adicionar / Salvar");
        JButton btnExcluir = Tema.botaoPerigo("Excluir");
        JButton btnLimpar  = Tema.botaoSecundario("Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar); painelBotoes.add(btnExcluir); painelBotoes.add(btnLimpar);
        topo.add(lblTitulo, BorderLayout.NORTH);
        topo.add(painelBotoes, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Descrição"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        Tema.estilizarTabela(tabela);
        tabela.getColumnModel().getColumn(0).setMaxWidth(60);
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) preencherFormulario();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 222, 228), 1));

        JPanel form = new JPanel();
        form.setBackground(Tema.PAINEL_BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(Tema.bordaPainel());
        form.setPreferredSize(new Dimension(260, 0));

        lblIdSelecionado = new JLabel("Novo tipo");
        lblIdSelecionado.setFont(Tema.FONTE_SUBTITULO);
        lblIdSelecionado.setForeground(Tema.TEXTO_TITULO);
        lblIdSelecionado.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtDescricao = Tema.campo();
        txtDescricao.setDocument(new PainelDespesas.LimiteCaracteres(50));

        JPanel campoDesc = new JPanel(new BorderLayout(0, 4));
        campoDesc.setBackground(Tema.PAINEL_BG);
        campoDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        campoDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        campoDesc.add(Tema.label("Descrição *"), BorderLayout.NORTH);
        campoDesc.add(txtDescricao, BorderLayout.CENTER);

        form.add(lblIdSelecionado);
        form.add(Box.createVerticalStrut(16));
        form.add(campoDesc);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, form);
        split.setDividerLocation(580);
        split.setBorder(null);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);
    }

    private void salvar() {
        String idTxt = lblIdSelecionado.getClientProperty("id") != null
                ? lblIdSelecionado.getClientProperty("id").toString() : "";
        try {
            TipoDespesa t = new TipoDespesa();
            t.setDescricao(txtDescricao.getText().trim());

            if (idTxt.isEmpty()) {
                service.salvar(t);
            } else {
                t.setIdTipoDespesa(Integer.parseInt(idTxt));
                service.atualizar(t);
            }
            JOptionPane.showMessageDialog(this, "Salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Selecione um tipo na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
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

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        try {
            for (TipoDespesa t : service.listar()) {
                modeloTabela.addRow(new Object[]{t.getIdTipoDespesa(), t.getDescricao()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        int id = (int) modeloTabela.getValueAt(row, 0);
        lblIdSelecionado.setText("Editando ID: " + id);
        lblIdSelecionado.putClientProperty("id", String.valueOf(id));
        txtDescricao.setText(modeloTabela.getValueAt(row, 1).toString());
    }

    private void limparFormulario() {
        lblIdSelecionado.setText("Novo tipo");
        lblIdSelecionado.putClientProperty("id", "");
        txtDescricao.setText("");
        tabela.clearSelection();
    }
}