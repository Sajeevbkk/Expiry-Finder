package gui;

import gui.modified.RoundedPanel;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        initUI();
    }

    private void initUI() {
        // Center card with app info
        RoundedPanel card = new RoundedPanel(24, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        ImageIcon icon = new ImageIcon("src/assets/icon.png");
        JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH)));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("EXPIRY FINDER");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UITheme.PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("Version 1.0 • Desktop Shopkeeper Edition");
        version.setFont(UITheme.FONT_SUBTITLE);
        version.setForeground(UITheme.TEXT_MUTED);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html><center style='width: 500px;'>" +
                "Expiry Finder is a desktop inventory and expiry management solution built for " +
                "retail shopkeepers, supermarkets, and pharmacies to eliminate stock waste, " +
                "organize product batches, and safeguard customer health by preventing expired sales." +
                "</center></html>");
        desc.setFont(UITheme.FONT_REGULAR);
        desc.setForeground(UITheme.TEXT_DARK);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel featuresBox = new JPanel(new GridLayout(4, 1, 0, 8));
        featuresBox.setOpaque(false);
        featuresBox.setMaximumSize(new Dimension(520, 150));
        featuresBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xEEEEEE), 1, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        featuresBox.add(new JLabel("📦  Product & Multi-Batch Stock Tracking with EAN/Barcodes"));
        featuresBox.add(new JLabel("⚠️  Visual Expiry Radar (Expired, <7 Days, <30 Days, Good)"));
        featuresBox.add(new JLabel("🔍  Instant Barcode Scanning & Fast Shelf Lookup"));
        featuresBox.add(new JLabel("💾  Embedded SQLite Database (`db/product.db`) for reliable offline storage"));

        featuresBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tipLabel = new JLabel("Tip: Click '📋 View Printable Shelf Checklist' on the Expiry Alerts tab to inspect shelves quickly!");
        tipLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        tipLabel.setForeground(UITheme.PRIMARY_DARK);
        tipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(title);
        card.add(Box.createVerticalStrut(5));
        card.add(version);
        card.add(Box.createVerticalStrut(20));
        card.add(desc);
        card.add(Box.createVerticalStrut(25));
        card.add(featuresBox);
        card.add(Box.createVerticalStrut(25));
        card.add(tipLabel);

        add(card, BorderLayout.CENTER);
    }
}
