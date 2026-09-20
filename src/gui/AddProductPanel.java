package gui;

import database.Database;
import gui.modified.RoundedButton;
import gui.modified.RoundedPanel;
import gui.modified.RoundedTextField;
import model.Category;
import model.Product;
import model.Stock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AddProductPanel extends JPanel {
    private final ViewNavigator navigator;

    private JRadioButton rbNewProduct;
    private JRadioButton rbExistingProduct;
    private JComboBox<Product> cbExistingProducts;
    private JPanel existingProductPanel;

    private RoundedTextField nameField;
    private JComboBox<Category> categoryCombo;
    private RoundedTextField barcodeField;
    private RoundedTextField priceField;

    private RoundedTextField batchField;
    private RoundedTextField quantityField;
    private RoundedTextField arrivalDateField;
    private RoundedTextField expiryDateField;

    private JLabel feedbackLabel;

    public AddProductPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));

        initUI();
    }

    private void initUI() {
        // --- Title Area ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(UITheme.MAIN_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Add Product & Stock");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_DARK);

        JLabel subtitleLabel = new JLabel("Register new products or add fresh stock batches with expiry tracking");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // --- Mode Selector (New vs Existing Product) ---
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        modePanel.setBackground(UITheme.MAIN_BG);
        modePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        rbNewProduct = new JRadioButton("Create New Product", true);
        rbNewProduct.setFont(UITheme.FONT_LABEL);
        rbNewProduct.setBackground(UITheme.MAIN_BG);
        rbNewProduct.setFocusPainted(false);

        rbExistingProduct = new JRadioButton("Add Stock to Existing Product", false);
        rbExistingProduct.setFont(UITheme.FONT_LABEL);
        rbExistingProduct.setBackground(UITheme.MAIN_BG);
        rbExistingProduct.setFocusPainted(false);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(rbNewProduct);
        modeGroup.add(rbExistingProduct);

        modePanel.add(rbNewProduct);
        modePanel.add(rbExistingProduct);

        // Dropdown for existing products
        existingProductPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        existingProductPanel.setBackground(UITheme.MAIN_BG);
        JLabel selectProdLabel = new JLabel("Select Product:");
        selectProdLabel.setFont(UITheme.FONT_LABEL);
        cbExistingProducts = new JComboBox<>();
        cbExistingProducts.setPreferredSize(new Dimension(300, 36));
        cbExistingProducts.setFont(UITheme.FONT_REGULAR);
        existingProductPanel.add(selectProdLabel);
        existingProductPanel.add(cbExistingProducts);
        existingProductPanel.setVisible(false);

        rbNewProduct.addActionListener(e -> toggleMode(false));
        rbExistingProduct.addActionListener(e -> toggleMode(true));

        cbExistingProducts.addActionListener(e -> {
            Product selected = (Product) cbExistingProducts.getSelectedItem();
            if (selected != null) {
                nameField.setText(selected.getName());
                nameField.setEnabled(false);
                barcodeField.setText(selected.getBarcode() != null ? selected.getBarcode() : "");
                barcodeField.setEnabled(false);
                priceField.setText(String.valueOf(selected.getPrice()));
                priceField.setEnabled(false);

                // Select category in combo
                for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                    Category cat = categoryCombo.getItemAt(i);
                    if (cat != null && cat.getId() == selected.getCategoryID()) {
                        categoryCombo.setSelectedIndex(i);
                        break;
                    }
                }
                categoryCombo.setEnabled(false);
            }
        });

        // --- Form Card ---
        RoundedPanel formCard = new RoundedPanel(24, Color.WHITE);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.weightx = 1.0;

        int row = 0;

        // Row 0: Section 1 Header
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JLabel pHeader = new JLabel("1. Product Information");
        pHeader.setFont(UITheme.FONT_SECTION);
        pHeader.setForeground(UITheme.PRIMARY);
        formCard.add(pHeader, gbc);
        gbc.gridwidth = 1;

        // Row 1: Product Name & Category
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Product Name *", nameField = new RoundedTextField(12)), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        categoryCombo = new JComboBox<>();
        categoryCombo.setPreferredSize(new Dimension(0, 42));
        categoryCombo.setFont(UITheme.FONT_REGULAR);
        formCard.add(createComboBlock("Category *", categoryCombo), gbc);

        // Row 2: Barcode & Unit Price
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Barcode / EAN Number", barcodeField = new RoundedTextField(12)), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Unit Price ($) *", priceField = new RoundedTextField(12)), gbc);

        // Row 3: Section 2 Header
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 12, 6, 12);
        JLabel sHeader = new JLabel("2. Stock Batch & Expiry Date");
        sHeader.setFont(UITheme.FONT_SECTION);
        sHeader.setForeground(UITheme.PRIMARY);
        formCard.add(sHeader, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 12, 8, 12);

        // Row 4: Batch Number & Quantity
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Batch Number *", batchField = new RoundedTextField(12)), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Quantity (units) *", quantityField = new RoundedTextField(12)), gbc);

        // Row 5: Arrival Date & Expiry Date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        formCard.add(createFieldBlock("Arrival Date (YYYY-MM-DD)", arrivalDateField = new RoundedTextField(12)), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        JPanel expiryBlock = new JPanel(new BorderLayout(0, 4));
        expiryBlock.setOpaque(false);
        JLabel expLbl = new JLabel("Expiry Date (YYYY-MM-DD) *");
        expLbl.setFont(UITheme.FONT_LABEL);
        expLbl.setForeground(UITheme.TEXT_DARK);
        expiryDateField = new RoundedTextField(12);
        expiryDateField.setPreferredSize(new Dimension(0, 42));

        // Quick date shortcuts
        JPanel shortcutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        shortcutPanel.setOpaque(false);
        shortcutPanel.add(createDateShortcut("+7D", 7));
        shortcutPanel.add(createDateShortcut("+1M", 30));
        shortcutPanel.add(createDateShortcut("+3M", 90));
        shortcutPanel.add(createDateShortcut("+6M", 180));
        shortcutPanel.add(createDateShortcut("+1Y", 365));

        expiryBlock.add(expLbl, BorderLayout.NORTH);
        expiryBlock.add(expiryDateField, BorderLayout.CENTER);
        expiryBlock.add(shortcutPanel, BorderLayout.SOUTH);
        formCard.add(expiryBlock, gbc);

        // Row 6: Feedback Label
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        feedbackLabel = new JLabel(" ");
        feedbackLabel.setFont(UITheme.FONT_LABEL);
        feedbackLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formCard.add(feedbackLabel, gbc);

        // Row 7: Submit & Action Buttons
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 12, 10, 12);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        RoundedButton submitBtn = new RoundedButton("  SAVE PRODUCT & STOCK  ", 18, UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitBtn.setPreferredSize(new Dimension(280, 48));
        submitBtn.addActionListener(e -> saveForm());

        RoundedButton clearBtn = new RoundedButton("Clear Form", 18, new Color(0xEEEEEE), new Color(0xDDDDDD));
        clearBtn.setForeground(UITheme.TEXT_DARK);
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setPreferredSize(new Dimension(140, 48));
        clearBtn.addActionListener(e -> clearForm());

        buttonPanel.add(submitBtn);
        buttonPanel.add(clearBtn);
        formCard.add(buttonPanel, gbc);

        // --- Assemble ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(UITheme.MAIN_BG);
        topContainer.add(titlePanel, BorderLayout.NORTH);

        JPanel selectWrapper = new JPanel(new BorderLayout());
        selectWrapper.setBackground(UITheme.MAIN_BG);
        selectWrapper.add(modePanel, BorderLayout.NORTH);
        selectWrapper.add(existingProductPanel, BorderLayout.CENTER);
        topContainer.add(selectWrapper, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(null);
        scrollPane.setBackground(UITheme.MAIN_BG);
        scrollPane.getViewport().setBackground(UITheme.MAIN_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Set default values
        resetFormDefaults();
        loadCategories();
        loadExistingProducts();
    }

    private JButton createDateShortcut(String label, int days) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setMargin(new Insets(2, 6, 2, 6));
        btn.setBackground(new Color(0xE8F5E9));
        btn.setForeground(UITheme.PRIMARY_DARK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(0xC8E6C9), 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> expiryDateField.setText(LocalDate.now().plusDays(days).toString()));
        return btn;
    }

    private JPanel createFieldBlock(String labelText, RoundedTextField field) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_DARK);
        field.setPreferredSize(new Dimension(0, 42));
        field.setFont(UITheme.FONT_REGULAR);
        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
    }

    private JPanel createComboBlock(String labelText, JComboBox<?> combo) {
        JPanel block = new JPanel(new BorderLayout(0, 4));
        block.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_DARK);
        combo.setPreferredSize(new Dimension(0, 42));
        combo.setFont(UITheme.FONT_REGULAR);
        block.add(label, BorderLayout.NORTH);
        block.add(combo, BorderLayout.CENTER);
        return block;
    }

    private void toggleMode(boolean isExisting) {
        existingProductPanel.setVisible(isExisting);
        if (isExisting) {
            loadExistingProducts();
            if (cbExistingProducts.getItemCount() > 0) {
                cbExistingProducts.setSelectedIndex(0);
            }
        } else {
            nameField.setEnabled(true);
            barcodeField.setEnabled(true);
            priceField.setEnabled(true);
            categoryCombo.setEnabled(true);
            clearForm();
        }
        revalidate();
        repaint();
    }

    public void loadCategories() {
        categoryCombo.removeAllItems();
        List<Category> categories = Database.getAllCategories();
        for (Category c : categories) {
            categoryCombo.addItem(c);
        }
    }

    public void loadExistingProducts() {
        cbExistingProducts.removeAllItems();
        List<Product> products = Database.getAllProducts();
        for (Product p : products) {
            cbExistingProducts.addItem(p);
        }
    }

    private void resetFormDefaults() {
        arrivalDateField.setText(LocalDate.now().toString());
        expiryDateField.setText(LocalDate.now().plusMonths(3).toString());
        batchField.setText(String.valueOf((int) (System.currentTimeMillis() % 100000)));
        quantityField.setText("20");
        priceField.setText("0.00");
    }

    private void clearForm() {
        nameField.setText("");
        barcodeField.setText("");
        priceField.setText("0.00");
        batchField.setText(String.valueOf((int) (System.currentTimeMillis() % 100000)));
        quantityField.setText("20");
        arrivalDateField.setText(LocalDate.now().toString());
        expiryDateField.setText(LocalDate.now().plusMonths(3).toString());
        feedbackLabel.setText(" ");
    }

    private void saveForm() {
        try {
            String name = nameField.getText().trim();
            String barcode = barcodeField.getText().trim();
            String priceStr = priceField.getText().trim();
            String batchStr = batchField.getText().trim();
            String qtyStr = quantityField.getText().trim();
            String arrivalStr = arrivalDateField.getText().trim();
            String expiryStr = expiryDateField.getText().trim();

            if (name.isEmpty()) {
                showError("Please enter product name");
                nameField.requestFocus();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showError("Please enter a valid unit price (e.g. 2.99)");
                priceField.requestFocus();
                return;
            }

            int batchNo;
            try {
                batchNo = Integer.parseInt(batchStr);
            } catch (NumberFormatException e) {
                showError("Please enter a valid integer for Batch Number");
                batchField.requestFocus();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
                if (quantity <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showError("Please enter a positive quantity");
                quantityField.requestFocus();
                return;
            }

            if (expiryStr.isEmpty()) {
                showError("Please enter or select an expiry date (YYYY-MM-DD)");
                expiryDateField.requestFocus();
                return;
            }

            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            long categoryId = selectedCategory != null ? selectedCategory.getId() : 0;

            long productId;

            if (rbExistingProduct.isSelected()) {
                Product existing = (Product) cbExistingProducts.getSelectedItem();
                if (existing == null) {
                    showError("Please select an existing product");
                    return;
                }
                productId = existing.getId();
            } else {
                // Check if product with this barcode already exists
                if (!barcode.isEmpty()) {
                    Product existingByBarcode = Database.getProductByBarcode(barcode);
                    if (existingByBarcode != null) {
                        int opt = JOptionPane.showConfirmDialog(this,
                                "A product with barcode '" + barcode + "' already exists: '" + existingByBarcode.getName() + "'.\n" +
                                        "Do you want to add this stock batch to that existing product?",
                                "Product Exists", JOptionPane.YES_NO_OPTION);
                        if (opt == JOptionPane.YES_OPTION) {
                            productId = existingByBarcode.getId();
                        } else {
                            return;
                        }
                    } else {
                        Product newProduct = new Product(name, barcode, price, categoryId);
                        if (!Database.insertProduct(newProduct)) {
                            showError("Failed to save product to database.");
                            return;
                        }
                        productId = newProduct.getId();
                    }
                } else {
                    Product newProduct = new Product(name, barcode, price, categoryId);
                    if (!Database.insertProduct(newProduct)) {
                        showError("Failed to save product to database.");
                        return;
                    }
                    productId = newProduct.getId();
                }
            }

            // Create Stock batch
            Stock stock = new Stock(productId, batchNo, quantity, arrivalStr, expiryStr);
            if (stock.save()) {
                showSuccess("✓ Successfully saved product & stock batch (ID: " + stock.getId() + ")!");
                navigator.refreshAll();
                int viewChoice = JOptionPane.showConfirmDialog(this,
                        "Stock batch saved successfully!\nWould you like to view it in the Inventory list?",
                        "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                if (viewChoice == JOptionPane.YES_OPTION) {
                    navigator.navigateTo("INVENTORY");
                } else {
                    clearForm();
                    resetFormDefaults();
                }
            } else {
                showError("Failed to save stock batch. Check date format (must be YYYY-MM-DD).");
            }

        } catch (Exception ex) {
            showError("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String msg) {
        feedbackLabel.setText("❌ " + msg);
        feedbackLabel.setForeground(UITheme.EXPIRED_FG);
    }

    private void showSuccess(String msg) {
        feedbackLabel.setText(msg);
        feedbackLabel.setForeground(UITheme.GOOD_FG);
    }
}
