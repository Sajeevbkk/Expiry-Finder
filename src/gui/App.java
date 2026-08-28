package gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    public App() {
        ImageIcon icon = new ImageIcon("src/assets/icon.png");

        setTitle("Expiry Finder");
        setIconImage(icon.getImage());
        setSize(400, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        LeftPanel leftPanel = new LeftPanel();

        JPanel centrePanel = new JPanel();
        centrePanel.setBackground(Color.BLACK);

        add(leftPanel, BorderLayout.WEST);
        add(centrePanel, BorderLayout.CENTER);
        setVisible(true);
    }
}