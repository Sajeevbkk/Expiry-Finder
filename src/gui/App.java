package gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    public App() {
        ImageIcon icon = new ImageIcon("src/assets/icon.png");

        /*---------------- Initial Configurations -----------------*/
        setTitle("Expiry Finder");
        setIconImage(icon.getImage());
        setSize(400, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /*-----Components--------*/
        LeftPanel leftPanel = new LeftPanel(); // This side panel contains Navigation
        JPanel mainPanel = new MainPanel();

        /*---------------- Final Configurations -----------------*/
        add(leftPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }
}