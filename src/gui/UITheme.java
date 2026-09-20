package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {
    // --- Modern React-like Color Palette (Tailwind Emerald / Slate Inspired) ---
    public static final Color PRIMARY = new Color(0x10B981);       // Emerald 500
    public static final Color PRIMARY_DARK = new Color(0x059669);  // Emerald 600
    public static final Color PRIMARY_LIGHT = new Color(0xECFDF5); // Emerald 50
    public static final Color PRIMARY_HOVER = new Color(0xD1FAE5); // Emerald 100
    public static final Color PRIMARY_BORDER = new Color(0xA7F3D0);// Emerald 200

    // Backgrounds & Surface
    public static final Color MAIN_BG = new Color(0xF8FAFC);       // Slate 50
    public static final Color SIDEBAR_BG = new Color(0xECFDF5);    // Soft Mint Emerald
    public static final Color CARD_BG = Color.WHITE;
    public static final Color CARD_BORDER = new Color(0xE2E8F0);   // Slate 200
    public static final Color INPUT_BG = new Color(0xF8FAFC);
    public static final Color INPUT_BORDER = new Color(0xCBD5E1);  // Slate 300

    // Typography Colors
    public static final Color TEXT_MAIN = new Color(0x0F172A);     // Slate 900 (High contrast, crisp)
    public static final Color TEXT_DARK = new Color(0x1E293B);     // Slate 800
    public static final Color TEXT_MUTED = new Color(0x64748B);    // Slate 500
    public static final Color TEXT_LIGHT = Color.WHITE;

    // Status / Alert Colors (Pill Badge styles)
    public static final Color EXPIRED_FG = new Color(0xE11D48);    // Rose 600
    public static final Color EXPIRED_BG = new Color(0xFFE4E6);    // Rose 100
    public static final Color EXPIRED_BORDER = new Color(0xFECDD3);// Rose 200

    public static final Color CRITICAL_FG = new Color(0xEA580C);   // Orange 600
    public static final Color CRITICAL_BG = new Color(0xFFEDD5);   // Orange 100
    public static final Color CRITICAL_BORDER = new Color(0xFED7AA);

    public static final Color WARNING_FG = new Color(0xD97706);    // Amber 600
    public static final Color WARNING_BG = new Color(0xFEF3C7);    // Amber 100
    public static final Color WARNING_BORDER = new Color(0xFDE68A);

    public static final Color GOOD_FG = new Color(0x059669);       // Emerald 600
    public static final Color GOOD_BG = new Color(0xD1FAE5);       // Emerald 100
    public static final Color GOOD_BORDER = new Color(0xA7F3D0);

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 12);

    // Currency Formatting (Indian Rupees)
    public static final String CURRENCY_SYMBOL = "₹";

    public static String formatCurrency(double amount) {
        return String.format("₹%.2f", amount);
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(44);
        table.setGridColor(new Color(0xF1F5F9));
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setSelectionBackground(new Color(0xECFDF5));
        table.setSelectionForeground(TEXT_MAIN);
        table.setFocusable(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(0xF8FAFC));
        header.setForeground(new Color(0x475569));
        header.setPreferredSize(new Dimension(0, 44));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0xE2E8F0)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
    }
}
