package gui;

import database.Database;
import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import model.DashboardStats;
import model.StockItemDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ExpiryAlertsPanel extends JPanel {
    private final ViewNavigator navigator;

    private JLabel expiredCountLabel;
    private JLabel criticalCountLabel;
    private JLabel warningCountLabel;
    private JLabel goodCountLabel;

    private JTable alertTable;
    private DefaultTableModel tableModel;
    private List<StockItemDTO> currentList;

    private String activeFilter = "ALL_ALERT"; // ALL_ALERT, EXPIRED, 7DAYS, 30DAYS

    private JButton btnAll;
    private JButton btnExpired;
    private JButton btn7Days;
    private JButton btn30Days;

    public ExpiryAlertsPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 15));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        initUI();
        loadData();
    }

    private void initUI() {
        // --- Header Area ---
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setBackground(UITheme.MAIN_BG);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 4));
        titleBox.setBackground(UITheme.MAIN_BG);
        JLabel titleLabel = new JLabel("Expiry Tracker & Alerts");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Protect customers and prevent waste by acting on products nearing expiration");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        // --- Metric Stat Cards ---
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statsGrid.setOpaque(false);

        statsGrid.add(createStatCard("🚨 EXPIRED", expiredCountLabel = new JLabel("0"),
                "Needs immediate disposal", UITheme.EXPIRED_BG, UITheme.EXPIRED_FG));
        statsGrid.add(createStatCard("⚡ CRITICAL (<7 DAYS)", criticalCountLabel = new JLabel("0"),
                "Discount or front shelf", UITheme.CRITICAL_BG, UITheme.CRITICAL_FG));
        statsGrid.add(createStatCard("⚠️ EXPIRING (<30 DAYS)", warningCountLabel = new JLabel("0"),
                "Plan sales promotion", UITheme.WARNING_BG, UITheme.WARNING_FG));
        statsGrid.add(createStatCard("🌿 SAFE STOCK", goodCountLabel = new JLabel("0"),
                "Normal shelf life", UITheme.GOOD_BG, UITheme.GOOD_FG));

        // --- Filter Buttons Row ---
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);

        btnAll = createFilterButton("All Urgent Batches", "ALL_ALERT");
        btnExpired = createFilterButton("🚨 Expired Batches", "EXPIRED");
        btn7Days = createFilterButton("⚡ Expiring in 7 Days", "7DAYS");
        btn30Days = createFilterButton("⚠️ Expiring in 30 Days", "30DAYS");

        filterRow.add(btnAll);
        filterRow.add(btnExpired);
        filterRow.add(btn7Days);
        filterRow.add(btn30Days);

        updateFilterButtonStyles();

        headerPanel.add(titleBox, BorderLayout.NORTH);
        headerPanel.add(statsGrid, BorderLayout.CENTER);
        headerPanel.add(filterRow, BorderLayout.SOUTH);

        // --- Table Card ---
        RoundedPanel tableCard = new RoundedPanel(20, Color.WHITE);
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columns = {
                "Stock ID", "Product Name", "Category", "Barcode",
                "Batch #", "Quantity", "Expiry Date", "Days Remaining",
                "Action Status", "Potential Waste ($)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        alertTable = new JTable(tableModel);
        UITheme.styleTable(alertTable);

        alertTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        alertTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        alertTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        alertTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        alertTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        alertTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        alertTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        alertTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        alertTable.getColumnModel().getColumn(8).setPreferredWidth(130);
        alertTable.getColumnModel().getColumn(9).setPreferredWidth(100);

        alertTable.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(alertTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xEAEAEA)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Action Toolbar
        JPanel bottomActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomActionPanel.setOpaque(false);

        RoundedButton discardBtn = new RoundedButton("🗑️ Discard / Write Off Selected Batch", 12,
                new Color(0xDC2626), new Color(0xB91C1C));
        discardBtn.setForeground(Color.WHITE);
        discardBtn.setFont(UITheme.FONT_BUTTON);
        discardBtn.setPreferredSize(new Dimension(280, 40));
        discardBtn.addActionListener(e -> discardSelectedBatch());

        RoundedButton printListBtn = new RoundedButton("📋 View Printable Shelf Checklist", 12,
                UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        printListBtn.setForeground(Color.WHITE);
        printListBtn.setFont(UITheme.FONT_BUTTON);
        printListBtn.setPreferredSize(new Dimension(250, 40));
        printListBtn.addActionListener(e -> showChecklistDialog());

        bottomActionPanel.add(printListBtn);
        bottomActionPanel.add(discardBtn);

        tableCard.add(scrollPane, BorderLayout.CENTER);
        tableCard.add(bottomActionPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String note, Color bgColor, Color fgColor) {
        RoundedPanel card = new RoundedPanel(18, Color.WHITE);
        card.setLayout(new BorderLayout(0, 4));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor, 2, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tLbl.setForeground(fgColor);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(fgColor);

        JLabel nLbl = new JLabel(note);
        nLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nLbl.setForeground(UITheme.TEXT_MUTED);

        card.add(tLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(nLbl, BorderLayout.SOUTH);

        return card;
    }

    private JButton createFilterButton(String text, String filterKey) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 36));
        btn.addActionListener(e -> {
            activeFilter = filterKey;
            updateFilterButtonStyles();
            applyFilter();
        });
        return btn;
    }

    private void updateFilterButtonStyles() {
        styleOneButton(btnAll, "ALL_ALERT".equals(activeFilter));
        styleOneButton(btnExpired, "EXPIRED".equals(activeFilter));
        styleOneButton(btn7Days, "7DAYS".equals(activeFilter));
        styleOneButton(btn30Days, "30DAYS".equals(activeFilter));
    }

    private void styleOneButton(JButton btn, boolean isActive) {
        if (isActive) {
            btn.setBackground(UITheme.PRIMARY);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_DARK, 1));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UITheme.TEXT_DARK);
            btn.setBorder(BorderFactory.createLineBorder(new Color(0xDDDDDD), 1));
        }
    }

    public void loadData() {
        DashboardStats stats = Database.getDashboardStats();
        expiredCountLabel.setText(String.valueOf(stats.getExpiredBatches()));
        criticalCountLabel.setText(String.valueOf(stats.getExpiringWithin7Days()));
        warningCountLabel.setText(String.valueOf(stats.getExpiringWithin30Days()));

        int totalBatches = Database.getAllStocks().size();
        int safeCount = Math.max(0, totalBatches - stats.getExpiredBatches() - stats.getExpiringWithin7Days() - stats.getExpiringWithin30Days());
        goodCountLabel.setText(String.valueOf(safeCount));

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
            } else { // ALL_ALERT: expired + all within 30 days
                return days <= 30;
            }
        }).toList();

        for (StockItemDTO item : currentList) {
            long days = item.getDaysUntilExpiry();
            String daysStr = days < 0 ? ("EXPIRED (" + Math.abs(days) + "d ago)") : (days + " days left");

            tableModel.addRow(new Object[]{
                    item.getStockId(),
                    item.getProductName(),
                    item.getCategoryName(),
                    item.getBarcode(),
                    item.getBatchNo(),
                    item.getQuantity(),
                    item.getExpiryDateFormatted(),
                    daysStr,
                    item.getStatus(),
                    String.format("$%.2f", item.getTotalValue())
            });
        }
    }

    private void discardSelectedBatch() {
        int selectedRow = alertTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expired or expiring batch from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StockItemDTO item = currentList.get(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(this,
                "DISCARD STOCK CONFIRMATION\n\n" +
                        "Product: " + item.getProductName() + "\n" +
                        "Batch #: " + item.getBatchNo() + "\n" +
                        "Quantity: " + item.getQuantity() + " units\n" +
                        "Expiry Date: " + item.getExpiryDateFormatted() + " (" + item.getStatus() + ")\n\n" +
                        "Are you sure you want to write off and remove this batch from stock?",
                "Confirm Stock Write-off", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Database.deleteStock(item.getStockId())) {
                JOptionPane.showMessageDialog(this, "Batch written off and removed from active inventory.", "Success", JOptionPane.INFORMATION_MESSAGE);
                navigator.refreshAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove batch.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showChecklistDialog() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expiring batches to show in checklist!", "All Good", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("          EXPIRY FINDER - SHELF CHECKLIST           \n");
        sb.append("====================================================\n");
        sb.append(String.format("Generated: %s | Total Items: %d\n\n", java.time.LocalDate.now(), currentList.size()));

        for (StockItemDTO item : currentList) {
            sb.append(String.format("[%s] %s\n", item.getStatus(), item.getProductName()));
            sb.append(String.format("  Category: %s | Barcode: %s\n", item.getCategoryName(), item.getBarcode()));
            sb.append(String.format("  Batch #%d | Qty: %d | Expiry: %s (%s)\n",
                    item.getBatchNo(), item.getQuantity(), item.getExpiryDateFormatted(),
                    item.getDaysUntilExpiry() < 0 ? "EXPIRED" : item.getDaysUntilExpiry() + " days left"));
            sb.append("  --------------------------------------------------\n");
        }

        JTextArea textArea = new JTextArea(sb.toString(), 20, 45);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scroll, "Printable Shelf Checklist", JOptionPane.PLAIN_MESSAGE);
    }
}
