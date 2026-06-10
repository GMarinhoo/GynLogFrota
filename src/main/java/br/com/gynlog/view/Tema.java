package br.com.gynlog.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Tema {

    public static final Color SIDEBAR_BG       = new Color(43, 45, 58);
    public static final Color SIDEBAR_ITEM_SEL = new Color(60, 63, 80);
    public static final Color SIDEBAR_TEXTO     = new Color(200, 202, 210);
    public static final Color SIDEBAR_TITULO    = new Color(255, 255, 255);

    public static final Color CONTEUDO_BG      = new Color(244, 245, 247);
    public static final Color PAINEL_BG        = new Color(255, 255, 255);

    public static final Color TABELA_HEADER_BG = new Color(55, 58, 75);
    public static final Color TABELA_HEADER_FG = new Color(255, 255, 255);
    public static final Color TABELA_LINHA_PAR  = new Color(255, 255, 255);
    public static final Color TABELA_LINHA_IMPAR= new Color(248, 249, 251);
    public static final Color TABELA_SEL_BG    = new Color(210, 215, 230);
    public static final Color TABELA_SEL_FG    = new Color(30, 30, 40);
    public static final Color TABELA_GRADE     = new Color(225, 227, 232);

    public static final Color BTN_PRIMARIO_BG  = new Color(55, 58, 75);
    public static final Color BTN_PRIMARIO_FG  = Color.WHITE;
    public static final Color BTN_SECUNDARIO_BG= new Color(255, 255, 255);
    public static final Color BTN_SECUNDARIO_FG= new Color(55, 58, 75);
    public static final Color BTN_PERIGO_BG    = new Color(180, 60, 60);
    public static final Color BTN_PERIGO_FG    = Color.WHITE;

    public static final Color TEXTO_TITULO     = new Color(30, 32, 45);
    public static final Color TEXTO_LABEL      = new Color(80, 85, 100);
    public static final Color BORDA_CAMPO      = new Color(200, 203, 215);
    public static final Color CAMPO_BG         = new Color(255, 255, 255);

    public static final Color STATUS_ATIVO     = new Color(40, 130, 80);
    public static final Color STATUS_INATIVO   = new Color(160, 60, 60);

    public static final Font FONTE_TITULO      = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONTE_SUBTITULO   = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_LABEL       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_CAMPO       = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_BOTAO       = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONTE_TABELA      = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONTE_HEADER      = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONTE_SIDEBAR     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_SIDEBAR_TIT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONTE_STATUS      = new Font("Segoe UI", Font.PLAIN, 11);

    public static JButton botaoPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(BTN_PRIMARIO_BG);
        btn.setForeground(BTN_PRIMARIO_FG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 32));
        return btn;
    }

    public static JButton botaoSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(BTN_SECUNDARIO_BG);
        btn.setForeground(BTN_SECUNDARIO_FG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(BORDA_CAMPO, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 32));
        return btn;
    }

    public static JButton botaoPerigo(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(FONTE_BOTAO);
        btn.setBackground(BTN_PERIGO_BG);
        btn.setForeground(BTN_PERIGO_FG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 32));
        return btn;
    }

    public static JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(FONTE_CAMPO);
        tf.setBackground(CAMPO_BG);
        tf.setForeground(TEXTO_TITULO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDA_CAMPO, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    public static JComboBox<?> combo() {
        JComboBox<?> cb = new JComboBox<>();
        cb.setFont(FONTE_CAMPO);
        cb.setBackground(CAMPO_BG);
        cb.setForeground(TEXTO_TITULO);
        cb.setBorder(BorderFactory.createLineBorder(BORDA_CAMPO, 1));
        return cb;
    }

    public static JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FONTE_LABEL);
        lbl.setForeground(TEXTO_LABEL);
        return lbl;
    }

    public static Border bordaPainel() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 222, 228), 1),
                BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public static void estilizarTabela(JTable tabela) {
        tabela.setFont(FONTE_TABELA);
        tabela.setRowHeight(28);
        tabela.setShowGrid(true);
        tabela.setGridColor(TABELA_GRADE);
        tabela.setSelectionBackground(TABELA_SEL_BG);
        tabela.setSelectionForeground(TABELA_SEL_FG);
        tabela.setFillsViewportHeight(true);
        tabela.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = tabela.getTableHeader();
        header.setFont(FONTE_HEADER);
        header.setBackground(TABELA_HEADER_BG);
        header.setForeground(TABELA_HEADER_FG);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, col);
                lbl.setBackground(TABELA_HEADER_BG);
                lbl.setForeground(TABELA_HEADER_FG);
                lbl.setFont(FONTE_HEADER);
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(75, 78, 98)),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? TABELA_LINHA_PAR : TABELA_LINHA_IMPAR);
                    setForeground(TEXTO_TITULO);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });
    }

    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("OptionPane.messageFont", FONTE_LABEL);
        UIManager.put("OptionPane.buttonFont",  FONTE_BOTAO);
    }
}