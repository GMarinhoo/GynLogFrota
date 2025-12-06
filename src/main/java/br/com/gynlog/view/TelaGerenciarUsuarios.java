package br.com.gynlog.view;

import br.com.gynlog.enums.TipoUsuario;
import br.com.gynlog.model.Usuario;
import br.com.gynlog.service.UsuarioService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaGerenciarUsuarios extends JFrame {

    private JTextField txtId, txtLogin, txtNome;
    private JPasswordField txtSenha;
    private JComboBox<TipoUsuario> cbTipo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private final UsuarioService service;
    private final Usuario usuarioLogado;

    public TelaGerenciarUsuarios(UsuarioService service, Usuario usuarioLogado) {
        this.service = service;
        this.usuarioLogado = usuarioLogado;
        setTitle("Gerenciar Usuários");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtId = new JTextField(); txtId.setEditable(false);
        txtLogin = new JTextField();
        txtSenha = new JPasswordField();
        txtNome = new JTextField();
        cbTipo = new JComboBox<>(TipoUsuario.values());

        form.add(new JLabel("ID:")); form.add(txtId);
        form.add(new JLabel("Login:")); form.add(txtLogin);
        form.add(new JLabel("Senha:")); form.add(txtSenha);
        form.add(new JLabel("Nome:")); form.add(txtNome);
        form.add(new JLabel("Tipo:")); form.add(cbTipo);

        add(form, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Login", "Nome", "Tipo"}, 0);
        tabela = new JTable(modeloTabela);
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { preencherFormulario(); }
        });
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        JButton btnSalvar = new JButton("Salvar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar = new JButton("Limpar");

        btnSalvar.addActionListener(e -> salvar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparFormulario());

        botoes.add(btnSalvar); botoes.add(btnExcluir); botoes.add(btnLimpar);
        add(botoes, BorderLayout.SOUTH);

        carregarDados();
    }

    private void salvar() {
        try {
            Usuario u = new Usuario();
            u.setId(txtId.getText().isEmpty() ? 0 : Integer.parseInt(txtId.getText()));
            u.setLogin(txtLogin.getText());
            u.setSenha(new String(txtSenha.getPassword()));
            u.setNome(txtNome.getText());
            u.setTipo((TipoUsuario) cbTipo.getSelectedItem());

            if(u.getId() == 0) service.salvar(u);
            else service.atualizar(u);

            JOptionPane.showMessageDialog(this, "Salvo!");
            limparFormulario();
            carregarDados();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void excluir() {
        try {
            if(!txtId.getText().isEmpty()) {
                int id = Integer.parseInt(txtId.getText());
                if(id == usuarioLogado.getId()) {
                    JOptionPane.showMessageDialog(this, "Não pode excluir a si mesmo!");
                    return;
                }
                service.excluir(id);
                limparFormulario();
                carregarDados();
            }
        } catch (Exception ex) {}
    }

    private void carregarDados() {
        try {
            modeloTabela.setRowCount(0);
            java.util.List<br.com.gynlog.model.Usuario> lista = service.listar();
            for (br.com.gynlog.model.Usuario u : lista) {
                modeloTabela.addRow(new Object[]{u.getId(), u.getLogin(), u.getNome(), u.getTipo()});
            }
        } catch (Exception e) { // <--- GARANTA QUE ESTÁ 'Exception e'
            e.printStackTrace(); // <--- GARANTA QUE ESTÁ USANDO 'e'
        }
    }

    private void preencherFormulario() {
        int row = tabela.getSelectedRow();
        if(row != -1) {
            int id = Integer.parseInt(tabela.getValueAt(row, 0).toString());
            try {
                Usuario u = service.buscarPorId(id);
                if(u != null) {
                    txtId.setText(String.valueOf(u.getId()));
                    txtLogin.setText(u.getLogin());
                    txtSenha.setText(u.getSenha());
                    txtNome.setText(u.getNome());
                    cbTipo.setSelectedItem(u.getTipo());
                }
            } catch(Exception e){}
        }
    }

    private void limparFormulario() {
        txtId.setText(""); txtLogin.setText(""); txtSenha.setText(""); txtNome.setText("");
        cbTipo.setSelectedIndex(0);
    }
}