package gui;

import database.Database;
import gui.components.ModernButton;
import gui.components.ModernCard;
import gui.components.ModernTextField;
import gui.components.VectorIcon;
import model.Category;
import model.StockItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class InventoryPanel extends JPanel {
    private final ViewNavigator navigator;

    private ModernTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> statusFilterCombo;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private List<StockItemDTO> currentList;

    private JLabel summaryLabel;

    public InventoryPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header & Filters ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 14));
        topPanel.setBackground(UITheme.MAIN_BG);

        // Title
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setBackground(UITheme.MAIN_BG);
        JLabel titleLabel = new JLabel("Product Inventory");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Browse all stock batches, monitor quantities, and stay ahead of expiration dates");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        // Filter Bar (Modern Card)
        ModernCard filterCard = new ModernCard(18);
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 12));

        searchField = new ModernTextField("Search products or batch #...");
        searchField.setPreferredSize(new Dimension(180, 40));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.setPreferredSize(new Dimension(140, 40));
        categoryFilterCombo.setFont(UITheme.FONT_REGULAR);
        categoryFilterCombo.setBackground(Color.WHITE);
        categoryFilterCombo.addActionListener(e -> applyFilters());

        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "Expired Only", "Expiring < 7 Days", "Expiring < 30 Days", "Good"});
        statusFilterCombo.setPreferredSize(new Dimension(140, 40));
        statusFilterCombo.setFont(UITheme.FONT_REGULAR);
        statusFilterCombo.setBackground(Color.WHITE);
        statusFilterCombo.addActionListener(e -> applyFilters());

        ModernButton refreshBtn = new ModernButton("Refresh", new VectorIcon(VectorIcon.Type.REFRESH, 13), ModernButton.Variant.GHOST, 12);
        refreshBtn.setPreferredSize(new Dimension(115, 40));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            statusFilterCombo.setSelectedIndex(0);
            loadData();
        });

        ModernButton addBtn = new ModernButton("Add Stock", new VectorIcon(VectorIcon.Type.PLUS, 13), ModernButton.Variant.PRIMARY, 12);
        addBtn.setPreferredSize(new Dimension(135, 40));
        addBtn.addActionListener(e -> navigator.navigateTo("ADD"));

        filterCard.add(searchField);
        filterCard.add(categoryFilterCombo);
        filterCard.add(statusFilterCombo);
        filterCard.add(refreshBtn);
        filterCard.add(addBtn);

        topPanel.add(titleBox, BorderLayout.NORTH);
        topPanel.add(filterCard, BorderLayout.CENTER);

        // --- Table Card ---
        ModernCard tableCard = new ModernCard(20);
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Note: ID column is intentionally completely removed for clean user experience!
        String[] columns = {
                "Product Name", "Category", "Batch #",
                "Unit Price", "Quantity", "Arrival Date",
                "Expiry Date", "Days Left", "Status", "Total Value"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        UITheme.styleTable(inventoryTable);

        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(210); // Product Name
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Category
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(80);  // Batch #
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(85);  // Unit Price
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Quantity
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Arrival Date
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Expiry Date
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Days Left
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(135); // Status (Badge)
        inventoryTable.getColumnModel().getColumn(9).setPreferredWidth(95);  // Total Value

        inventoryTable.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane tableScroll = new JScrollPane(inventoryTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xF1F5F9)));
        tableScroll.getViewport().setBackground(Color.WHITE);

        // Bottom Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        summaryLabel = new JLabel("Loading inventory...");
        summaryLabel.setFont(UITheme.FONT_LABEL);
        summaryLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel actionBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtnBox.setOpaque(false);

        ModernButton editQtyBtn = new ModernButton("Edit Quantity", new VectorIcon(VectorIcon.Type.EDIT, 13), ModernButton.Variant.OUTLINE, 12);
        editQtyBtn.setPreferredSize(new Dimension(150, 38));
        editQtyBtn.addActionListener(e -> editSelectedQuantity());

        ModernButton deleteBtn = new ModernButton("Remove Batch", new VectorIcon(VectorIcon.Type.TRASH, 13), ModernButton.Variant.DANGER_SOFT, 12);
        deleteBtn.setPreferredSize(new Dimension(155, 38));
        deleteBtn.addActionListener(e -> deleteSelectedBatch());

        actionBtnBox.add(editQtyBtn);
        actionBtnBox.add(deleteBtn);

        bottomBar.add(summaryLabel, BorderLayout.WEST);
        bottomBar.add(actionBtnBox, BorderLayout.EAST);

        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(bottomBar, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void loadCategories() {
        categoryFilterCombo.removeAllItems();
        categoryFilterCombo.addItem("All Categories");
        List<Category> categories = Database.getAllCategories();
        for (Category c : categories) {
            categoryFilterCombo.addItem(c.getName());
        }
    }

    public void loadData() {
        loadCategories();
        applyFilters();
    }

    public void applyFilters() {
        String keyword = searchField.getText().trim();
        String selectedCat = (String) categoryFilterCombo.getSelectedItem();
        int statusIndex = statusFilterCombo.getSelectedIndex();

        String statusFilter = switch (statusIndex) {
            case 1 -> "EXPIRED";
            case 2 -> "7DAYS";
            case 3 -> "30DAYS";
            case 4 -> "GOOD";
            default -> "ALL";
        };

        Long categoryId = null;
        if (selectedCat != null && !selectedCat.equals("All Categories")) {
            for (Category c : Database.getAllCategories()) {
                if (c.getName().equalsIgnoreCase(selectedCat)) {
                    categoryId = c.getId();
                    break;
                }
            }
        }

        currentList = Database.searchStockItems(keyword, categoryId, statusFilter);
        tableModel.setRowCount(0);

        int totalUnits = 0;
        double totalValue = 0;

        for (StockItemDTO item : currentList) {
            totalUnits += item.getQuantity();
            totalValue += item.getTotalValue();

            long days = item.getDaysUntilExpiry();
            String daysStr = days < 0 ? (Math.abs(days) + "d ago") : (days + "d");

            tableModel.addRow(new Object[]{
                    item.getProductName(),
                    item.getCategoryName(),
                    "#" + item.getBatchNo(),
                    UITheme.formatCurrency(item.getPrice()),
                    item.getQuantity() + " pcs",
                    item.getArrivalDateFormatted(),
                    item.getExpiryDateFormatted(),
                    daysStr,
                    item.getStatus(),
                    UITheme.formatCurrency(item.getTotalValue())
            });
        }

        summaryLabel.setText(String.format("Showing %d batches  •  %d total units in stock  •  Total Value: %s",
                currentList.size(), totalUnits, UITheme.formatCurrency(totalValue)));
    }

    private void editSelectedQuantity() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product batch from the table.", "Select Batch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        String input = JOptionPane.showInputDialog(this,
                "Enter updated quantity for " + item.getProductName() + " (Batch #" + item.getBatchNo() + "):",
                item.getQuantity());

        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(input.trim());
                if (newQty < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (Database.adjustStockQuantity(item.getStockId(), newQty)) {
                    navigator.refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, "Could not update quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBatch() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a product batch from the table to remove.", "Select Batch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove Batch #" + item.getBatchNo() + " of " + item.getProductName() + "?\n" +
                        "Quantity: " + item.getQuantity() + " units",
                "Remove Batch", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteStock(item.getStockId())) {
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove batch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
