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
import java.util.ArrayList;
import java.util.List;

public class VendingUI extends JFrame implements VendingObserver {
    private VendingFacade facade;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Referensi komponen untuk refresh data
    private JPanel productGridPanel;
    private JTable productTable;
    private DefaultTableModel tableModel;

    // Label Saldo (Digital Display)
    private JLabel balanceLabelMoneyPage;
    private JLabel balanceLabelProductPage;
    
    // List untuk menyimpan barang yang dibeli selama sesi ini
    private List<String> purchasedItems = new ArrayList<>();

    // --- PALET WARNA (KOMBINASI) ---
    private final Color BG_COLOR = new Color(245, 247, 250); // Background Putih Abu Soft
    private final Color SLOT_DARK = new Color(44, 62, 80);   // Dark Blue-Grey (Slot Mesin)
    private final Color GLOW_GREEN = new Color(46, 204, 113);// Hijau Neon (Indikator)
    private final Color TEXT_COLOR = new Color(52, 73, 94);  // Teks Utama (TEXT_DARK)
    private final Color TEXT_DARK = new Color(52, 73, 94);   // Alias untuk TEXT_COLOR
    private final Color SECONDARY_COLOR = new Color(52, 152, 219); // Biru Terang (Untuk Admin)
    
    // --- WARNA UANG RUPIAH (PASTEL/SOFT) ---
    private final Color COLOR_5K = new Color(193, 140, 93);  // Orange 
    private final Color COLOR_10K = new Color(149, 117, 205);// Light Purple
    private final Color COLOR_20K = new Color(129, 199, 132);// Light Green
    private final Color COLOR_50K = new Color(79, 195, 247); // Ligth Blue

    // Font
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font DIGITAL_FONT = new Font("Monospaced", Font.BOLD, 22); 

    public VendingUI() {
        this.facade = new VendingFacade();
        this.facade.registerObserver(this);
        initUI();
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
    }

    private void initUI() {
        setTitle("Simulasi Vending Machine Modern");
        setSize(1000, 750); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR); 

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(null);

        mainPanel.add(createMoneyPanel(), "PAGE_MONEY");
        mainPanel.add(createProductPanel(), "PAGE_PRODUCT");
        mainPanel.add(createAdminPanel(), "PAGE_ADMIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "PAGE_MONEY");
    }

