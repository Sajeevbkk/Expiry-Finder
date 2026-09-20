package gui;

import gui.modified.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeftPanel extends JPanel {
    private final NavBar navBar;

    public LeftPanel(ViewNavigator navigator) {
        ImageIcon topImage = new ImageIcon("src/assets/labelicon.png");
        ImageIcon bottomImage = new ImageIcon("src/assets/products.png");

        /* ------------ Initial Configurations of LeftPanel -------------- */
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 100));
        setBackground(new Color(0xF5F8F5));

        /* -------------- Components ------------------ */
        /* Top */
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(300, 100));
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        topPanel.setBackground(new Color(0xDAE9CA));

        JLabel topPanelIcon = new JLabel(new ImageIcon(
                topImage.getImage().getScaledInstance(
                        70, 70, Image.SCALE_SMOOTH
                )));

        /* Right Side Text section of Top Panel */
        JPanel subTopPanel = new JPanel(new GridLayout(2, 1, 50, 0));
        subTopPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 13, 0));
        subTopPanel.setBackground(new Color(0xDAE9CA));

        JLabel labelExpiry = new JLabel("EXPIRY");
        labelExpiry.setPreferredSize(new Dimension(100, 30));
        labelExpiry.setFont(new Font("Sans Serif", Font.BOLD, 30));
        labelExpiry.setForeground(new Color(0x182432));

        JLabel labelFinder = new JLabel("FINDER");
        labelFinder.setPreferredSize(new Dimension(100, 30));
        labelFinder.setFont(new Font("Sans Serif", Font.BOLD, 30));
        labelFinder.setForeground(new Color(0x299645));

        subTopPanel.add(labelExpiry);
        subTopPanel.add(labelFinder);

        topPanel.add(topPanelIcon, BorderLayout.WEST);
        topPanel.add(subTopPanel, BorderLayout.CENTER);

        /* Navbar (Main Left Panel) */
        navBar = new NavBar(navigator);

        /* Bottom */
        JLabel bottom = new JLabel(new ImageIcon(
                bottomImage.getImage().getScaledInstance(
                        300, 215, Image.SCALE_SMOOTH
                )));
        bottom.setPreferredSize(new Dimension(300, 215));

        /* -------------- Final Configurations of LeftPanel --------------- */
        add(topPanel, BorderLayout.NORTH);
        add(navBar, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }

    public void setActiveView(String viewName) {
        navBar.setActiveView(viewName);
    }
}

class NavBar extends JPanel {
    private final ViewNavigator navigator;
    private final List<NavButtonWrapper> buttons = new ArrayList<>();

    private record NavButtonWrapper(RoundedButton button, String viewName) {}

    NavBar(ViewNavigator navigator) {
        this.navigator = navigator;

        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        setBackground(new Color(0xDAE9CA));
        setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        ImageIcon addIcon = new ImageIcon("src/assets/addIcon.png");

        RoundedButton addBtn = createNavButton("Add Product", "ADD");
        if (addIcon.getImage() != null) {
            addBtn.setIcon(addIcon);
        }

        RoundedButton lstBtn = createNavButton("📦  Inventory", "INVENTORY");
        RoundedButton expiryBtn = createNavButton("⚠️  Expiry Tracker", "EXPIRY");
        RoundedButton barcodeBtn = createNavButton("🔍  Barcode Scan", "BARCODE");
        RoundedButton setBtn = createNavButton("🏷️  Categories", "CATEGORIES");
        RoundedButton abtBtn = createNavButton("ℹ️  About", "ABOUT");

        add(addBtn);
        add(lstBtn);
        add(expiryBtn);
        add(barcodeBtn);
        add(setBtn);
        add(abtBtn);

        // Highlight initial active
        setActiveView("ADD");
    }

    private RoundedButton createNavButton(String text, String viewKey) {
        RoundedButton btn = new RoundedButton(text, 15, new Color(0xE4F1E8), new Color(0xD2E8D8));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(new Color(0x182432));
        btn.setPreferredSize(new Dimension(240, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setIconTextGap(15);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addActionListener(e -> {
            setActiveView(viewKey);
            navigator.navigateTo(viewKey);
        });

        buttons.add(new NavButtonWrapper(btn, viewKey));
        return btn;
    }

    public void setActiveView(String activeKey) {
        for (NavButtonWrapper wrapper : buttons) {
            RoundedButton btn = wrapper.button();
            if (wrapper.viewName().equalsIgnoreCase(activeKey)) {
                btn.setBackground(new Color(0x287A38));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(0xE4F1E8));
                btn.setForeground(new Color(0x182432));
            }
            btn.repaint();
        }
    }
}
