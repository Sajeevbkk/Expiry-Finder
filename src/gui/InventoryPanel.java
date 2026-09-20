package gui;

import database.Database;
import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import gui.modified.RoundedTextField;
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

    private RoundedTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private JComboBox<String> statusFilterCombo;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private List<StockItemDTO> currentList;

    private JLabel summaryLabel;

    public InventoryPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 15));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Top Title & Control Area ---
        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setBackground(UITheme.MAIN_BG);

        // Title
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setBackground(UITheme.MAIN_BG);
        JLabel titleLabel = new JLabel("Product & Stock Inventory");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Track all product batches, adjust stock levels, and monitor expiration dates");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        // Filter Bar (Card)
        RoundedPanel filterCard = new RoundedPanel(18, Color.WHITE);
        filterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 12));

        JLabel searchLbl = new JLabel("🔍 Search:");
        searchLbl.setFont(UITheme.FONT_LABEL);
        searchField = new RoundedTextField(12);
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setFont(UITheme.FONT_REGULAR);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(UITheme.FONT_LABEL);
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.setPreferredSize(new Dimension(170, 36));
        categoryFilterCombo.setFont(UITheme.FONT_REGULAR);
        categoryFilterCombo.addActionListener(e -> applyFilters());

        JLabel statusLbl = new JLabel("Status:");
        statusLbl.setFont(UITheme.FONT_LABEL);
        statusFilterCombo = new JComboBox<>(new String[]{"ALL", "EXPIRED", "7DAYS", "30DAYS", "GOOD"});
        statusFilterCombo.setPreferredSize(new Dimension(140, 36));
        statusFilterCombo.setFont(UITheme.FONT_REGULAR);
        statusFilterCombo.addActionListener(e -> applyFilters());

        RoundedButton refreshBtn = new RoundedButton("↻ Refresh", 12, UITheme.PRIMARY_LIGHT, UITheme.PRIMARY_HOVER);
        refreshBtn.setForeground(UITheme.PRIMARY_DARK);
        refreshBtn.setFont(UITheme.FONT_BUTTON);
        refreshBtn.setPreferredSize(new Dimension(100, 36));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            categoryFilterCombo.setSelectedIndex(0);
            statusFilterCombo.setSelectedIndex(0);
            loadData();
        });

        RoundedButton addBtn = new RoundedButton("➕ Add Stock", 12, UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(UITheme.FONT_BUTTON);
        addBtn.setPreferredSize(new Dimension(120, 36));
        addBtn.addActionListener(e -> navigator.navigateTo("ADD"));

        filterCard.add(searchLbl);
        filterCard.add(searchField);
        filterCard.add(catLbl);
        filterCard.add(categoryFilterCombo);
        filterCard.add(statusLbl);
        filterCard.add(statusFilterCombo);
        filterCard.add(refreshBtn);
        filterCard.add(addBtn);

        topPanel.add(titleBox, BorderLayout.NORTH);
        topPanel.add(filterCard, BorderLayout.CENTER);

        // --- Data Table Card ---
        RoundedPanel tableCard = new RoundedPanel(20, Color.WHITE);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {
                "Stock ID", "Product Name", "Category", "Barcode",
                "Batch #", "Unit Price", "Quantity", "Arrival Date",
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

        // Set column widths & renderers
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(180);  // Name
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(120);  // Category
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(120);  // Barcode
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(70);   // Batch
        inventoryTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Price
        inventoryTable.getColumnModel().getColumn(6).setPreferredWidth(70);   // Qty
        inventoryTable.getColumnModel().getColumn(7).setPreferredWidth(95);   // Arrival
        inventoryTable.getColumnModel().getColumn(8).setPreferredWidth(95);   // Expiry
        inventoryTable.getColumnModel().getColumn(9).setPreferredWidth(80);   // Days Left
        inventoryTable.getColumnModel().getColumn(10).setPreferredWidth(120); // Status
        inventoryTable.getColumnModel().getColumn(11).setPreferredWidth(90);  // Total Value

        inventoryTable.getColumnModel().getColumn(10).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane tableScroll = new JScrollPane(inventoryTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        tableScroll.getViewport().setBackground(Color.WHITE);

        // Bottom Action Controls
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        summaryLabel = new JLabel("Loading inventory...");
        summaryLabel.setFont(UITheme.FONT_LABEL);
        summaryLabel.setForeground(UITheme.TEXT_MUTED);

        JPanel actionBtnBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBtnBox.setOpaque(false);

        RoundedButton editQtyBtn = new RoundedButton("✏️ Edit Quantity", 12, new Color(0x3B82F6), new Color(0x2563EB));
        editQtyBtn.setForeground(Color.WHITE);
        editQtyBtn.setFont(UITheme.FONT_BUTTON);
        editQtyBtn.setPreferredSize(new Dimension(140, 36));
        editQtyBtn.addActionListener(e -> editSelectedQuantity());

        RoundedButton deleteBtn = new RoundedButton("🗑️ Remove Batch", 12, new Color(0xEF4444), new Color(0xDC2626));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFont(UITheme.FONT_BUTTON);
        deleteBtn.setPreferredSize(new Dimension(140, 36));
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
        String selectedCatName = (String) categoryFilterCombo.getSelectedItem();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();

        Long categoryId = null;
        if (selectedCatName != null && !selectedCatName.equals("All Categories")) {
            List<Category> allCats = Database.getAllCategories();
            for (Category c : allCats) {
                if (c.getName().equalsIgnoreCase(selectedCatName)) {
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
                    item.getStockId(),
                    item.getProductName(),
                    item.getCategoryName(),
                    item.getBarcode(),
                    item.getBatchNo(),
                    String.format("$%.2f", item.getPrice()),
                    item.getQuantity(),
                    item.getArrivalDateFormatted(),
                    item.getExpiryDateFormatted(),
                    daysStr,
                    item.getStatus(),
                    String.format("$%.2f", item.getTotalValue())
            });
        }

        summaryLabel.setText(String.format("Showing %d batches | %d total units | Total Inventory Value: $%.2f",
                currentList.size(), totalUnits, totalValue));
    }

    private void editSelectedQuantity() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a stock batch from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        String input = JOptionPane.showInputDialog(this,
                "Enter new quantity for: " + item.getProductName() + " (Batch #" + item.getBatchNo() + ")\nCurrent Quantity: " + item.getQuantity(),
                item.getQuantity());

        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(input.trim());
                if (newQty < 0) {
                    JOptionPane.showMessageDialog(this, "Quantity cannot be negative.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (Database.adjustStockQuantity(item.getStockId(), newQty)) {
                    JOptionPane.showMessageDialog(this, "Quantity updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    navigator.refreshAll();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer quantity.", "Invalid Number", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedBatch() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a stock batch from the table to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove Stock Batch #" + item.getBatchNo() + " for '" + item.getProductName() + "'?\n" +
                        "Quantity: " + item.getQuantity() + " units (Value: $" + String.format("%.2f", item.getTotalValue()) + ")",
                "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteStock(item.getStockId())) {
                JOptionPane.showMessageDialog(this, "Batch removed successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove batch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
