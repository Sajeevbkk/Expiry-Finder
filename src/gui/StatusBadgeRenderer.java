package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class StatusBadgeRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String status = value != null ? value.toString() : "";

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setOpaque(true);

        if ("EXPIRED".equalsIgnoreCase(status)) {
            label.setBackground(UITheme.EXPIRED_BG);
            label.setForeground(UITheme.EXPIRED_FG);
        } else if (status.contains("<7d") || status.contains("CRITICAL")) {
            label.setBackground(UITheme.CRITICAL_BG);
            label.setForeground(UITheme.CRITICAL_FG);
        } else if (status.contains("<30d") || status.contains("EXPIRING")) {
            label.setBackground(UITheme.WARNING_BG);
            label.setForeground(UITheme.WARNING_FG);
        } else if ("GOOD".equalsIgnoreCase(status)) {
            label.setBackground(UITheme.GOOD_BG);
            label.setForeground(UITheme.GOOD_FG);
        } else {
            label.setBackground(table.getBackground());
            label.setForeground(table.getForeground());
        }

        if (isSelected) {
            label.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY, 1));
        } else {
            label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        }

        return label;
    }
}
