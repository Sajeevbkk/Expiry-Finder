package gui;

import database.Database;
import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import gui.modified.RoundedTextField;
import model.Category;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryPanel extends JPanel {
    private final ViewNavigator navigator;

    private RoundedTextField categoryNameField;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private List<Category> currentCategories;

    public CategoryPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 15));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UITheme.MAIN_BG);

        JLabel titleLabel = new JLabel("Category Management");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Organize products by category and department to simplify expiry and inventory checks");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // --- Main Content: Left Form, Right Table ---
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 8, 0, 8);
        gbc.weighty = 1.0;

        // Left Side: Add Category Card
        gbc.gridx = 0; gbc.weightx = 0.35;
        RoundedPanel addCard = new RoundedPanel(20, Color.WHITE);
        addCard.setLayout(new GridBagLayout());
        addCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(8, 5, 8, 5);
        fgbc.weightx = 1.0;

        JLabel addTitle = new JLabel("Add New Category");
        addTitle.setFont(UITheme.FONT_SECTION);
        addTitle.setForeground(UITheme.PRIMARY);
        fgbc.gridx = 0; fgbc.gridy = 0;
        addCard.add(addTitle, fgbc);

        JLabel catNameLbl = new JLabel("Category Name:");
        catNameLbl.setFont(UITheme.FONT_LABEL);
        fgbc.gridy = 1;
        addCard.add(catNameLbl, fgbc);

        categoryNameField = new RoundedTextField(12);
        categoryNameField.setPreferredSize(new Dimension(0, 42));
        categoryNameField.setFont(UITheme.FONT_REGULAR);
        fgbc.gridy = 2;
        addCard.add(categoryNameField, fgbc);

        RoundedButton saveCatBtn = new RoundedButton("➕ Save Category", 14, UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        saveCatBtn.setForeground(Color.WHITE);
        saveCatBtn.setFont(UITheme.FONT_BUTTON);
        saveCatBtn.setPreferredSize(new Dimension(0, 44));
        saveCatBtn.addActionListener(e -> addCategory());
        fgbc.gridy = 3;
        fgbc.insets = new Insets(16, 5, 8, 5);
        addCard.add(saveCatBtn, fgbc);

        // Spacer to push content up
        fgbc.gridy = 4;
        fgbc.weighty = 1.0;
        addCard.add(Box.createVerticalGlue(), fgbc);

        splitPanel.add(addCard, gbc);

        // Right Side: Table of Categories
        gbc.gridx = 1; gbc.weightx = 0.65;
        RoundedPanel tableCard = new RoundedPanel(20, Color.WHITE);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Existing Categories");
        tableTitle.setFont(UITheme.FONT_SECTION);
        tableTitle.setForeground(UITheme.TEXT_DARK);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Category ID", "Category Name", "Products Registered"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        UITheme.styleTable(categoryTable);
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        categoryTable.getColumnModel().getColumn(2).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xEEEEEE)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Action Toolbar below table
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tableActions.setOpaque(false);

        RoundedButton renameBtn = new RoundedButton("✏️ Rename", 12, new Color(0x3B82F6), new Color(0x2563EB));
        renameBtn.setForeground(Color.WHITE);
        renameBtn.setFont(UITheme.FONT_BUTTON);
        renameBtn.setPreferredSize(new Dimension(120, 36));
        renameBtn.addActionListener(e -> renameSelectedCategory());

        RoundedButton deleteCatBtn = new RoundedButton("🗑️ Delete", 12, new Color(0xEF4444), new Color(0xDC2626));
        deleteCatBtn.setForeground(Color.WHITE);
        deleteCatBtn.setFont(UITheme.FONT_BUTTON);
        deleteCatBtn.setPreferredSize(new Dimension(120, 36));
        deleteCatBtn.addActionListener(e -> deleteSelectedCategory());

        tableActions.add(renameBtn);
        tableActions.add(deleteCatBtn);
        tableCard.add(tableActions, BorderLayout.SOUTH);

        splitPanel.add(tableCard, gbc);

        add(headerPanel, BorderLayout.NORTH);
        add(splitPanel, BorderLayout.CENTER);
    }

    public void loadData() {
        currentCategories = Database.getAllCategories();
        tableModel.setRowCount(0);

        List<Product> allProducts = Database.getAllProducts();

        for (Category c : currentCategories) {
            long count = allProducts.stream().filter(p -> p.getCategoryID() == c.getId()).count();
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    count + " product(s)"
            });
        }
    }

    private void addCategory() {
        String name = categoryNameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category c = new Category(name);
        if (Database.insertCategory(c)) {
            categoryNameField.setText("");
            JOptionPane.showMessageDialog(this, "Category '" + name + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            navigator.refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add category. It may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renameSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to rename.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category selected = currentCategories.get(row);
        String newName = JOptionPane.showInputDialog(this, "Enter new name for category:", selected.getName());
        if (newName != null && !newName.trim().isEmpty() && !newName.trim().equalsIgnoreCase(selected.getName())) {
            selected.setName(newName.trim());
            if (Database.updateCategory(selected)) {
                JOptionPane.showMessageDialog(this, "Category renamed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to rename category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Category selected = currentCategories.get(row);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete category: '" + selected.getName() + "'?\n" +
                        "(Products in this category will become Uncategorized)",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteCategory(selected.getId())) {
                JOptionPane.showMessageDialog(this, "Category deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
