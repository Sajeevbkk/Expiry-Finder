package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {
    // Primary palette
    public static final Color PRIMARY = new Color(0x299645);
    public static final Color PRIMARY_DARK = new Color(0x1E6B32);
    public static final Color PRIMARY_LIGHT = new Color(0xE4F1E8);
    public static final Color PRIMARY_HOVER = new Color(0xD2E8D8);

    // Sidebar & Backgrounds
    public static final Color SIDEBAR_BG = new Color(0xDAE9CA);
    public static final Color MAIN_BG = new Color(0xF4F6F4);
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(0xE0E0E0);

    // Status Alert Colors
    public static final Color EXPIRED_FG = new Color(0xC62828);
    public static final Color EXPIRED_BG = new Color(0xFFCDD2);

    public static final Color CRITICAL_FG = new Color(0xD84315);
    public static final Color CRITICAL_BG = new Color(0xFFE0B2);

    public static final Color WARNING_FG = new Color(0xF57F17);
    public static final Color WARNING_BG = new Color(0xFFF9C4);

    public static final Color GOOD_FG = new Color(0x2E7D32);
    public static final Color GOOD_BG = new Color(0xC8E6C9);

    // Text Colors
    public static final Color TEXT_DARK = new Color(0x182432);
    public static final Color TEXT_MUTED = new Color(0x666666);
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(38);
        table.setGridColor(new Color(0xEEEEEE));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(0xE1EFE5));
        table.setSelectionForeground(TEXT_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0xEDF3EA));
        header.setForeground(TEXT_DARK);
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }
}
