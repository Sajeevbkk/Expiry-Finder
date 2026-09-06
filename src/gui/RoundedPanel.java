/* Written By Antigravity */

package gui;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {
    private int radius;
    private Color backgroundColor;

    public RoundedPanel(int radius) {
        super();
        this.radius = radius;
        setOpaque(false);
    }

    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.backgroundColor = bgColor;
        setOpaque(false);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.backgroundColor = bg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (backgroundColor != null) {
            g2.setColor(backgroundColor);
        } else {
            g2.setColor(getBackground());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
    }
}
