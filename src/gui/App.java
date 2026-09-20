package gui;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame implements ViewNavigator {
    private final LeftPanel leftPanel;
    private final CardLayout cardLayout;
    private final JPanel cardContainer;

    private final AddProductPanel addPanel;
    private final InventoryPanel inventoryPanel;
    private final ExpiryAlertsPanel expiryPanel;
    private final CategoryPanel categoryPanel;
    private final AboutPanel aboutPanel;

    public App() {
        ImageIcon icon = new ImageIcon("src/assets/icon.png");

        /*---------------- Initial Configurations -----------------*/
        setTitle("Expiry Finder - Smart Stock & Expiry Manager");
        if (icon.getImage() != null) {
            setIconImage(icon.getImage());
        }
        setMinimumSize(new Dimension(1080, 700));
        setSize(1240, 780);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        /*---------------- Views & Card Container -----------------*/
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(UITheme.MAIN_BG);

        addPanel = new AddProductPanel(this);
        inventoryPanel = new InventoryPanel(this);
        expiryPanel = new ExpiryAlertsPanel(this);
        categoryPanel = new CategoryPanel(this);
        aboutPanel = new AboutPanel();

        cardContainer.add(addPanel, "ADD");
        cardContainer.add(inventoryPanel, "INVENTORY");
        cardContainer.add(expiryPanel, "EXPIRY");
        cardContainer.add(categoryPanel, "CATEGORIES");
        cardContainer.add(aboutPanel, "ABOUT");

        /*---------------- Navigation Panel -----------------*/
        leftPanel = new LeftPanel(this);

        /*---------------- Final Assembly -----------------*/
        add(leftPanel, BorderLayout.WEST);
        add(cardContainer, BorderLayout.CENTER);

        // Start on Inventory if stocks exist, else on Add Product
        navigateTo("INVENTORY");

        setVisible(true);
    }

    @Override
    public void navigateTo(String viewName) {
        cardLayout.show(cardContainer, viewName);
        leftPanel.setActiveView(viewName);

        if ("INVENTORY".equalsIgnoreCase(viewName)) {
            inventoryPanel.loadData();
        } else if ("EXPIRY".equalsIgnoreCase(viewName)) {
            expiryPanel.loadData();
        } else if ("ADD".equalsIgnoreCase(viewName)) {
            addPanel.loadCategories();
            addPanel.loadExistingProducts();
        } else if ("CATEGORIES".equalsIgnoreCase(viewName)) {
            categoryPanel.loadData();
        }
    }

    @Override
    public void refreshAll() {
        inventoryPanel.loadData();
        expiryPanel.loadData();
        addPanel.loadCategories();
        addPanel.loadExistingProducts();
        categoryPanel.loadData();
    }
}