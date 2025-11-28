package br.com.gynlog.view;

import br.com.gynlog.model.Usuario;
import br.com.gynlog.service.UsuarioService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;

    // O Contexto do Spring quw guarda os Services
    private final ConfigurableApplicationContext context;
    private final UsuarioService usuarioService;

    public TelaLogin(ConfigurableApplicationContext context) {
        this.context = context;
        // Pega o Service de Usuário de dentro do Spring
        this.usuarioService = context.getBean(UsuarioService.class);

        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("GynLog - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void criarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(41, 128, 185));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("SISTEMA GYNLOG", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblUsuario = new JLabel("Usuário:");
        lblUsuario.setForeground(Color.WHITE);
        panel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.WHITE);
        panel.add(lblSenha, gbc);

        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        panel.add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(new Color(46, 204, 113));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 14));
        btnEntrar.addActionListener(e -> realizarLogin());
        panel.add(btnEntrar, gbc);

        gbc.gridy = 4;
        JLabel lblInfo = new JLabel("Usuário padrão: admin / admin", SwingConstants.CENTER);
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 10));
        panel.add(lblInfo, gbc);

        add(panel);
    }

    private void realizarLogin() {
        String login = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha usuário e senha!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuario = usuarioService.autenticar(login, senha);

        if (usuario != null) {
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuario.getNome() + "!");
            dispose();
            new TelaPrincipal(usuario, context).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtUsuario.requestFocus();
        }
    }
}