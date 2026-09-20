package gui.components;

import gui.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Modern React-style KPI Stat Card component.
 */
public class StatCard extends ModernCard {
    private final JLabel valueLabel;
    private final JLabel titleLabel;
    private final JLabel noteLabel;
    private final JLabel iconLabel;
    private final Color accentColor;

    public StatCard(String title, String initialValue, String note, String emojiIcon, Color accentBg, Color accentFg) {
        super(18, Color.WHITE, UITheme.CARD_BORDER);
        this.accentColor = accentFg;

        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        // Top Row: Title + Icon Pill
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(UITheme.TEXT_MUTED);

        iconLabel = new JLabel(emojiIcon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(accentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLabel.setPreferredSize(new Dimension(32, 32));
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));

        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(iconLabel, BorderLayout.EAST);

        // Middle: Large Metric Value
        valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentFg);

        // Bottom: Note
        noteLabel = new JLabel(note);
        noteLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        noteLabel.setForeground(UITheme.TEXT_MUTED);

        add(topRow, BorderLayout.NORTH);
        add(valueLabel, BorderLayout.CENTER);
        add(noteLabel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }
}
