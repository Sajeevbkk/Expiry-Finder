package gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

/**
 * Universal resolution-independent vector icons for UI buttons.
 * Eliminates missing emoji glyphs and scales crisp on all displays.
 */
public class VectorIcon implements Icon {
    public enum Type {
        PLUS,
        CHECK,
        CLEAR,
        REFRESH,
        EDIT,
        TRASH,
        CHECKLIST,
        PACKAGE,
        SEARCH
    }

    private final Type type;
    private final int size;
    private Color customColor;

    public VectorIcon(Type type) {
        this(type, 16);
    }

    public VectorIcon(Type type, int size) {
        this.type = type;
        this.size = size;
    }

    public VectorIcon(Type type, int size, Color customColor) {
        this.type = type;
        this.size = size;
        this.customColor = customColor;
    }

    public void setColor(Color color) {
        this.customColor = color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Color drawColor = customColor != null ? customColor : (c != null ? c.getForeground() : Color.DARK_GRAY);
        g2.setColor(drawColor);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int cx = x + size / 2;
        int cy = y + size / 2;

        switch (type) {
            case PLUS -> {
                g2.drawLine(x + 3, cy, x + size - 3, cy);
                g2.drawLine(cx, y + 3, cx, y + size - 3);
            }
            case CHECK -> {
                Path2D p = new Path2D.Float();
                p.moveTo(x + 3, y + size * 0.52f);
                p.lineTo(x + size * 0.42f, y + size * 0.78f);
                p.lineTo(x + size - 3, y + size * 0.28f);
                g2.draw(p);
            }
            case CLEAR -> {
                g2.drawArc(x + 3, y + 3, size - 6, size - 6, 45, 270);
                g2.drawLine(x + 3, y + 3, x + 7, y + 3);
                g2.drawLine(x + 3, y + 3, x + 3, y + 7);
            }
            case REFRESH -> {
                g2.drawArc(x + 3, y + 3, size - 6, size - 6, 30, 300);
                g2.drawLine(x + size - 3, y + 4, x + size - 3, y + 8);
                g2.drawLine(x + size - 7, y + 8, x + size - 3, y + 8);
            }
            case EDIT -> {
                g2.drawLine(x + 3, y + size - 3, x + 6, y + size - 4);
                g2.drawLine(x + 6, y + size - 4, x + size - 4, y + 5);
                g2.drawLine(x + size - 4, y + 5, x + size - 5, y + 3);
                g2.drawLine(x + size - 5, y + 3, x + 4, y + size - 6);
                g2.drawLine(x + 4, y + size - 6, x + 3, y + size - 3);
            }
            case TRASH -> {
                g2.drawLine(x + 2, y + 4, x + size - 2, y + 4);
                g2.drawLine(x + 5, y + 2, x + size - 5, y + 2);
                g2.drawRoundRect(x + 4, y + 5, size - 8, size - 7, 2, 2);
                g2.drawLine(cx - 2, y + 7, cx - 2, y + size - 4);
                g2.drawLine(cx + 2, y + 7, cx + 2, y + size - 4);
            }
            case CHECKLIST -> {
                g2.drawRoundRect(x + 3, y + 3, size - 6, size - 4, 3, 3);
                g2.drawLine(cx - 2, y + 2, cx + 2, y + 2);
                g2.drawLine(x + 6, y + 7, x + size - 6, y + 7);
                g2.drawLine(x + 6, y + 10, x + size - 6, y + 10);
                g2.drawLine(x + 6, y + 13, x + size - 6, y + 13);
            }
            case PACKAGE -> {
                g2.drawRoundRect(x + 2, y + 3, size - 4, size - 5, 3, 3);
                g2.drawLine(x + 2, y + 7, x + size - 2, y + 7);
                g2.drawLine(cx, y + 7, cx, y + size - 2);
            }
            case SEARCH -> {
                int r = size - 7;
                g2.drawOval(x + 2, y + 2, r, r);
                g2.drawLine(x + r, y + r, x + size - 2, y + size - 2);
            }
        }

        g2.dispose();
    }
}
