package gui;

import database.Database;
import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import gui.modified.RoundedTextField;
import model.Product;
import model.Stock;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class BarcodeLookupPanel extends JPanel {
    private final ViewNavigator navigator;

    private RoundedTextField barcodeInput;
    private JComboBox<String> sampleBarcodeCombo;

    private JLabel prodNameLabel;
    private JLabel prodCategoryLabel;
    private JLabel prodPriceLabel;
    private JLabel prodTotalStockLabel;
    private JPanel resultCard;

    private JTable batchesTable;
    private DefaultTableModel tableModel;
    private Product currentProduct;

    public BarcodeLookupPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout(0, 15));
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        initUI();
    }

    private void initUI() {
        // --- Header ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        headerPanel.setBackground(UITheme.MAIN_BG);

        JLabel titleLabel = new JLabel("Barcode Scanner & Fast Lookup");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Scan barcode or enter EAN to check product batches and expiry status instantly");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // --- Scanner Input Card ---
        RoundedPanel scanCard = new RoundedPanel(20, Color.WHITE);
        scanCard.setLayout(new GridBagLayout());
        scanCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 10, 6, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        JLabel scanIconLbl = new JLabel("🔍 Scan / Enter Barcode:");
        scanIconLbl.setFont(UITheme.FONT_LABEL);
        scanCard.add(scanIconLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        barcodeInput = new RoundedTextField(14);
        barcodeInput.setFont(new Font("Segoe UI", Font.BOLD, 16));
        barcodeInput.setPreferredSize(new Dimension(0, 44));
        barcodeInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLookup();
                }
            }
        });
        scanCard.add(barcodeInput, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.3;
        RoundedButton lookupBtn = new RoundedButton("LOOKUP PRODUCT", 14, UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        lookupBtn.setForeground(Color.WHITE);
        lookupBtn.setFont(UITheme.FONT_BUTTON);
        lookupBtn.setPreferredSize(new Dimension(170, 44));
        lookupBtn.addActionListener(e -> performLookup());
        scanCard.add(lookupBtn, gbc);

        // Quick sample picker
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        JLabel sampleLbl = new JLabel("Or Select from Inventory:");
        sampleLbl.setFont(UITheme.FONT_REGULAR);
        sampleLbl.setForeground(UITheme.TEXT_MUTED);
        scanCard.add(sampleLbl, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        sampleBarcodeCombo = new JComboBox<>();
        sampleBarcodeCombo.setFont(UITheme.FONT_REGULAR);
        sampleBarcodeCombo.setPreferredSize(new Dimension(0, 36));
        sampleBarcodeCombo.addActionListener(e -> {
            String selected = (String) sampleBarcodeCombo.getSelectedItem();
            if (selected != null && selected.contains("[")) {
                String code = selected.substring(selected.indexOf("[") + 1, selected.indexOf("]"));
                barcodeInput.setText(code);
                performLookup();
            }
        });
        scanCard.add(sampleBarcodeCombo, gbc);

        // --- Result Card ---
        resultCard = new RoundedPanel(20, Color.WHITE);
        resultCard.setLayout(new BorderLayout(0, 15));
        resultCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Product Details Info Box
        JPanel detailsBox = new JPanel(new GridLayout(2, 2, 20, 10));
        detailsBox.setOpaque(false);
        detailsBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)),
                BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));

        prodNameLabel = new JLabel("Product: -");
        prodNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        prodNameLabel.setForeground(UITheme.TEXT_DARK);

        prodCategoryLabel = new JLabel("Category: -");
        prodCategoryLabel.setFont(UITheme.FONT_LABEL);
        prodCategoryLabel.setForeground(UITheme.TEXT_MUTED);

        prodPriceLabel = new JLabel("Unit Price: -");
        prodPriceLabel.setFont(UITheme.FONT_LABEL);
        prodPriceLabel.setForeground(UITheme.TEXT_DARK);

        prodTotalStockLabel = new JLabel("Total Stock: 0 units");
        prodTotalStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        prodTotalStockLabel.setForeground(UITheme.PRIMARY);

        detailsBox.add(prodNameLabel);
        detailsBox.add(prodCategoryLabel);
        detailsBox.add(prodPriceLabel);
        detailsBox.add(prodTotalStockLabel);

        // Batches Table
        String[] columns = {"Batch #", "Quantity", "Arrival Date", "Expiry Date", "Days Remaining", "Expiry Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        batchesTable = new JTable(tableModel);
        UITheme.styleTable(batchesTable);
        batchesTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(batchesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xEEEEEE)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Result bottom actions
        JPanel resultBottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        resultBottomBar.setOpaque(false);

        RoundedButton addBatchBtn = new RoundedButton("➕ Add Batch for this Product", 12, UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        addBatchBtn.setForeground(Color.WHITE);
        addBatchBtn.setFont(UITheme.FONT_BUTTON);
        addBatchBtn.setPreferredSize(new Dimension(230, 38));
        addBatchBtn.addActionListener(e -> {
            if (currentProduct != null) {
                navigator.navigateTo("ADD");
            }
        });

        resultBottomBar.add(addBatchBtn);

        resultCard.add(detailsBox, BorderLayout.NORTH);
        resultCard.add(scrollPane, BorderLayout.CENTER);
        resultCard.add(resultBottomBar, BorderLayout.SOUTH);
        resultCard.setVisible(false);

        // Container
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 15));
        centerWrapper.setOpaque(false);
        centerWrapper.add(scanCard, BorderLayout.NORTH);
        centerWrapper.add(resultCard, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        loadSampleBarcodes();
    }

    public void loadSampleBarcodes() {
        sampleBarcodeCombo.removeAllItems();
        sampleBarcodeCombo.addItem("-- Select a product to auto-fill barcode --");
        List<Product> products = Database.getAllProducts();
        for (Product p : products) {
            if (p.getBarcode() != null && !p.getBarcode().trim().isEmpty()) {
                sampleBarcodeCombo.addItem(p.getName() + " [" + p.getBarcode() + "]");
            }
        }
    }

    private void performLookup() {
        String code = barcodeInput.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or scan a barcode.", "Barcode Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product product = Database.getProductByBarcode(code);
        if (product == null) {
            resultCard.setVisible(false);
            int opt = JOptionPane.showConfirmDialog(this,
                    "No product found with barcode: '" + code + "'.\nWould you like to register this as a new product?",
                    "Not Found", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                navigator.navigateTo("ADD");
            }
            return;
        }

        currentProduct = product;
        prodNameLabel.setText("📦 " + product.getName() + " (" + product.getBarcode() + ")");
        prodCategoryLabel.setText("Category: " + (product.getCategoryName() != null ? product.getCategoryName() : "Uncategorized"));
        prodPriceLabel.setText(String.format("Unit Price: $%.2f", product.getPrice()));

        List<Stock> stocks = Database.getStocksByProductId(product.getId());
        tableModel.setRowCount(0);

        int totalUnits = 0;
        java.time.LocalDate today = java.time.LocalDate.now();

        for (Stock s : stocks) {
            totalUnits += s.getQuantity();
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, s.getExpiryDate().toLocalDate());
            String daysStr = days < 0 ? (Math.abs(days) + "d ago") : (days + "d");

            String status;
            if (days < 0) status = "EXPIRED";
            else if (days <= 7) status = "CRITICAL (<7d)";
            else if (days <= 30) status = "EXPIRING (<30d)";
            else status = "GOOD";

            tableModel.addRow(new Object[]{
                    s.getBatchNo(),
                    s.getQuantity(),
                    s.getArrivalLocalDate(),
                    s.getExpiryLocalDate(),
                    daysStr,
                    status
            });
        }

        prodTotalStockLabel.setText(String.format("Total Stock: %d units across %d batch(es)", totalUnits, stocks.size()));
        resultCard.setVisible(true);
        revalidate();
        repaint();
    }
}
