package com.pms.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UITheme Design System & Swing Styling Utilities
 * Provides a modern, cohesive color palette and styled components:
 * Navy Blue, Crisp White, Light Gray, and subtle shadows/accents.
 * 
 * Demonstrates:
 * - Clean UI/UX design principles in desktop Java
 * - Custom Swing component styling and event listeners
 */
public class UITheme {

    // --- Color Palette ---
    public static final Color NAVY_PRIMARY = new Color(30, 58, 138);     // #1E3A8A Deep Corporate Navy
    public static final Color NAVY_DARK = new Color(15, 23, 42);         // #0F172A Midnight Navy
    public static final Color BLUE_ACCENT = new Color(37, 99, 235);      // #2563EB Vibrant Blue
    public static final Color BLUE_HOVER = new Color(29, 78, 216);       // #1D4ED8 Darker Blue on Hover
    public static final Color BG_LIGHT = new Color(248, 250, 252);       // #F8FAFC Clean Light Gray Background
    public static final Color BG_CARD = Color.WHITE;                     // #FFFFFF Pure White
    public static final Color BORDER_COLOR = new Color(226, 232, 240);    // #E2E8F0 Subtle Slate Border
    public static final Color BORDER_FOCUS = new Color(59, 130, 246);    // #3B82F6 Focus Outline
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);      // #0F172A High Contrast Black/Slate
    public static final Color TEXT_MUTED = new Color(100, 116, 139);     // #64748B Subtitle Muted Gray
    public static final Color SUCCESS_GREEN = new Color(16, 185, 129);   // #10B981 Emerald Green
    public static final Color DANGER_RED = new Color(239, 68, 68);       // #EF4444 Crimson Red
    public static final Color WARNING_AMBER = new Color(245, 158, 11);   // #F59E0B Amber

    // --- Modern System Fonts ---
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    /**
     * Creates a styled Primary action button (Navy Blue with hover effect).
     */
    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(NAVY_PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BLUE_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(NAVY_PRIMARY);
            }
        });
        return btn;
    }

    /**
     * Creates a styled Secondary button (White with subtle border).
     */
    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(NAVY_DARK);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(9, 16, 9, 16)
        ));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(241, 245, 249));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    /**
     * Creates a styled Success button (Emerald Green).
     */
    public static JButton createSuccessButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(SUCCESS_GREEN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(5, 150, 105));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SUCCESS_GREEN);
            }
        });
        return btn;
    }

    /**
     * Creates a Danger/Delete button (Soft Red).
     */
    public static JButton createDangerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(Color.WHITE);
        btn.setBackground(DANGER_RED);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(220, 38, 38));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(DANGER_RED);
            }
        });
        return btn;
    }

    /**
     * Styles a JTextField with modern padding and subtle borders.
     */
    public static JTextField createTextField() {
        JTextField tf = new JTextField();
        styleTextInput(tf);
        return tf;
    }

    /**
     * Styles a JTextArea with word wrap and modern padding.
     */
    public static JTextArea createTextArea(int rows) {
        JTextArea ta = new JTextArea(rows, 20);
        ta.setFont(FONT_REGULAR);
        ta.setForeground(TEXT_PRIMARY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        return ta;
    }

    public static void styleTextInput(JTextField tf) {
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_PRIMARY);
        tf.setCaretColor(NAVY_PRIMARY);
        tf.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    /**
     * Creates a clean card container panel with subtle border.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BG_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Creates a standard form field label.
     */
    public static JLabel createFieldLabel(String labelText, boolean required) {
        String text = required ? "<html>" + labelText + " <font color='#EF4444'>*</font></html>" : labelText;
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    /**
     * Creates a section header divider panel.
     */
    public static JPanel createSectionHeader(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(12, 0, 6, 0));

        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_SECTION);
        lbl.setForeground(NAVY_PRIMARY);
        p.add(lbl, BorderLayout.WEST);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        p.add(sep, BorderLayout.SOUTH);
        return p;
    }

    /**
     * Applies standard Look & Feel.
     */
    public static void setupSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}
