package gui.components;

import gui.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modern React-style Segmented Filter Tabs (e.g. [All] [Expired] [<7 Days] [<30 Days]).
 */
public class ModernTabFilter extends JPanel {
    public record TabItem(String key, String label) {}

    private final List<TabButton> buttons = new ArrayList<>();
    private String selectedKey;
    private final Consumer<String> onSelect;

    public ModernTabFilter(List<TabItem> items, Consumer<String> onSelect) {
        this.onSelect = onSelect;
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        setOpaque(false);

        for (int i = 0; i < items.size(); i++) {
            TabItem item = items.get(i);
            TabButton btn = new TabButton(item.label(), item.key());
            buttons.add(btn);
            add(btn);
            if (i == 0) {
                selectedKey = item.key();
                btn.setActive(true);
            }
        }
    }

    public void selectKey(String key) {
        this.selectedKey = key;
        for (TabButton b : buttons) {
            b.setActive(b.key.equals(key));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0xF1F5F9));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        g2.dispose();
        super.paintComponent(g);
    }

    private class TabButton extends JButton {
        private final String key;
        private boolean isActive = false;

        TabButton(String text, String key) {
            super(text);
            this.key = key;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(UITheme.FONT_LABEL);
            setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

            addActionListener(e -> {
                selectKey(key);
                if (onSelect != null) {
                    onSelect.accept(key);
                }
            });
        }

        void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isActive) {
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                g2.setColor(new Color(0xE2E8F0));
                g2.setStroke(new BasicStroke(1.0f));
                g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                setForeground(UITheme.PRIMARY_DARK);
            } else {
                setForeground(UITheme.TEXT_MUTED);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}
