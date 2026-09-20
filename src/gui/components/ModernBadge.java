package gui.components;

import gui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern React-style pill badge with status dot indicator.
 */
public class ModernBadge extends JComponent {
    private String text;
    private Color bgColor;
    private Color fgColor;
    private Color dotColor;

    public ModernBadge(String text, Color bgColor, Color fgColor, Color dotColor) {
        this.text = text;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.dotColor = dotColor;
        setFont(UITheme.FONT_BADGE);
        setPreferredSize(new Dimension(120, 28));
    }

    public void setStatus(String status) {
        this.text = status;
        if ("EXPIRED".equalsIgnoreCase(status)) {
            bgColor = UITheme.EXPIRED_BG;
            fgColor = UITheme.EXPIRED_FG;
            dotColor = UITheme.EXPIRED_FG;
        } else if (status.contains("<7d") || status.contains("CRITICAL")) {
            bgColor = UITheme.CRITICAL_BG;
            fgColor = UITheme.CRITICAL_FG;
            dotColor = UITheme.CRITICAL_FG;
        } else if (status.contains("<30d") || status.contains("EXPIRING")) {
            bgColor = UITheme.WARNING_BG;
            fgColor = UITheme.WARNING_FG;
            dotColor = UITheme.WARNING_FG;
        } else {
            bgColor = UITheme.GOOD_BG;
            fgColor = UITheme.GOOD_FG;
            dotColor = UITheme.GOOD_FG;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Pill background
        g2.setColor(bgColor != null ? bgColor : UITheme.GOOD_BG);
        g2.fillRoundRect(2, 2, w - 4, h - 4, h, h);

        // Dot indicator
        int dotSize = 6;
        int dotX = 10;
        int dotY = (h - dotSize) / 2;
        g2.setColor(dotColor != null ? dotColor : fgColor);
        g2.fillOval(dotX, dotY, dotSize, dotSize);

        // Text
        g2.setFont(getFont());
        g2.setColor(fgColor != null ? fgColor : UITheme.TEXT_MAIN);
        FontMetrics fm = g2.getFontMetrics();
        int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, dotX + dotSize + 6, textY);

        g2.dispose();
    }
}
