package gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;

public class App extends JFrame {
    public App() {
        setTitle("Expiry Finder");
        setSize(400, 300);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Welcome to Expiry Finder!", JLabel.CENTER);
        label.setFont(new Font("MV Boli", Font.BOLD, 64));
        label.setForeground(Color.green);
        add(label);

        setVisible(true);
    }
}