package br.com.gynlog.view;

import br.com.gynlog.enums.TipoUsuario;
import br.com.gynlog.model.Usuario;
import br.com.gynlog.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaGerenciarUsuarios extends JFrame {

    // Componentes Visuais
    private JTextField txtId, txtLogin, txtNome;
    private JPasswordField txtSenha; // Senha oculta
    private JComboBox<TipoUsuario> cbTipo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;

    // Dependências
    private final UsuarioService service;
    private final Usuario usuarioLogado;

    // Cores
    private static final Color COR_PRIMARY = new Color(46, 204, 113); // Verde para gestão de pessoas
    private static final Color COR_BG = new Color(236, 240, 241);

    public TelaGerenciarUsuarios(UsuarioService service, Usuario usuarioLogado) {
        this.service = service;
        this.usuarioLogado = usuarioLogado;

        configurarJanela();
        criarComponentes();
        carregarDados();
    }

    private void configurarJanela() {
        setTitle("Gerenciamento de Usuários");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COR_BG);
    }

    private void criarComponentes() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(COR_PRIMARY);
        JLabel lblTitulo = new JLabel("👥 Gerenciar Usuários do Sistema");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        add(header, BorderLayout.NORTH);

        // Formulario
        JPanel painelForm = new JPanel(new GridLayout(3, 4, 10, 10));
        painelForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelForm.setBackground(Color.WHITE);

        txtId = new JTextField();
        txtId.setEditable(false); // ID não se mexe
        txtId.setBackground(new Color(230, 230, 230));

        txtLogin = new JTextField();
        txtSenha = new JPasswordField();
        txtNome = new JTextField();
        cbTipo = new JComboBox<>(TipoUsuario.values());

        painelForm.add(new JLabel("ID:")); painelForm.add(txtId);
        painelForm.add(new JLabel("Login:")); painelForm.add(txtLogin);
        painelForm.add(new JLabel("Senha:")); painelForm.add(txtSenha);
        painelForm.add(new JLabel("Nome Completo:")); painelForm.add(txtNome);
        painelForm.add(new JLabel("Tipo de Acesso:")); painelForm.add(cbTipo);

        // Tabela
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Login", "Nome", "Tipo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Evento de Clique na tabela para editar
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                preencherFormulario();
            }
        });

        // Painel Central que Une Form e Tabela
        JPanel central = new JPanel(new BorderLayout(10, 10));
        central.add(painelForm, BorderLayout.NORTH);
        central.add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(central, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(COR_BG);

        JButton btnSalvar = new JButton("Salvar / Atualizar");
        btnSalvar.setBackground(new Color(52, 152, 219));
        btnSalvar.setForeground(Color.WHITE);

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBackground(new Color(231, 76, 60));
        btnExcluir.setForeground(Color.WHITE);

        JButton btnLimpar = new JButton("Limpar / Novo");

        // Ações dos Botões
        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    // Lógica do Crud
    private void salvar() {
        try {
            if(txtLogin.getText().trim().isEmpty() || new String(txtSenha.getPassword()).isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login e Senha são obrigatórios!");
                return;
            }

            Usuario u = new Usuario();
            u.setId(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            u.setLogin(txtLogin.getText());
            u.setSenha(new String(txtSenha.getPassword()));
            u.setNome(txtNome.getText());
            u.setTipo((TipoUsuario) cbTipo.getSelectedItem());

            if (u.getId() == 0) {
                service.salvar(u);
            } else {
                service.atualizar(u);
            }

            JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!");
            limparFormulario();
            carregarDados();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    private void excluir() {
        try {
            if (txtId.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para excluir.");
                return;
            }

            int id = Integer.parseInt(txtId.getText());

            // Regra para Segurança
            if (id == usuarioLogado.getId()) {
                JOptionPane.showMessageDialog(this, "Você não pode excluir seu próprio usuário logado!", "Ação Bloqueada", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir este usuário?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                service.excluir(id);
                JOptionPane.showMessageDialog(this, "Usuário excluído.");
                limparFormulario();
                carregarDados();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao excluir: " + ex.getMessage());
        }
    }

    private void carregarDados() {
        try {
            modeloTabela.setRowCount(0); // Limpa tabela
            List<Usuario> lista = service.listar();
            for (Usuario u : lista) {
                modeloTabela.addRow(new Object[]{
                        u.getId(),
                        u.getLogin(),
                        u.getNome(),
                        u.getTipo().getDescricao()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if (row != -1) {
            int id = Integer.parseInt(tabela.getValueAt(row, 0).toString());

            // Busca no Service para Pegar a Senha
            try {
                Usuario u = service.buscarPorId(id);
                if (u != null) {
                    txtId.setText(String.valueOf(u.getId()));
                    txtLogin.setText(u.getLogin());
                    txtSenha.setText(u.getSenha());
                    txtNome.setText(u.getNome());
                    cbTipo.setSelectedItem(u.getTipo());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void limparFormulario() {
        txtId.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        txtNome.setText("");
        cbTipo.setSelectedIndex(0);
        tabela.clearSelection();
    }
}