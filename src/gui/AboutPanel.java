package gui;

import gui.components.ModernCard;

import javax.swing.*;
import java.awt.*;

public class AboutPanel extends JPanel {
    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        initUI();
    }

    private void initUI() {
        ModernCard card = new ModernCard(24);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 36, 20, 36));

        ImageIcon icon = new ImageIcon("src/assets/icon.png");
        JLabel iconLabel = new JLabel(new ImageIcon(icon.getImage().getScaledInstance(88, 88, Image.SCALE_SMOOTH)));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Expiry Finder", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UITheme.PRIMARY_DARK);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("Smart & Simple Stock Expiry Assistant", SwingConstants.CENTER);
        version.setFont(UITheme.FONT_SUBTITLE);
        version.setForeground(UITheme.TEXT_MUTED);
        version.setHorizontalAlignment(SwingConstants.CENTER);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel desc = new JLabel("<html><table width='660' align='center' cellpadding='0' cellspacing='0'><tr>" +
                "<td align='center' style='font-family: Segoe UI; font-size: 14px; color: #475569;'>" +
                "Expiry Finder makes managing store inventory effortless, smart, and cool.<br>" +
                "Track stock batches without complicated spreadsheets, avoid customer complaints from expired goods,<br>" +
                "and minimize retail waste with automated visual alerts." +
                "</td></tr></table></html>", SwingConstants.CENTER);
        desc.setFont(UITheme.FONT_REGULAR);
        desc.setForeground(UITheme.TEXT_MAIN);
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        desc.setMaximumSize(new Dimension(680, 90));

        // Feature cards
        JPanel stepsGrid = new JPanel(new GridLayout(3, 1, 0, 12));
        stepsGrid.setOpaque(false);
        stepsGrid.setMaximumSize(new Dimension(660, 210));
        stepsGrid.setPreferredSize(new Dimension(660, 210));

        stepsGrid.add(createStepRow("1", "Add Product Batches", "Enter product name, quantity, and pick a quick expiry date (+1M, +3M, +6M)."));
        stepsGrid.add(createStepRow("2", "Check Expiry Radar", "Items are color-coded automatically: Red (Expired), Orange (<7d), Yellow (<30d), Green (Safe)."));
        stepsGrid.add(createStepRow("3", "Take Fast Action", "Discount expiring items, write off spoiled batches, or print a shelf checklist in one click."));

        stepsGrid.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Meta info badges row
        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        metaRow.setOpaque(false);
        metaRow.setMaximumSize(new Dimension(660, 32));
        metaRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        metaRow.add(createBadge("Version 1.0.0"));
        metaRow.add(createBadge("SQLite Local Storage"));
        metaRow.add(createBadge("Offline Ready"));
        metaRow.add(createBadge("Retail Grade"));

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(version);
        card.add(Box.createVerticalStrut(20));
        card.add(desc);
        card.add(Box.createVerticalStrut(22));
        card.add(stepsGrid);
        card.add(Box.createVerticalStrut(24));
        card.add(metaRow);
        card.add(Box.createVerticalGlue());

        add(card, BorderLayout.CENTER);
    }

    private JLabel createBadge(String text) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(UITheme.PRIMARY_DARK);
        badge.setOpaque(true);
        badge.setBackground(UITheme.PRIMARY_LIGHT);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.PRIMARY_BORDER, 1, true),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        return badge;
    }

    private JPanel createStepRow(String number, String stepTitle, String stepDesc) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE2E8F0), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel badge = new JLabel(number, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.PRIMARY_LIGHT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.PRIMARY_BORDER);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setPreferredSize(new Dimension(32, 32));
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(UITheme.PRIMARY_DARK);

        JPanel textBox = new JPanel(new GridLayout(2, 1, 0, 2));
        textBox.setOpaque(false);
        JLabel title = new JLabel(stepTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(UITheme.TEXT_MAIN);

        JLabel desc = new JLabel(stepDesc);
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(UITheme.TEXT_MUTED);

        textBox.add(title);
        textBox.add(desc);

        row.add(badge, BorderLayout.WEST);
        row.add(textBox, BorderLayout.CENTER);
        return row;
    }
}
