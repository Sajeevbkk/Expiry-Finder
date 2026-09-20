package gui.components;

import gui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern React/Tailwind-style Card component.
 * Features rounded corners, crisp border, and antialiased rendering.
 */
public class ModernCard extends JPanel {
    private int radius;
    private Color backgroundColor;
    private Color borderColor;

    public ModernCard() {
        this(18, UITheme.CARD_BG, UITheme.CARD_BORDER);
    }

    public ModernCard(int radius) {
        this(radius, UITheme.CARD_BG, UITheme.CARD_BORDER);
    }

    public ModernCard(int radius, Color backgroundColor, Color borderColor) {
        this.radius = radius;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
        setOpaque(false);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(backgroundColor != null ? backgroundColor : UITheme.CARD_BG);
        g2.fillRoundRect(0, 0, width - 1, height - 1, radius, radius);

        // Border
        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
