package gui;

import database.Database;
import gui.components.ModernButton;
import gui.components.ModernCard;
import gui.components.ModernTabFilter;
import gui.components.StatCard;
import gui.components.VectorIcon;
import model.DashboardStats;
import model.StockItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpiryAlertsPanel extends JPanel {
    private final ViewNavigator navigator;

    private StatCard cardExpired;
    private StatCard cardCritical;
    private StatCard cardWarning;
    private StatCard cardGood;

    private JTable alertTable;
    private DefaultTableModel tableModel;
    private List<StockItemDTO> currentList;

    private String activeFilter = "ALL_ALERT";

    public ExpiryAlertsPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 16));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 16));
        headerPanel.setBackground(UITheme.MAIN_BG);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setBackground(UITheme.MAIN_BG);
        JLabel titleLabel = new JLabel("Expiry Alerts & Radar");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Protect your customers and reduce inventory waste by acting on expiring items");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        // --- 4 React-style Stat Cards ---
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 14, 0));
        statsGrid.setOpaque(false);

        cardExpired = new StatCard("Expired", "0", "Remove from shelves", "🚨", UITheme.EXPIRED_BG, UITheme.EXPIRED_FG);
        cardCritical = new StatCard("Critical (<7d)", "0", "Discount or prioritize", "⚡", UITheme.CRITICAL_BG, UITheme.CRITICAL_FG);
        cardWarning = new StatCard("Expiring (<30d)", "0", "Plan promotions", "⚠️", UITheme.WARNING_BG, UITheme.WARNING_FG);
        cardGood = new StatCard("Fresh Stock", "0", "Safe for normal sales", "🌿", UITheme.GOOD_BG, UITheme.GOOD_FG);

        statsGrid.add(cardExpired);
        statsGrid.add(cardCritical);
        statsGrid.add(cardWarning);
        statsGrid.add(cardGood);

        // --- Segmented Pill Tab Filter ---
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterRow.setOpaque(false);

        List<ModernTabFilter.TabItem> tabs = List.of(
                new ModernTabFilter.TabItem("ALL_ALERT", "All Urgent Batches"),
                new ModernTabFilter.TabItem("EXPIRED", "Expired"),
                new ModernTabFilter.TabItem("7DAYS", "Expiring in 7 Days"),
                new ModernTabFilter.TabItem("30DAYS", "Expiring in 30 Days")
        );

        ModernTabFilter tabFilter = new ModernTabFilter(tabs, key -> {
            activeFilter = key;
            applyFilter();
        });
        filterRow.add(tabFilter);

        headerPanel.add(titleBox, BorderLayout.NORTH);
        headerPanel.add(statsGrid, BorderLayout.CENTER);
        headerPanel.add(filterRow, BorderLayout.SOUTH);

        // --- Table Card ---
        ModernCard tableCard = new ModernCard(20);
        tableCard.setLayout(new BorderLayout(0, 12));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // Note: ID column is intentionally removed for user simplicity!
        String[] columns = {
                "Product Name", "Category", "Batch #",
                "Quantity", "Expiry Date", "Days Remaining",
                "Status", "Potential Waste"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        alertTable = new JTable(tableModel);
        UITheme.styleTable(alertTable);

        alertTable.getColumnModel().getColumn(0).setPreferredWidth(230); // Product Name
        alertTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Category
        alertTable.getColumnModel().getColumn(2).setPreferredWidth(90);  // Batch #
        alertTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Quantity
        alertTable.getColumnModel().getColumn(4).setPreferredWidth(110); // Expiry Date
        alertTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Days Remaining
        alertTable.getColumnModel().getColumn(6).setPreferredWidth(140); // Status (Badge)
        alertTable.getColumnModel().getColumn(7).setPreferredWidth(110); // Potential Waste

        alertTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(alertTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xF1F5F9)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Action Toolbar
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        bottomActionPanel.setOpaque(false);

        ModernButton printListBtn = new ModernButton("Shelf Checklist", new VectorIcon(VectorIcon.Type.CHECKLIST, 13), ModernButton.Variant.SECONDARY, 12);
        printListBtn.setPreferredSize(new Dimension(165, 40));
        printListBtn.addActionListener(e -> showChecklistDialog());

        ModernButton discardBtn = new ModernButton("Write Off Selected Batch", new VectorIcon(VectorIcon.Type.TRASH, 13), ModernButton.Variant.DANGER_SOFT, 12);
        discardBtn.setPreferredSize(new Dimension(230, 40));
        discardBtn.addActionListener(e -> discardSelectedBatch());

        bottomActionPanel.add(printListBtn);
        bottomActionPanel.add(discardBtn);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(bottomActionPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    public void loadData() {
        DashboardStats stats = Database.getDashboardStats();
        cardExpired.setValue(String.valueOf(stats.getExpiredBatches()));
        cardCritical.setValue(String.valueOf(stats.getExpiringWithin7Days()));
        cardWarning.setValue(String.valueOf(stats.getExpiringWithin30Days()));

        int totalBatches = Database.getAllStocks().size();
        int safeCount = Math.max(0, totalBatches - stats.getExpiredBatches() - stats.getExpiringWithin7Days() - stats.getExpiringWithin30Days());
        cardGood.setValue(String.valueOf(safeCount));

        applyFilter();
    }

    private void applyFilter() {
        List<StockItemDTO> all = Database.getAllStockItems();
        tableModel.setRowCount(0);

        currentList = all.stream().filter(item -> {
            long days = item.getDaysUntilExpiry();
            if ("EXPIRED".equals(activeFilter)) {
                return days < 0;
            } else if ("7DAYS".equals(activeFilter)) {
                return days >= 0 && days <= 7;
            } else if ("30DAYS".equals(activeFilter)) {
                return days >= 0 && days <= 30;
            } else { // ALL_ALERT
                return days <= 30;
            }
        }).toList();

        for (StockItemDTO item : currentList) {
            long days = item.getDaysUntilExpiry();
            String daysStr = days < 0 ? ("Expired " + Math.abs(days) + "d ago") : (days + " days left");

            tableModel.addRow(new Object[]{
                    item.getProductName(),
                    item.getCategoryName(),
                    "#" + item.getBatchNo(),
                    item.getQuantity() + " units",
                    item.getExpiryDateFormatted(),
                    daysStr,
                    item.getStatus(),
                    UITheme.formatCurrency(item.getTotalValue())
            });
        }
    }

    private void discardSelectedBatch() {
        int selectedRow = alertTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expiring or expired batch from the table.",
                    "Select Batch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Write Off Batch Confirmation\n\n" +
                        "Product: " + item.getProductName() + "\n" +
                        "Batch #: " + item.getBatchNo() + "\n" +
                        "Quantity: " + item.getQuantity() + " units\n" +
                        "Status: " + item.getStatus() + "\n\n" +
                        "Remove this batch from inventory?",
                "Write Off Stock", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteStock(item.getStockId())) {
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove batch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChecklistDialog() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expiring products found right now!", "All Good", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          EXPIRY FINDER - SHELF CHECKLIST           \n");
        sb.append("====================================================\n");
        sb.append(String.format("Generated: %s | Total Items: %d\n\n", java.time.LocalDate.now(), currentList.size()));

        for (StockItemDTO item : currentList) {
            sb.append(String.format("• %s\n", item.getProductName()));
            sb.append(String.format("   Department: %s | Batch #%d\n", item.getCategoryName(), item.getBatchNo()));
            sb.append(String.format("   Quantity: %d units | Expiry: %s (%s)\n",
                    item.getQuantity(), item.getExpiryDateFormatted(),
                    item.getDaysUntilExpiry() < 0 ? "EXPIRED" : item.getDaysUntilExpiry() + " days left"));
            sb.append("   -------------------------------------------------\n");
        }

        JTextArea textArea = new JTextArea(sb.toString(), 20, 42);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scroll, "Store Shelf Checklist", JOptionPane.PLAIN_MESSAGE);
    }
}
