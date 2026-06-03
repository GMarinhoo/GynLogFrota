package br.com.gynlog.view;

import br.com.gynlog.service.*;
import org.springframework.context.ConfigurableApplicationContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TelaPrincipal extends JFrame {

    private final ConfigurableApplicationContext context;
    private JPanel painelConteudo;
    private JPanel btnAtual;

    public TelaPrincipal(ConfigurableApplicationContext context) {
        this.context = context;
        Tema.instalar();
        configurarJanela();
        construirLayout();
        abrirPainel(new PainelPrincipal(context));
    }

    private void configurarJanela() {
        setTitle("GynLog Frota");
        setSize(960, 620);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void construirLayout() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Tema.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel painelLogo = new JPanel(new BorderLayout());
        painelLogo.setBackground(Tema.SIDEBAR_BG);
        painelLogo.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));
        JLabel lblLogo = new JLabel("GynLog Frota");
        lblLogo.setFont(Tema.FONTE_SIDEBAR_TIT);
        lblLogo.setForeground(Tema.SIDEBAR_TITULO);
        painelLogo.add(lblLogo, BorderLayout.CENTER);
        sidebar.add(painelLogo);
        sidebar.add(separadorSidebar());

        sidebar.add(itemSidebar("  Painel Principal",  () -> abrirPainel(new PainelPrincipal(context))));
        sidebar.add(itemSidebar("  Veículos",          () -> abrirPainel(new PainelVeiculos(context))));
        sidebar.add(itemSidebar("  Abastecimentos",    () -> abrirPainel(new PainelAbastecimentos(context))));
        sidebar.add(itemSidebar("  Despesas",          () -> abrirPainel(new PainelDespesas(context))));
        sidebar.add(itemSidebar("  Tipos de Despesa",  () -> abrirPainel(new PainelTiposDespesa(context))));
        sidebar.add(itemSidebar("  Relatórios",        () -> abrirPainel(new PainelRelatorios(context))));
        sidebar.add(Box.createVerticalGlue());

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.setBackground(Tema.CONTEUDO_BG);

        add(sidebar, BorderLayout.WEST);
        add(painelConteudo, BorderLayout.CENTER);
    }

    public void abrirPainel(JPanel painel) {
        painelConteudo.removeAll();
        painelConteudo.add(painel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private JPanel itemSidebar(String texto, Runnable acao) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Tema.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(Tema.FONTE_SIDEBAR);
        lbl.setForeground(Tema.SIDEBAR_TEXTO);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
        item.add(lbl, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                acao.run();
            }
            @Override public void mouseEntered(MouseEvent e) {
                item.setBackground(Tema.SIDEBAR_ITEM_SEL);
                lbl.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(MouseEvent e) {
                item.setBackground(Tema.SIDEBAR_BG);
                lbl.setForeground(Tema.SIDEBAR_TEXTO);
            }
        });

        return item;
    }

    private Component separadorSidebar() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(65, 68, 85));
        sep.setBackground(Tema.SIDEBAR_BG);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}