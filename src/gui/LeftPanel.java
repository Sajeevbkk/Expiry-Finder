package gui;

import javax.swing.*;
import java.awt.*;

class LeftPanel extends JPanel {
    LeftPanel() {
        ImageIcon topImage = new ImageIcon("src/assets/labelicon.png");
        ImageIcon bottomImage = new ImageIcon("src/assets/products.png");

        /* ------------ Initial Configurations of LeftPanel -------------- */
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 100));
        setBackground(new Color(0xDAE9CA));

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
        NavBar navBar = new NavBar();

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
}

class NavBar extends JPanel {
    JButton addBtn, lstBtn, barcodeBtn,
        expiryBtn, hisBtn, setBtn, abtBtn;

    NavBar() {
        ImageIcon addIcon = new ImageIcon("src/assets/addIcon.png");

        setLayout(new FlowLayout());
        setBackground(new Color(0xDAE9CA));
        setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        addBtn = new JButton("Add Product");
        addBtn.setIcon(addIcon);
        addBtn.setFocusPainted(false);
        addBtn.setFont(new Font("Sans Serif", Font.BOLD, 25));
        addBtn.setBackground(new Color(0xDAE9CA));

        //TODO: Implement balance buttons

        add(addBtn);
    }
}
