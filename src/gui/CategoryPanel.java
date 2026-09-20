package gui;

import database.Database;
import gui.components.ModernButton;
import gui.components.ModernCard;
import gui.components.ModernTextField;
import gui.components.VectorIcon;
import model.Category;
import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryPanel extends JPanel {
    private final ViewNavigator navigator;

    private ModernTextField categoryNameField;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private List<Category> currentCategories;

    public CategoryPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UITheme.MAIN_BG);

        JLabel titleLabel = new JLabel("Store Categories");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Organize products into departments to make shelf inspection smooth and intuitive");
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
        ModernCard addCard = new ModernCard(20);
        addCard.setLayout(new GridBagLayout());
        addCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.insets = new Insets(8, 5, 8, 5);
        fgbc.weightx = 1.0;

        JLabel addTitle = new JLabel("New Category");
        addTitle.setFont(UITheme.FONT_SECTION);
        addTitle.setForeground(UITheme.PRIMARY_DARK);
        fgbc.gridx = 0; fgbc.gridy = 0;
        addCard.add(addTitle, fgbc);

        JLabel catNameLbl = new JLabel("Category Name");
        catNameLbl.setFont(UITheme.FONT_LABEL);
        catNameLbl.setForeground(UITheme.TEXT_MAIN);
        fgbc.gridy = 1;
        addCard.add(catNameLbl, fgbc);

        categoryNameField = new ModernTextField("e.g. Beverages & Juices");
        categoryNameField.setPreferredSize(new Dimension(0, 42));
        fgbc.gridy = 2;
        addCard.add(categoryNameField, fgbc);

        ModernButton saveCatBtn = new ModernButton("Add Category", new VectorIcon(VectorIcon.Type.PLUS, 14), ModernButton.Variant.PRIMARY, 14);
        saveCatBtn.setPreferredSize(new Dimension(0, 44));
        saveCatBtn.addActionListener(e -> addCategory());
        fgbc.gridy = 3;
        fgbc.insets = new Insets(16, 5, 8, 5);
        addCard.add(saveCatBtn, fgbc);

        fgbc.gridy = 4;
        fgbc.weighty = 1.0;
        addCard.add(Box.createVerticalGlue(), fgbc);

        splitPanel.add(addCard, gbc);

        // Right Side: Table of Categories
        gbc.gridx = 1; gbc.weightx = 0.65;
        ModernCard tableCard = new ModernCard(20);
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tableTitle = new JLabel("Categories List");
        tableTitle.setFont(UITheme.FONT_SECTION);
        tableTitle.setForeground(UITheme.TEXT_MAIN);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        // Note: Category ID column is intentionally removed!
        String[] columns = {"Category Name", "Products In Department"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        UITheme.styleTable(categoryTable);
        categoryTable.getColumnModel().getColumn(0).setPreferredWidth(280);
        categoryTable.getColumnModel().getColumn(1).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xF1F5F9)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Action Toolbar
        JPanel tableActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tableActions.setOpaque(false);

        ModernButton renameBtn = new ModernButton("Rename", new VectorIcon(VectorIcon.Type.EDIT, 13), ModernButton.Variant.OUTLINE, 12);
        renameBtn.setPreferredSize(new Dimension(120, 36));
        renameBtn.addActionListener(e -> renameSelectedCategory());

        ModernButton deleteCatBtn = new ModernButton("Delete", new VectorIcon(VectorIcon.Type.TRASH, 13), ModernButton.Variant.DANGER_SOFT, 12);
        deleteCatBtn.setPreferredSize(new Dimension(110, 36));
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
            navigator.refreshAll();
        } else {
            JOptionPane.showMessageDialog(this, "Category '" + name + "' already exists.", "Duplicate Category", JOptionPane.WARNING_MESSAGE);
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
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Could not rename category.", "Error", JOptionPane.ERROR_MESSAGE);
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
                "Delete category '" + selected.getName() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteCategory(selected.getId())) {
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
