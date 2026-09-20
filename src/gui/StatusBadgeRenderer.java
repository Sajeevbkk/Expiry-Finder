package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Modern React-style Pill Badge renderer for JTable cells.
 */
public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        String status = value != null ? value.toString() : "";
        return new PillBadgeCell(status);
    }

    private static class PillBadgeCell extends JPanel {
        private final String status;
        private final Color bgColor;
        private final Color fgColor;
        private final Color borderColor;
        private final String displayText;

        PillBadgeCell(String status) {
            this.status = status;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

            if ("EXPIRED".equalsIgnoreCase(status)) {
                bgColor = UITheme.EXPIRED_BG;
                fgColor = UITheme.EXPIRED_FG;
                borderColor = UITheme.EXPIRED_BORDER;
                displayText = "● Expired";
            } else if (status.contains("<7d") || status.contains("CRITICAL")) {
                bgColor = UITheme.CRITICAL_BG;
                fgColor = UITheme.CRITICAL_FG;
                borderColor = UITheme.CRITICAL_BORDER;
                displayText = "● Critical (<7d)";
            } else if (status.contains("<30d") || status.contains("EXPIRING")) {
                bgColor = UITheme.WARNING_BG;
                fgColor = UITheme.WARNING_FG;
                borderColor = UITheme.WARNING_BORDER;
                displayText = "● Expiring (<30d)";
            } else {
                bgColor = UITheme.GOOD_BG;
                fgColor = UITheme.GOOD_FG;
                borderColor = UITheme.GOOD_BORDER;
                displayText = "● Good";
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pillW = 120;
            int pillH = 26;
            int x = (getWidth() - pillW) / 2;
            int y = (getHeight() - pillH) / 2;

            // Fill pill
            g2.setColor(bgColor);
            g2.fillRoundRect(x, y, pillW, pillH, pillH, pillH);

            // Border
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(x, y, pillW, pillH, pillH, pillH);

            // Text
            g2.setColor(fgColor);
            g2.setFont(UITheme.FONT_BADGE);
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (pillW - fm.stringWidth(displayText)) / 2;
            int ty = y + (pillH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(displayText, tx, ty);

            g2.dispose();
        }
    }
}
