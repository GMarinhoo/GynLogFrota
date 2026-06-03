package br.com.gynlog.view;

import br.com.gynlog.service.AbastecimentoService;
import br.com.gynlog.service.DespesaService;
import br.com.gynlog.service.VeiculoService;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;
import java.awt.*;

public class PainelPrincipal extends JPanel {

    private final ConfigurableApplicationContext context;

    public PainelPrincipal(ConfigurableApplicationContext context) {
        this.context = context;
        setBackground(Tema.CONTEUDO_BG);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        construir();
    }

    private void construir() {
        JLabel lblTitulo = new JLabel("Painel Principal");
        lblTitulo.setFont(Tema.FONTE_TITULO);
        lblTitulo.setForeground(Tema.TEXTO_TITULO);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel painelCards = new JPanel(new GridLayout(1, 3, 16, 0));
        painelCards.setBackground(Tema.CONTEUDO_BG);

        painelCards.add(criarCard("Total de Veículos",  contarVeiculos(),  Tema.TEXTO_TITULO));
        painelCards.add(criarCard("Veículos Inativos",  contarInativos(),  Tema.STATUS_INATIVO));
        painelCards.add(criarCard("Abastecimentos",     contarAbastecimentos(), Tema.TEXTO_TITULO));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Tema.CONTEUDO_BG);
        centro.add(painelCards, BorderLayout.NORTH);

        add(centro, BorderLayout.CENTER);
    }

    private JPanel criarCard(String titulo, String valor, Color corValor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Tema.PAINEL_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 222, 228), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.FONTE_LABEL);
        lblTitulo.setForeground(Tema.TEXTO_LABEL);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(corValor);
        lblValor.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitulo);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValor);

        return card;
    }

    private String contarVeiculos() {
        try {
            return String.valueOf(context.getBean(VeiculoService.class).listar().size());
        } catch (Exception e) { return "–"; }
    }

    private String contarInativos() {
        try {
            return String.valueOf(context.getBean(VeiculoService.class).listarInativos().size());
        } catch (Exception e) { return "–"; }
    }

    private String contarAbastecimentos() {
        try {
            return String.valueOf(context.getBean(AbastecimentoService.class).listar().size());
        } catch (Exception e) { return "–"; }
    }
}