package br.com.gynlog.view;

import br.com.gynlog.model.Usuario;
import br.com.gynlog.service.UsuarioService;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private final ConfigurableApplicationContext context;
    private final UsuarioService usuarioService;

    public TelaLogin(ConfigurableApplicationContext context) {
        this.context = context;
        this.usuarioService = context.getBean(UsuarioService.class);
        configurarJanela();
        criarComponentes();
    }

    private void configurarJanela() {
        setTitle("Acesso ao Sistema");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null); // Layout nulo é bem "iniciante", mas funciona
    }

    private void criarComponentes() {
        JLabel lblUser = new JLabel("Usuário:");
        lblUser.setBounds(30, 30, 80, 25);
        add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(100, 30, 200, 25);
        add(txtUsuario);

        JLabel lblPass = new JLabel("Senha:");
        lblPass.setBounds(30, 70, 80, 25);
        add(lblPass);

        txtSenha = new JPasswordField();
        txtSenha.setBounds(100, 70, 200, 25);
        add(txtSenha);

        JButton btnEntrar = new JButton("Entrar");
        btnEntrar.setBounds(100, 110, 100, 30);
        btnEntrar.addActionListener(e -> realizarLogin());
        add(btnEntrar);
    }

    private void realizarLogin() {
        String login = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        Usuario usuario = usuarioService.autenticar(login, senha);

        if (usuario != null) {
            dispose();
            new TelaPrincipal(usuario, context).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso Negado");
        }
    }
}