package com.vending.ui;

import com.vending.model.Product;
import com.vending.patterns.VendingFacade;
import com.vending.patterns.VendingObserver;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class VendingUI extends JFrame implements VendingObserver {

    // Clean code --> Make non-static 'facade' transient
    private transient VendingFacade facade;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Components
    private JPanel productGridPanel;
    private DefaultTableModel tableModel;
    private JLabel balanceLabelMoneyPage;
    private JLabel balanceLabelProductPage;
    
    // Clean code --> Declare declarations on separate lines
    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtQty;

    private List<String> purchasedItems = new ArrayList<>();

    // --- CONSTANTS ---
    private static final String FONT_FAMILY = "Segoe UI";
    private static final String MONOSPACE_FONT = "Monospaced";
    
    // Page Names
    private static final String PAGE_MONEY = "PAGE_MONEY";
    private static final String PAGE_PRODUCT = "PAGE_PRODUCT";
    private static final String PAGE_ADMIN = "PAGE_ADMIN";

    // Messages
    private static final String TITLE_ERROR = "Error";
    private static final String CURRENCY_RP = "Rp ";

    // --- COLOR PALETTE ---
    private static final Color BG_COLOR = new Color(230, 235, 240);
    private static final Color MACHINE_BODY = new Color(45, 52, 54);
    private static final Color SLOT_DARK = new Color(20, 20, 20);
    private static final Color GLOW_GREEN = new Color(46, 204, 113);
    private static final Color BUTTON_GRAY = new Color(99, 110, 114);
    private static final Color TEXT_DARK = new Color(45, 52, 54);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);

    // Fonts
    private static final Font TITLE_FONT = new Font(FONT_FAMILY, Font.BOLD, 28);
    private static final Font HEADER_FONT = new Font(FONT_FAMILY, Font.BOLD, 16);
    private static final Font NORMAL_FONT = new Font(FONT_FAMILY, Font.PLAIN, 14);
    private static final Font DIGITAL_FONT = new Font(MONOSPACE_FONT, Font.BOLD, 24);

    public VendingUI() {
        this.facade = new VendingFacade();
        this.facade.registerObserver(this);
        initUI();
        try { 
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
        } catch (Exception ignored) {
            // If setting the look and feel fails, we simply proceed with the default.
        }
    }

    private void initUI() {
        setTitle("Simulasi Vending Machine Modern");
        setSize(1100, 850); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR); 

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(null);

        mainPanel.add(createMoneyPanel(), PAGE_MONEY);
        mainPanel.add(createProductPanel(), PAGE_PRODUCT);
        mainPanel.add(createAdminPanel(), PAGE_ADMIN);

        add(mainPanel);
        cardLayout.show(mainPanel, PAGE_MONEY);
    }

    // --- HALAMAN 1: MASUKKAN UANG ---
    private JPanel createMoneyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 63, 65));

        // 1. Header Area
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 63, 65));
        header.setBorder(new EmptyBorder(25, 40, 15, 40));

        JLabel title = new JLabel("Selamat Datang! Silahkan Masukkan Uang", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);

        JButton btnAdmin = new JButton("Admin");
        btnAdmin.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        btnAdmin.setBackground(Color.LIGHT_GRAY);
        btnAdmin.setFocusable(false);
        btnAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdmin.addActionListener(e -> showAdminLogin());

        JPanel adminWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminWrapper.setBackground(new Color(60, 63, 65));
        adminWrapper.add(btnAdmin);

        header.add(title, BorderLayout.CENTER);
        header.add(adminWrapper, BorderLayout.EAST);

        // 2. Center: Vending Machine Face
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(60, 63, 65));
        
        JPanel machineFace = new JPanel(new GridBagLayout());
        machineFace.setBackground(MACHINE_BODY);
        machineFace.setBorder(new CompoundBorder(
            new LineBorder(new Color(100, 100, 100), 2),
            new EmptyBorder(30, 40, 30, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Bill Acceptor Slot
        JPanel slotPanel = createSlotPanel();

        // Money Buttons
        JPanel moneyGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        moneyGrid.setBackground(MACHINE_BODY);
        moneyGrid.setBorder(new EmptyBorder(20, 0, 20, 0));

        moneyGrid.add(createMoneyButton(2000, BUTTON_GRAY));
        moneyGrid.add(createMoneyButton(5000, BUTTON_GRAY));
        moneyGrid.add(createMoneyButton(10000, BUTTON_GRAY));
        moneyGrid.add(createMoneyButton(20000, BUTTON_GRAY));
        moneyGrid.add(createMoneyButton(50000, BUTTON_GRAY));
        moneyGrid.add(createMoneyButton(100000, BUTTON_GRAY));

        // Digital Display
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(Color.BLACK);
        displayPanel.setBorder(new LineBorder(new Color(100, 100, 100), 3)); 
        
        balanceLabelMoneyPage = new JLabel("SALDO: Rp 0", SwingConstants.CENTER);
        balanceLabelMoneyPage.setFont(DIGITAL_FONT); 
        balanceLabelMoneyPage.setForeground(GLOW_GREEN);
        balanceLabelMoneyPage.setOpaque(true);
        balanceLabelMoneyPage.setBackground(Color.BLACK);
        balanceLabelMoneyPage.setPreferredSize(new Dimension(300, 60));
        displayPanel.add(balanceLabelMoneyPage, BorderLayout.CENTER);

        // Action Button
        JButton btnNext = new JButton("LANJUT PILIH BARANG >>");
        btnNext.setFont(new Font(FONT_FAMILY, Font.BOLD, 18)); 
        btnNext.setBackground(new Color(39, 174, 96));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFocusPainted(false);
        btnNext.setBorder(BorderFactory.createRaisedBevelBorder());
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.setPreferredSize(new Dimension(300, 50));
        
        btnNext.addActionListener(e -> {
            if (facade.getCurrentBalance() > 0) {
                refreshProductButtons(); 
                cardLayout.show(mainPanel, PAGE_PRODUCT);
            } else {
                JOptionPane.showMessageDialog(this, "Mohon masukkan uang ke slot terlebih dahulu.", "Info", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Assemble
        gbc.gridx = 0; gbc.gridy = 0;
        machineFace.add(slotPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        machineFace.add(moneyGrid, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        machineFace.add(displayPanel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        machineFace.add(btnNext, gbc);

        centerWrapper.add(machineFace);
        panel.add(header, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSlotPanel() {
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
        slotPanel.setBackground(new Color(60, 60, 60));
        slotPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.LOWERED),
                new EmptyBorder(15, 80, 15, 80)
        ));

        JLabel lblInsert = new JLabel("INSERT UANG", SwingConstants.CENTER);
        lblInsert.setForeground(new Color(200, 200, 200));
        lblInsert.setFont(new Font("Arial", Font.BOLD, 11));
        lblInsert.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel slotHole = new JPanel();
        slotHole.setPreferredSize(new Dimension(280, 10));
        slotHole.setMaximumSize(new Dimension(280, 10));
        slotHole.setBackground(SLOT_DARK);
        slotHole.setBorder(BorderFactory.createLineBorder(GLOW_GREEN, 2));

        slotPanel.add(lblInsert);
        slotPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        slotPanel.add(slotHole);
        return slotPanel;
    }

    private JButton createMoneyButton(int amount, Color color) {
        JButton btn = new JButton(formatCurrency(amount));
        btn.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 55)); 
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60), 1), 
            BorderFactory.createRaisedBevelBorder()
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) { btn.setBackground(color.brighter()); }
            @Override
            public void mouseExited(MouseEvent evt) { btn.setBackground(color); }
        });

        btn.addActionListener(e -> facade.insertMoney(amount));
        return btn;
    }

    // --- HALAMAN 2: PILIH PRODUK ---
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JPanel topContainer = new JPanel(new GridLayout(2, 1));
        topContainer.setBackground(BG_COLOR);
        JLabel title = new JLabel("Silakan Pilih Produk Anda", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);

        balanceLabelProductPage = new JLabel("Saldo Tersedia: Rp 0", SwingConstants.CENTER);
        balanceLabelProductPage.setFont(HEADER_FONT);
        balanceLabelProductPage.setForeground(new Color(41, 128, 185));
        balanceLabelProductPage.setBorder(new EmptyBorder(10, 0, 20, 0));

        topContainer.add(title);
        topContainer.add(balanceLabelProductPage);

        productGridPanel = new JPanel(new GridLayout(0, 4, 20, 20)); 
        productGridPanel.setBackground(BG_COLOR);
        JScrollPane scrollPane = new JScrollPane(productGridPanel);
        scrollPane.setBorder(new EmptyBorder(20, 20, 20, 20)); 
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setBackground(BG_COLOR);

        JButton btnDone = new JButton("SELESAI & AMBIL KEMBALIAN");
        btnDone.setFont(HEADER_FONT);
        btnDone.setBackground(Color.GRAY);
        btnDone.setForeground(Color.WHITE);
        btnDone.setPreferredSize(new Dimension(100, 70));
        btnDone.setFocusPainted(false);

        btnDone.addActionListener(e -> {
            String changeInfo = facade.finishAndGetChange();
            String htmlContent = generateReceiptHtml(changeInfo);

            JLabel receiptLabel = new JLabel(htmlContent);
            JOptionPane.showMessageDialog(this, receiptLabel, "Transaksi Selesai", JOptionPane.PLAIN_MESSAGE);
            
            purchasedItems.clear(); 
            cardLayout.show(mainPanel, PAGE_MONEY);
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnDone, BorderLayout.SOUTH);

        return panel;
    }

    private String generateReceiptHtml(String changeInfo) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='width: 350px; font-family: sans-serif; background-color: #ffffff; padding: 5px;'>");
        html.append("<div style='background-color: #6c5ce7; padding: 15px; text-align: center; color: white; border-radius: 8px 8px 0 0;'>");
        html.append("<h2 style='margin: 0;'>STRUK PEMBELIAN</h2>");
        html.append("<small style='color: #dfe6e9;'>Terima Kasih Telah Berbelanja</small>");
        html.append("</div>");
        html.append("<div style='border: 2px dashed #6c5ce7; border-top: 0; padding: 15px; background-color: #f8f9fa;'>");
        
        if (purchasedItems.isEmpty()) {
            html.append("<p style='text-align: center; color: #b2bec3;'><i>Tidak ada barang yang dibeli.</i></p>");
        } else {
            html.append("<ul style='margin-left: 10px; color: #2d3436;'>");
            for (String item : purchasedItems) {
                html.append("<li style='margin-bottom: 5px;'><b>").append(item).append("</b> <span style='color:green;'>[OK]</span></li>");
            }
            html.append("</ul>");
        }
        
        html.append("<div style='margin-top: 15px; padding: 10px; background-color: #fffce7; border: 1px solid #ffeaa7; border-radius: 5px;'>");
        html.append("<b style='color: #fdcb6e;'>DETAIL KEMBALIAN:</b><br>");
        html.append("<span style='font-family: monospace; color: #636e72;'>");
        html.append(changeInfo.replace("\n", "<br>"));
        html.append("</span>");
        html.append("</div></div>");
        html.append("<div style='text-align: center; margin-top: 5px; color: #b2bec3; font-size: 10px;'>Simulasi Vending Machine</div>");
        html.append("</body></html>");
        return html.toString();
    }

    private void refreshProductButtons() {
        productGridPanel.removeAll();
        List<Product> products = facade.getProductList();

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            int index = i;

            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBackground(Color.WHITE);
            productCard.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); 

            // 1.Item Name (Centered, Big)
            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 18));
            nameLabel.setForeground(TEXT_DARK);
            nameLabel.setBorder(new EmptyBorder(10, 5, 10, 5));

            // 2. Footer containing Price and Button
            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(Color.WHITE);
            footer.setBorder(new EmptyBorder(5, 5, 5, 5));

            String priceText = formatCurrency(p.getPrice());
            JLabel priceLabel = new JLabel(priceText + " | Stok: " + p.getQuantity(), SwingConstants.CENTER);
            priceLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
            priceLabel.setForeground(new Color(41, 128, 185));
            priceLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

            JButton btnBuy = new JButton();
            btnBuy.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
            btnBuy.setFocusPainted(false);
            btnBuy.setPreferredSize(new Dimension(100, 40)); 

            if (p.getQuantity() <= 0) {
                btnBuy.setText("HABIS");
                btnBuy.setBackground(Color.GRAY);
                btnBuy.setForeground(Color.WHITE);
                btnBuy.setEnabled(false); 
            } else {
                btnBuy.setText("BELI");
                btnBuy.setBackground(new Color(52, 152, 219));
                btnBuy.setForeground(Color.BLACK);
                btnBuy.setEnabled(true);

                btnBuy.addActionListener(e -> {
                    if(facade.buyProduct(index)) {
                         purchasedItems.add(p.getName());
                         JOptionPane.showMessageDialog(this,
                             "Berhasil membeli: " + p.getName() + "\nSilahkan ambil di baki pengambilan.",
                             "Sukses", JOptionPane.INFORMATION_MESSAGE);
                         refreshProductButtons(); 
                    }
                });
            }

            footer.add(priceLabel, BorderLayout.NORTH);
            footer.add(btnBuy, BorderLayout.SOUTH);

            productCard.add(nameLabel, BorderLayout.CENTER);
            productCard.add(footer, BorderLayout.SOUTH);

            productGridPanel.add(productCard);
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    // --- HALAMAN 3: ADMIN DASHBOARD ---
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JLabel title = new JLabel("ADMIN DASHBOARD - MANAJEMEN STOK", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);
        title.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Table
        String[] columns = {"No", "Nama Barang", "Harga (Rp)", "Stok"};
        tableModel = new DefaultTableModel(columns, 0);

        JTable productTable = new JTable(tableModel);
        productTable.setFont(NORMAL_FONT);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(HEADER_FONT);
        productTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout()); 
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_DARK), "Input Data Produk", 0, 0, HEADER_FONT, TEXT_DARK));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtName = new JTextField(20); txtName.setFont(NORMAL_FONT);
        txtPrice = new JTextField(20); txtPrice.setFont(NORMAL_FONT);
        txtQty = new JTextField(20); txtQty.setFont(NORMAL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nama Barang:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtName, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Harga (Angka):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtPrice, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Stok (Angka):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtQty, gbc);

        // CRUD Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(BG_COLOR);
        
        JButton btnAdd = createStyledButton("Tambah", SECONDARY_COLOR);
        JButton btnEdit = createStyledButton("Edit", Color.ORANGE);
        JButton btnDelete = createStyledButton("Hapus", Color.RED);
        JButton btnBack = createStyledButton("Keluar Admin", TEXT_DARK);

        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                int price = Integer.parseInt(txtPrice.getText());
                int qty = Integer.parseInt(txtQty.getText());
                facade.addProduct(name, price, qty); 
                refreshAdminTable();
                clearForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input Error!", TITLE_ERROR, JOptionPane.ERROR_MESSAGE); }
        });

        btnEdit.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                try {
                    String name = txtName.getText();
                    int price = Integer.parseInt(txtPrice.getText());
                    int qty = Integer.parseInt(txtQty.getText());
                    facade.editProduct(row, name, price, qty); 
                    refreshAdminTable();
                    clearForm();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input Error!", TITLE_ERROR, JOptionPane.ERROR_MESSAGE); }
            } else { JOptionPane.showMessageDialog(this, "Pilih baris dulu!"); }
        });

        btnDelete.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0 && JOptionPane.showConfirmDialog(this, "Yakin?") == JOptionPane.YES_OPTION) {
                facade.removeProduct(row);
                refreshAdminTable();
                clearForm();
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, PAGE_MONEY));

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting()) {
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString().replace(CURRENCY_RP, "").replace(".", ""));
                txtQty.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnBack);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(BG_COLOR);
        bottomContainer.add(formPanel, BorderLayout.NORTH);
        bottomContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomContainer, BorderLayout.SOUTH);

        return panel;
    }

    private void clearForm() {
        txtName.setText(""); txtPrice.setText(""); txtQty.setText("");
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(HEADER_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void refreshAdminTable() {
        tableModel.setRowCount(0);
        List<Product> products = facade.getProductList();
        for (int i = 0; i < products.size(); i++) {
            tableModel.addRow(new Object[]{
                i + 1, 
                products.get(i).getName(), 
                formatCurrency(products.get(i).getPrice()),
                products.get(i).getQuantity()
            });
        }
    }

    private void showAdminLogin() {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Masukkan Password Admin:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String pass = new String(pf.getPassword());
            if (facade.loginAdmin(pass)) {
                refreshAdminTable();
                cardLayout.show(mainPanel, PAGE_ADMIN);
            } else {
                JOptionPane.showMessageDialog(this, "Password Salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String formatCurrency(int amount) {
        return CURRENCY_RP + String.format("%,d", amount).replace(',', '.');
    }

    @Override
    public void onStateChanged(String message, int currentBalance) {
        String formattedBalance = formatCurrency(currentBalance);
        balanceLabelMoneyPage.setText("SALDO: " + formattedBalance);
        balanceLabelProductPage.setText("Saldo Tersedia: " + formattedBalance);
    }

    @Override
    public void onErrorOccurred(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, TITLE_ERROR, JOptionPane.ERROR_MESSAGE);
    }
}