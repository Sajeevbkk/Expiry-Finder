package gui;

import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import gui.modified.RoundedTextField;

import javax.swing.*;
import java.awt.*;

class MainPanel extends JPanel {
    MainPanel() {
        /*------------ Initial Configurations -------------------*/
        setLayout(new BorderLayout());
        setBackground(new Color(0xf4f4f4)); // light gray background for main panel
        setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        /*---------- Title Area ----------*/
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(new Color(0xf4f4f4));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Add New Product");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(0x182432));
        
        JLabel subtitleLabel = new JLabel("Enter product details to track expiry");
        subtitleLabel.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0x666666));
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        /*---------- Form Card ----------*/
        RoundedPanel formCard = new RoundedPanel(30, Color.WHITE);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weightx = 1.0;

        // Product Type
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel typeLabel = new JLabel("Product Type");
        typeLabel.setFont(new Font("Sans Serif", Font.BOLD, 20));
        formCard.add(typeLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        RoundedTextField typeField = new RoundedTextField(15);
        typeField.setText("Select product type");
        typeField.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        typeField.setPreferredSize(new Dimension(0, 45));
        formCard.add(typeField, gbc);

        // Product Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Product Name");
        nameLabel.setFont(new Font("Sans Serif", Font.BOLD, 20));
        formCard.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        RoundedTextField nameField = new RoundedTextField(15);
        nameField.setText("Enter product name");
        nameField.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        nameField.setPreferredSize(new Dimension(0, 45));
        formCard.add(nameField, gbc);

        // EAN Number
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel eanLabel = new JLabel("EAN Number");
        eanLabel.setFont(new Font("Sans Serif", Font.BOLD, 20));
        formCard.add(eanLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        RoundedTextField eanField = new RoundedTextField(15);
        eanField.setText("Enter EAN number");
        eanField.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        eanField.setPreferredSize(new Dimension(0, 45));
        formCard.add(eanField, gbc);
        
        // Expiry Date
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel dateLabel = new JLabel("Expiry Date");
        dateLabel.setFont(new Font("Sans Serif", Font.BOLD, 20));
        formCard.add(dateLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        RoundedTextField dateField = new RoundedTextField(15);
        dateField.setText("Select expiry date");
        dateField.setFont(new Font("Sans Serif", Font.PLAIN, 16));
        dateField.setPreferredSize(new Dimension(0, 45));
        formCard.add(dateField, gbc);
        
        // Submit Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(30, 10, 10, 10);
        RoundedButton submitBtn = new RoundedButton("SUBMIT", 20, new Color(0x329345), new Color(0x287A38));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Sans Serif", Font.BOLD, 20));
        submitBtn.setPreferredSize(new Dimension(0, 50));
        formCard.add(submitBtn, gbc);

        /*-------------- Final Configurations -------------------*/
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0xf4f4f4));
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(formCard, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.NORTH);
        setVisible(true);
    }
}
