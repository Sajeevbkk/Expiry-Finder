package gui.components;

import gui.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Modern React-style TextField with placeholder support, focus glow, and smooth rounded borders.
 */
public class ModernTextField extends JTextField {
    private String placeholder = "";
    private final int radius;
    private boolean isFocused = false;

    public ModernTextField() {
        this("", 12);
    }

    public ModernTextField(String placeholder) {
        this(placeholder, 12);
    }

    public ModernTextField(String placeholder, int radius) {
        this.placeholder = placeholder;
        this.radius = radius;

        setOpaque(false);
        setFont(UITheme.FONT_REGULAR);
        setForeground(UITheme.TEXT_MAIN);
        setCaretColor(UITheme.PRIMARY_DARK);
        setBorder(new EmptyBorder(8, 14, 8, 14));

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(isEnabled() ? Color.WHITE : new Color(0xF1F5F9));
        g2.fillRoundRect(1, 1, w - 2, h - 2, radius, radius);

        // Border & Focus Ring
        if (isFocused) {
            // Subtle glow ring
            g2.setColor(new Color(0xD1FAE5));
            g2.setStroke(new BasicStroke(3.5f));
            g2.drawRoundRect(2, 2, w - 5, h - 5, radius, radius);

            g2.setColor(UITheme.PRIMARY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
        } else {
            g2.setColor(UITheme.INPUT_BORDER);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);

        // Draw placeholder if empty
        if (getText().isEmpty() && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D gp = (Graphics2D) g.create();
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            gp.setFont(getFont());
            gp.setColor(new Color(0x94A3B8)); // Slate 400

            FontMetrics fm = gp.getFontMetrics();
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            Insets insets = getInsets();
            gp.drawString(placeholder, insets.left, y);
            gp.dispose();
        }
    }
}
