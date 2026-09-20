package gui.components;

import gui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Modern React/Tailwind-style button with hover transitions and variant styles.
 */
public class ModernButton extends JButton {
    public enum Variant {
        PRIMARY,
        SECONDARY,
        DANGER,
        DANGER_SOFT,
        GHOST,
        OUTLINE
    }

    private final Variant variant;
    private final int radius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public ModernButton(String text) {
        this(text, (Icon) null, Variant.PRIMARY, 14);
    }

    public ModernButton(String text, Variant variant) {
        this(text, (Icon) null, variant, 14);
    }

    public ModernButton(String text, Variant variant, int radius) {
        this(text, (Icon) null, variant, radius);
    }

    public ModernButton(String text, Icon icon) {
        this(text, icon, Variant.PRIMARY, 14);
    }

    public ModernButton(String text, Icon icon, Variant variant) {
        this(text, icon, variant, 14);
    }

    public ModernButton(String text, Icon icon, Variant variant, int radius) {
        super(text, icon);
        this.variant = variant;
        this.radius = radius;

        setIconTextGap(9);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setFont(UITheme.FONT_BUTTON);

        applyVariantColors();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    private void applyVariantColors() {
        switch (variant) {
            case PRIMARY -> setForeground(Color.WHITE);
            case SECONDARY -> setForeground(UITheme.PRIMARY_DARK);
            case DANGER -> setForeground(Color.WHITE);
            case DANGER_SOFT -> setForeground(UITheme.EXPIRED_FG);
            case GHOST, OUTLINE -> setForeground(UITheme.TEXT_DARK);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        Color bg;
        Color border = null;

        switch (variant) {
            case PRIMARY -> {
                if (isPressed) bg = new Color(0x047857);
                else if (isHovered) bg = UITheme.PRIMARY_DARK;
                else bg = UITheme.PRIMARY;
            }
            case SECONDARY -> {
                if (isPressed) bg = new Color(0xBBF7D0);
                else if (isHovered) bg = UITheme.PRIMARY_HOVER;
                else bg = UITheme.PRIMARY_LIGHT;
                border = UITheme.PRIMARY_BORDER;
            }
            case DANGER -> {
                if (isPressed) bg = new Color(0xB91C1C);
                else if (isHovered) bg = new Color(0xDC2626);
                else bg = new Color(0xEF4444);
            }
            case DANGER_SOFT -> {
                if (isPressed) bg = new Color(0xFECDD3);
                else if (isHovered) bg = new Color(0xFFE4E6);
                else bg = new Color(0xFFF1F2);
                border = UITheme.EXPIRED_BORDER;
            }
            case OUTLINE -> {
                if (isHovered) bg = new Color(0xF1F5F9);
                else bg = Color.WHITE;
                border = new Color(0xCBD5E1);
            }
            default -> { // GHOST
                if (isHovered) bg = new Color(0xF1F5F9);
                else bg = new Color(0, 0, 0, 0);
            }
        }

        // Fill background
        if (bg.getAlpha() > 0) {
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }

        // Draw border if defined
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
