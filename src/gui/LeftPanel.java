package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class LeftPanel extends JPanel {
    private final NavBar navBar;

    public LeftPanel(ViewNavigator navigator) {
        ImageIcon topImage = new ImageIcon("src/assets/labelicon.png");
        ImageIcon bottomImage = new ImageIcon("src/assets/products.png");

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 100));
        setBackground(UITheme.SIDEBAR_BG);

        // --- Top Brand Header ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(280, 95));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        topPanel.setBackground(UITheme.SIDEBAR_BG);

        JLabel topPanelIcon = new JLabel(new ImageIcon(
                topImage.getImage().getScaledInstance(62, 62, Image.SCALE_SMOOTH)
        ));

        JPanel subTopPanel = new JPanel(new GridLayout(2, 1, 0, 0));
        subTopPanel.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 0));
        subTopPanel.setBackground(UITheme.SIDEBAR_BG);

        JLabel labelExpiry = new JLabel("EXPIRY");
        labelExpiry.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelExpiry.setForeground(new Color(0x0F172A));

        JLabel labelFinder = new JLabel("FINDER");
        labelFinder.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelFinder.setForeground(UITheme.PRIMARY_DARK);

        subTopPanel.add(labelExpiry);
        subTopPanel.add(labelFinder);

        topPanel.add(topPanelIcon, BorderLayout.WEST);
        topPanel.add(subTopPanel, BorderLayout.CENTER);

        // --- Navigation Buttons (Custom Vector Icons, No missing character boxes) ---
        navBar = new NavBar(navigator);

        // --- Bottom Illustration ---
        JLabel bottom = new JLabel(new ImageIcon(
                bottomImage.getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH)
        ));
        bottom.setPreferredSize(new Dimension(280, 200));

        add(topPanel, BorderLayout.NORTH);
        add(navBar, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    public void setActiveView(String viewName) {
        navBar.setActiveView(viewName);
    }
}

class NavBar extends JPanel {
    private final ViewNavigator navigator;
    private final List<ModernNavButton> buttons = new ArrayList<>();

    NavBar(ViewNavigator navigator) {
        this.navigator = navigator;

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        setBackground(UITheme.SIDEBAR_BG);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        ModernNavButton addBtn = new ModernNavButton("Add Stock Batch", "ADD", NavIcon.Type.ADD);
        ModernNavButton lstBtn = new ModernNavButton("Stock Inventory", "INVENTORY", NavIcon.Type.INVENTORY);
        ModernNavButton expiryBtn = new ModernNavButton("Expiry Radar", "EXPIRY", NavIcon.Type.EXPIRY);
        ModernNavButton setBtn = new ModernNavButton("Store Categories", "CATEGORIES", NavIcon.Type.CATEGORIES);
        ModernNavButton abtBtn = new ModernNavButton("About Assistant", "ABOUT", NavIcon.Type.ABOUT);

        add(addBtn);
        add(lstBtn);
        add(expiryBtn);
        add(setBtn);
        add(abtBtn);

        setActiveView("ADD");
    }

    public void setActiveView(String activeKey) {
        for (ModernNavButton btn : buttons) {
            btn.setActive(btn.getViewKey().equalsIgnoreCase(activeKey));
        }
    }

    private class ModernNavButton extends JButton {
        private final String viewKey;
        private final NavIcon navIcon;
        private boolean isActive = false;
        private boolean isHovered = false;

        ModernNavButton(String text, String viewKey, NavIcon.Type iconType) {
            super(text);
            this.viewKey = viewKey;
            this.navIcon = new NavIcon(iconType);

            setIcon(navIcon);
            setIconTextGap(14);
            setPreferredSize(new Dimension(230, 46));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });

            addActionListener(e -> {
                setActiveView(viewKey);
                navigator.navigateTo(viewKey);
            });

            buttons.add(this);
        }

        String getViewKey() {
            return viewKey;
        }

        void setActive(boolean active) {
            this.isActive = active;
            this.navIcon.setActive(active);
            if (active) {
                setForeground(Color.WHITE);
            } else {
                setForeground(UITheme.TEXT_MAIN);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (isActive) {
                // Active button pill (rich primary emerald)
                g2.setColor(UITheme.PRIMARY_DARK);
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            } else if (isHovered) {
                // Soft hover effect
                g2.setColor(new Color(0xD1FAE5));
                g2.fillRoundRect(0, 0, w - 1, h - 1, 14, 14);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class NavIcon implements Icon {
        enum Type { ADD, INVENTORY, EXPIRY, CATEGORIES, ABOUT }
        private final Type type;
        private boolean active;

        NavIcon(Type type) {
            this.type = type;
        }

        void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            Color color = active ? Color.WHITE : UITheme.PRIMARY_DARK;
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case ADD:
                    // Plus sign (+)
                    g2.drawLine(x + 2, y + 9, x + 16, y + 9);
                    g2.drawLine(x + 9, y + 2, x + 9, y + 16);
                    break;
                case INVENTORY:
                    // 4-box grid (⊞)
                    g2.drawRoundRect(x + 1, y + 1, 15, 15, 3, 3);
                    g2.drawLine(x + 8, y + 1, x + 8, y + 16);
                    g2.drawLine(x + 1, y + 8, x + 16, y + 8);
                    break;
                case EXPIRY:
                    // Warning triangle (▲)
                    int[] xPoints = {x + 8, x + 1, x + 15};
                    int[] yPoints = {y + 2, y + 16, y + 16};
                    g2.drawPolygon(xPoints, yPoints, 3);
                    g2.drawLine(x + 8, y + 7, x + 8, y + 11);
                    g2.fillRect(x + 8, y + 13, 2, 2);
                    break;
                case CATEGORIES:
                    // Tag / Diamond (🏷)
                    int[] tx = {x + 8, x + 15, x + 8, x + 1};
                    int[] ty = {y + 1, y + 8, y + 15, y + 8};
                    g2.drawPolygon(tx, ty, 4);
                    g2.fillOval(x + 7, y + 7, 3, 3);
                    break;
                case ABOUT:
                    // Circle Info (ⓘ)
                    g2.drawOval(x + 1, y + 1, 15, 15);
                    g2.fillOval(x + 8, y + 4, 2, 2);
                    g2.drawLine(x + 8, y + 8, x + 8, y + 13);
                    break;
            }
            g2.dispose();
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }
    }
}