    // --- HALAMAN 1: MASUKKAN UANG ---
    private JPanel createMoneyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // 1. Header 
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_COLOR);
        header.setBorder(new EmptyBorder(30, 40, 10, 40));

        JLabel title = new JLabel("Selamat Datang! Silakan Masukkan Uang", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_DARK);

        JButton btnAdmin = new JButton("TM Admin");
        btnAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnAdmin.setBackground(Color.LIGHT_GRAY);
        btnAdmin.setFocusable(false);
        btnAdmin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdmin.addActionListener(e -> showAdminLogin());

        JPanel adminWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        adminWrapper.setBackground(BG_COLOR);
        adminWrapper.add(btnAdmin);

        header.add(title, BorderLayout.CENTER);
        header.add(adminWrapper, BorderLayout.EAST);

        // 2. Center (Slot & Tombol)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();

        // Slot Bill Acceptor
        JPanel slotPanel = new JPanel();
        slotPanel.setLayout(new BoxLayout(slotPanel, BoxLayout.Y_AXIS));
        slotPanel.setBackground(SLOT_DARK);
        slotPanel.setBorder(new CompoundBorder(
                new BevelBorder(BevelBorder.RAISED),
                new EmptyBorder(15, 60, 15, 60)
        ));

        JLabel lblInsert = new JLabel("INSERT BILL HERE", SwingConstants.CENTER);
        lblInsert.setForeground(Color.LIGHT_GRAY);
        lblInsert.setFont(new Font("Arial", Font.BOLD, 10));
        lblInsert.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel slotHole = new JPanel();
        slotHole.setPreferredSize(new Dimension(250, 8));
        slotHole.setMaximumSize(new Dimension(250, 8));
        slotHole.setBackground(Color.BLACK);
        slotHole.setBorder(BorderFactory.createLineBorder(GLOW_GREEN, 1));

        slotPanel.add(lblInsert);
        slotPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        slotPanel.add(slotHole);

        // Tombol Uang
        JPanel moneyGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        moneyGrid.setBackground(BG_COLOR);
        moneyGrid.setBorder(new EmptyBorder(30, 0, 0, 0));

        moneyGrid.add(createMoneyButton(5000, COLOR_5K));
        moneyGrid.add(createMoneyButton(10000, COLOR_10K));
        moneyGrid.add(createMoneyButton(20000, COLOR_20K));
        moneyGrid.add(createMoneyButton(50000, COLOR_50K));

        gbc.gridx = 0; gbc.gridy = 0; centerPanel.add(slotPanel, gbc);
        gbc.gridx = 0; gbc.gridy = 1; centerPanel.add(moneyGrid, gbc);

        // 3. Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BG_COLOR);
        footer.setBorder(new EmptyBorder(20, 150, 50, 150)); 

        balanceLabelMoneyPage = new JLabel("SALDO: Rp 0", SwingConstants.CENTER);
        balanceLabelMoneyPage.setFont(DIGITAL_FONT); 
        balanceLabelMoneyPage.setForeground(GLOW_GREEN);
        balanceLabelMoneyPage.setOpaque(true);
        balanceLabelMoneyPage.setBackground(Color.BLACK);
        balanceLabelMoneyPage.setBorder(new LineBorder(new Color(60, 60, 60), 3));
        balanceLabelMoneyPage.setPreferredSize(new Dimension(0, 50)); 

        JButton btnNext = new JButton("PILIH PRODUK SEDIAAN >>");
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        btnNext.setBackground(GLOW_GREEN);
        btnNext.setForeground(Color.WHITE);
        btnNext.setFocusPainted(false);
        btnNext.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnNext.addActionListener(e -> {
            if (facade.getCurrentBalance() > 0) {
                refreshProductButtons(); 
                cardLayout.show(mainPanel, "PAGE_PRODUCT");
            } else {
                JOptionPane.showMessageDialog(this, "Mohon masukkan uang ke slot terlebih dahulu.", "Info", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel saldoWrapper = new JPanel(new BorderLayout());
        saldoWrapper.setBackground(BG_COLOR);
        saldoWrapper.add(balanceLabelMoneyPage, BorderLayout.CENTER);
        saldoWrapper.setBorder(new EmptyBorder(0, 50, 20, 50)); 

        footer.add(saldoWrapper, BorderLayout.NORTH);
        footer.add(btnNext, BorderLayout.SOUTH);

        panel.add(header, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createMoneyButton(int amount, Color color) {
        JButton btn = new JButton("Rp " + String.format("%,d", amount).replace(',', '.'));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 60)); 
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='width: 350px; font-family: sans-serif; background-color: #ffffff; padding: 5px;'>");
            html.append("<div style='background-color: #6c5ce7; padding: 15px; text-align: center; color: white; border-radius: 8px 8px 0 0;'>");
            html.append("<h2 style='margin: 0;'>STRUK PEMBELIAN</h2>");
            html.append("<small style='color: #dfe6e9;'>Terima Kasih Telah Berbelanja</small>");
            html.append("</div>");
            html.append("<div style='border: 2px dashed #6c5ce7; border-top: 0; padding: 15px; background-color: #f8f9fa;'>");
            
            if (purchasedItems.isEmpty()) {
                html.append("<p style='text-align: center; color: #b2bec3;'><i>Zonk! Tidak ada barang yang dibeli.</i></p>");
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

            JLabel receiptLabel = new JLabel(html.toString());
            JOptionPane.showMessageDialog(this, receiptLabel, "Transaksi Selesai", JOptionPane.PLAIN_MESSAGE);
            
            purchasedItems.clear(); 
            cardLayout.show(mainPanel, "PAGE_MONEY");
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnDone, BorderLayout.SOUTH);

        return panel;
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

            JLabel nameLabel = new JLabel(p.getName(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setBorder(new EmptyBorder(15, 5, 5, 5));

            JLabel imagePlaceholder = new JLabel("[GAMBAR]", SwingConstants.CENTER);
            imagePlaceholder.setFont(new Font("Arial", Font.ITALIC, 12));
            imagePlaceholder.setForeground(Color.GRAY);
            imagePlaceholder.setPreferredSize(new Dimension(100, 100));
            imagePlaceholder.setBorder(BorderFactory.createLineBorder(BG_COLOR, 1));

            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(Color.WHITE);
            JLabel priceLabel = new JLabel("Rp " + String.format("%,d", p.getPrice()).replace(',', '.'), SwingConstants.CENTER);
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(new Color(41, 128, 185));
            priceLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

            JButton btnBuy = new JButton("BELI");
            btnBuy.setBackground(new Color(52, 152, 219));
            btnBuy.setForeground(Color.WHITE);
            btnBuy.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnBuy.setFocusPainted(false);
            btnBuy.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0)); 

            btnBuy.addActionListener(e -> {
                if(facade.buyProduct(index)) {
                     purchasedItems.add(p.getName());
                     JOptionPane.showMessageDialog(this,
                         "Berhasil membeli: " + p.getName() + "\nSilakan ambil di baki pengambilan.",
                         "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            footer.add(priceLabel, BorderLayout.NORTH);
            footer.add(btnBuy, BorderLayout.SOUTH);
            productCard.add(nameLabel, BorderLayout.NORTH);
            productCard.add(imagePlaceholder, BorderLayout.CENTER);
            productCard.add(footer, BorderLayout.SOUTH);
            productGridPanel.add(productCard);
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    // --- HALAMAN 3: ADMIN DASHBOARD 
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        JLabel title = new JLabel("ADMIN DASHBOARD - MANAJEMEN STOK", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_COLOR);
        title.setBorder(new EmptyBorder(10, 0, 20, 0));

        // Tabel dengan gaya header
        String[] columns = {"No", "Nama Barang", "Harga (Rp)"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        productTable.setFont(NORMAL_FONT);
        productTable.setRowHeight(30);
        productTable.getTableHeader().setFont(HEADER_FONT);
        productTable.getTableHeader().setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Form Input
        JPanel formPanel = new JPanel(new GridBagLayout()); 
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(TEXT_COLOR), "Input Data Produk", 0, 0, HEADER_FONT, TEXT_COLOR));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblName = new JLabel("Nama Barang:"); lblName.setFont(NORMAL_FONT);
        JTextField txtName = new JTextField(20); txtName.setFont(NORMAL_FONT);
        JLabel lblPrice = new JLabel("Harga (Angka):"); lblPrice.setFont(NORMAL_FONT);
        JTextField txtPrice = new JTextField(20); txtPrice.setFont(NORMAL_FONT);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lblName, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtName, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(lblPrice, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtPrice, gbc);

        // Tombol CRUD
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        btnPanel.setBackground(BG_COLOR);
        JButton btnAdd = createStyledButton("Tambah", SECONDARY_COLOR); btnAdd.setFont(NORMAL_FONT);
        JButton btnEdit = createStyledButton("Edit", Color.ORANGE); btnEdit.setFont(NORMAL_FONT); btnEdit.setForeground(TEXT_COLOR);
        JButton btnDelete = createStyledButton("Hapus", Color.RED); btnDelete.setFont(NORMAL_FONT);
        JButton btnBack = createStyledButton("Keluar Admin", TEXT_COLOR); btnBack.setFont(NORMAL_FONT);

        // Logic Tambah
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                int price = Integer.parseInt(txtPrice.getText());
                facade.addProduct(name, price);
                refreshAdminTable();
                txtName.setText(""); txtPrice.setText("");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input Harga harus angka!", "Error", JOptionPane.ERROR_MESSAGE); }
        });

        // Logic Edit
        btnEdit.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                try {
                    String name = txtName.getText();
                    int price = Integer.parseInt(txtPrice.getText());
                    facade.editProduct(row, name, price);
                    refreshAdminTable();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input salah!", "Error", JOptionPane.ERROR_MESSAGE); }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
            }
        });

        // Logic Hapus
        btnDelete.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus produk ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    facade.removeProduct(row);
                    refreshAdminTable();
                    txtName.setText(""); txtPrice.setText("");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "PAGE_MONEY"));

        // Auto-fill saat klik tabel
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0 && !e.getValueIsAdjusting()) {
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString().replace("Rp ", "").replace(".", ""));
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
            tableModel.addRow(new Object[]{i + 1, products.get(i).getName(), "Rp " + String.format("%,d", products.get(i).getPrice()).replace(',', '.')});
        }
    }

    private void showAdminLogin() {
        JPasswordField pf = new JPasswordField();
        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Masukkan Password Admin:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okCxl == JOptionPane.OK_OPTION) {
            String pass = new String(pf.getPassword());
            if (facade.loginAdmin(pass)) {
                refreshAdminTable();
                cardLayout.show(mainPanel, "PAGE_ADMIN");
            } else {
                JOptionPane.showMessageDialog(this, "Password Salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onStateChanged(String message, int currentBalance) {
        String formattedBalance = "SALDO: Rp " + String.format("%,d", currentBalance).replace(',', '.');
        balanceLabelMoneyPage.setText(formattedBalance);
        balanceLabelProductPage.setText("Saldo Tersedia: " + formattedBalance.replace("SALDO: ", ""));
    }

    @Override
    public void onErrorOccurred(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}