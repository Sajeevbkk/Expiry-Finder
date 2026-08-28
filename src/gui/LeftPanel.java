package gui;

import javax.swing.*;
import java.awt.*;

class LeftPanel extends JPanel {
    LeftPanel() {
        ImageIcon topImage = new ImageIcon("src/assets/labelicon.png");
        ImageIcon bottomImage = new ImageIcon("src/assets/products.png");

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 100));
        setBackground(new Color(0xDAE9CA));

        JLabel top = new JLabel(new ImageIcon(
                topImage.getImage().getScaledInstance(
                        300, 96, Image.SCALE_SMOOTH
                )));
        top.setPreferredSize(new Dimension(300, 96));

        JLabel bottom = new JLabel(new ImageIcon(
                bottomImage.getImage().getScaledInstance(
                        300, 215, Image.SCALE_SMOOTH
                )));
        bottom.setPreferredSize(new Dimension(300, 215));

        add(top, BorderLayout.NORTH);
        add(bottom, BorderLayout.SOUTH);
        setVisible(true);
    }
}
