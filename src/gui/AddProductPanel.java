package gui;

import database.Database;
import gui.components.ModernButton;
import gui.components.ModernCard;
import gui.components.ModernTextField;
import gui.components.VectorIcon;
import model.Category;
import model.Product;
import model.Stock;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AddProductPanel extends JPanel {
    private final ViewNavigator navigator;

    private boolean isNewProductMode = true;
    private JButton btnModeNew;
    private JButton btnModeExisting;
    private JPanel existingProductRow;
    private JComboBox<Product> cbExistingProducts;

    private ModernTextField nameField;
    private JComboBox<Category> categoryCombo;
    private ModernTextField priceField;

    private ModernTextField batchField;
    private ModernTextField quantityField;
    private ModernTextField arrivalDateField;
    private ModernTextField expiryDateField;

    private JLabel feedbackBanner;

    public AddProductPanel(ViewNavigator navigator) {
        this.navigator = navigator;
        setLayout(new BorderLayout());
        setBackground(UITheme.MAIN_BG);
        setBorder(BorderFactory.createEmptyBorder(25, 45, 25, 45));

        initUI();
    }

    private void initUI() {
        // --- Top Title Header ---
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setBackground(UITheme.MAIN_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Add Stock Batch");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_MAIN);

        JLabel subtitleLabel = new JLabel("Quickly register a product and track its batch expiration date");
        subtitleLabel.setFont(UITheme.FONT_SUBTITLE);
        subtitleLabel.setForeground(UITheme.TEXT_MUTED);

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        // --- Main Form Card ---
        ModernCard formCard = new ModernCard(20);
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.weightx = 1.0;

        int row = 0;

        // Row 0: Mode Toggle (Pill Switcher)
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleContainer.setOpaque(false);

        JPanel togglePill = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xF1F5F9));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        togglePill.setOpaque(false);

        btnModeNew = createTogglePillBtn("New Product", new VectorIcon(VectorIcon.Type.PLUS, 13), true);
        btnModeExisting = createTogglePillBtn("Existing Product", new VectorIcon(VectorIcon.Type.PACKAGE, 13), false);

        btnModeNew.addActionListener(e -> setMode(true));
        btnModeExisting.addActionListener(e -> setMode(false));

        togglePill.add(btnModeNew);
        togglePill.add(btnModeExisting);
        toggleContainer.add(togglePill);
        formCard.add(toggleContainer, gbc);

        // Row 1: Existing Product Selector (Hidden by default)
        existingProductRow = new JPanel(new BorderLayout(10, 4));
        existingProductRow.setOpaque(false);
        JLabel selectProdLbl = new JLabel("Choose Existing Product:");
        selectProdLbl.setFont(UITheme.FONT_LABEL);
        selectProdLbl.setForeground(UITheme.TEXT_MAIN);
        cbExistingProducts = new JComboBox<>();
        cbExistingProducts.setPreferredSize(new Dimension(0, 42));
        cbExistingProducts.setFont(UITheme.FONT_REGULAR);
        cbExistingProducts.addActionListener(e -> onExistingProductSelected());
        existingProductRow.add(selectProdLbl, BorderLayout.NORTH);
        existingProductRow.add(cbExistingProducts, BorderLayout.CENTER);
        existingProductRow.setVisible(false);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formCard.add(existingProductRow, gbc);
        gbc.gridwidth = 1;

        // Row 2: Section 1 Header
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 12, 4, 12);
        JLabel pHeader = new JLabel("Product Details");
        pHeader.setFont(UITheme.FONT_SECTION);
        pHeader.setForeground(UITheme.PRIMARY_DARK);
        formCard.add(pHeader, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 12, 8, 12);

        // Row 3: Product Name & Category
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        nameField = new ModernTextField("e.g. Organic Almond Milk 1L");
        formCard.add(createFieldBlock("Product Name", nameField), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        categoryCombo = new JComboBox<>();
        categoryCombo.setFont(UITheme.FONT_REGULAR);
        categoryCombo.setPreferredSize(new Dimension(0, 42));
        categoryCombo.setBackground(Color.WHITE);
        formCard.add(createComboBlock("Category", categoryCombo), gbc);

        // Row 4: Unit Price
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        priceField = new ModernTextField("e.g. 50.00");
        formCard.add(createFieldBlock("Price per Unit (₹)", priceField), gbc);
        gbc.gridwidth = 1;

        // Row 5: Section 2 Header
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(18, 12, 4, 12);
        JLabel sHeader = new JLabel("Batch & Expiration");
        sHeader.setFont(UITheme.FONT_SECTION);
        sHeader.setForeground(UITheme.PRIMARY_DARK);
        formCard.add(sHeader, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 12, 8, 12);

        // Row 6: Batch # & Quantity
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        batchField = new ModernTextField("e.g. 101");
        formCard.add(createFieldBlock("Batch Number", batchField), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        quantityField = new ModernTextField("e.g. 24");
        formCard.add(createFieldBlock("Quantity in Stock (units)", quantityField), gbc);

        // Row 7: Arrival Date & Expiry Date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.5;
        arrivalDateField = new ModernTextField("YYYY-MM-DD");
        formCard.add(createFieldBlock("Arrival Date", arrivalDateField), gbc);

        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.5;
        JPanel expiryBlock = new JPanel(new BorderLayout(0, 6));
        expiryBlock.setOpaque(false);
        JLabel expLbl = new JLabel("Expiry Date (YYYY-MM-DD)");
        expLbl.setFont(UITheme.FONT_LABEL);
        expLbl.setForeground(UITheme.TEXT_MAIN);
        expiryDateField = new ModernTextField("Select or type expiry date");
        expiryDateField.setPreferredSize(new Dimension(0, 42));

        // Modern Quick date shortcuts
        JPanel shortcutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        shortcutPanel.setOpaque(false);
        shortcutPanel.add(createDatePill("+7 Days", 7));
        shortcutPanel.add(createDatePill("+1 Month", 30));
        shortcutPanel.add(createDatePill("+3 Months", 90));
        shortcutPanel.add(createDatePill("+6 Months", 180));
        shortcutPanel.add(createDatePill("+1 Year", 365));

        expiryBlock.add(expLbl, BorderLayout.NORTH);
        expiryBlock.add(expiryDateField, BorderLayout.CENTER);
        expiryBlock.add(shortcutPanel, BorderLayout.SOUTH);
        formCard.add(expiryBlock, gbc);

        // Row 8: Feedback Banner
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        feedbackBanner = new JLabel(" ", SwingConstants.CENTER);
        feedbackBanner.setFont(UITheme.FONT_LABEL);
        feedbackBanner.setOpaque(false);
        formCard.add(feedbackBanner, gbc);

        // Row 9: Buttons
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 12, 10, 12);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow.setOpaque(false);

        ModernButton submitBtn = new ModernButton("Save to Inventory", new VectorIcon(VectorIcon.Type.CHECK, 16), ModernButton.Variant.PRIMARY, 16);
        submitBtn.setPreferredSize(new Dimension(240, 48));
        submitBtn.addActionListener(e -> saveForm());

        ModernButton clearBtn = new ModernButton("Clear Form", new VectorIcon(VectorIcon.Type.CLEAR, 16), ModernButton.Variant.OUTLINE, 16);
        clearBtn.setPreferredSize(new Dimension(140, 48));
        clearBtn.addActionListener(e -> clearForm());

        buttonRow.add(submitBtn);
        buttonRow.add(clearBtn);
        formCard.add(buttonRow, gbc);

        // --- Container Assembly ---
        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        resetFormDefaults();
        loadCategories();
        loadExistingProducts();
    }

    private JButton createTogglePillBtn(String text, Icon icon, boolean active) {
        JButton btn = new JButton(text, icon);
        btn.setIconTextGap(8);
        btn.setFont(UITheme.FONT_LABEL);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        stylePillButton(btn, active);
        return btn;
    }

    private void stylePillButton(JButton btn, boolean active) {
        if (active) {
            btn.setForeground(UITheme.PRIMARY_DARK);
            btn.setBackground(Color.WHITE);
        } else {
            btn.setForeground(UITheme.TEXT_MUTED);
            btn.setBackground(new Color(0xF1F5F9));
        }
        btn.repaint();
    }

    private void setMode(boolean newProduct) {
        this.isNewProductMode = newProduct;
        stylePillButton(btnModeNew, newProduct);
        stylePillButton(btnModeExisting, !newProduct);

        existingProductRow.setVisible(!newProduct);
        if (!newProduct) {
            loadExistingProducts();
            nameField.setEnabled(false);
            priceField.setEnabled(false);
            categoryCombo.setEnabled(false);
            onExistingProductSelected();
        } else {
            nameField.setEnabled(true);
            priceField.setEnabled(true);
            categoryCombo.setEnabled(true);
            clearForm();
        }
        revalidate();
        repaint();
    }

    private void onExistingProductSelected() {
        Product selected = (Product) cbExistingProducts.getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            priceField.setText(String.format("%.2f", selected.getPrice()));

            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                Category c = categoryCombo.getItemAt(i);
                if (c != null && c.getId() == selected.getCategoryID()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private JButton createDatePill(String label, int days) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setMargin(new Insets(3, 8, 3, 8));
        btn.setBackground(UITheme.PRIMARY_LIGHT);
        btn.setForeground(UITheme.PRIMARY_DARK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(UITheme.PRIMARY_BORDER, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> expiryDateField.setText(LocalDate.now().plusDays(days).toString()));
        return btn;
    }

    private JPanel createFieldBlock(String labelText, ModernTextField field) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MAIN);
        field.setPreferredSize(new Dimension(0, 42));
        block.add(label, BorderLayout.NORTH);
        block.add(field, BorderLayout.CENTER);
        return block;
    }

    private JPanel createComboBlock(String labelText, JComboBox<?> combo) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MAIN);
        block.add(label, BorderLayout.NORTH);
        block.add(combo, BorderLayout.CENTER);
        return block;
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
        priceField.setText("2.99");
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("2.99");
        batchField.setText(String.valueOf((int) (System.currentTimeMillis() % 100000)));
        quantityField.setText("20");
        arrivalDateField.setText(LocalDate.now().toString());
        expiryDateField.setText(LocalDate.now().plusMonths(3).toString());
        feedbackBanner.setText(" ");
    }

    private void saveForm() {
        String name = nameField.getText().trim();
        String priceStr = priceField.getText().trim();
        String batchStr = batchField.getText().trim();
        String qtyStr = quantityField.getText().trim();
        String arrivalStr = arrivalDateField.getText().trim();
        String expiryStr = expiryDateField.getText().trim();

        if (name.isEmpty()) {
            showError("Please provide a product name.");
            nameField.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Please enter a valid price (e.g. 3.49).");
            priceField.requestFocus();
            return;
        }

        int batchNo;
        try {
            batchNo = Integer.parseInt(batchStr);
        } catch (NumberFormatException e) {
            showError("Please enter a batch number.");
            batchField.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Please enter a positive quantity.");
            quantityField.requestFocus();
            return;
        }

        if (expiryStr.isEmpty()) {
            showError("Please choose or type an expiry date.");
            expiryDateField.requestFocus();
            return;
        }

        Category selectedCat = (Category) categoryCombo.getSelectedItem();
        long categoryId = selectedCat != null ? selectedCat.getId() : 0;

        long productId;
        if (!isNewProductMode) {
            Product selected = (Product) cbExistingProducts.getSelectedItem();
            if (selected == null) {
                showError("Please select a product from the list.");
                return;
            }
            productId = selected.getId();
        } else {
            Product newProd = new Product(name, "", price, categoryId);
            if (!Database.insertProduct(newProd)) {
                showError("Could not save product.");
                return;
            }
            productId = newProd.getId();
        }

        Stock stock = new Stock(productId, batchNo, quantity, arrivalStr, expiryStr);
        if (stock.save()) {
            showSuccess("✓ Added '" + name + "' (Batch " + batchNo + ") successfully!");
            navigator.refreshAll();
            int choice = JOptionPane.showConfirmDialog(this,
                    "Stock batch saved!\nWould you like to view it in the Inventory list?",
                    "Success", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                navigator.navigateTo("INVENTORY");
            } else {
                clearForm();
                resetFormDefaults();
            }
        } else {
            showError("Failed to save stock. Please ensure date format is YYYY-MM-DD.");
        }
    }

    private void showError(String msg) {
        feedbackBanner.setText("⚠️ " + msg);
        feedbackBanner.setForeground(UITheme.EXPIRED_FG);
    }

    private void showSuccess(String msg) {
        feedbackBanner.setText(msg);
        feedbackBanner.setForeground(UITheme.GOOD_FG);
    }
}
