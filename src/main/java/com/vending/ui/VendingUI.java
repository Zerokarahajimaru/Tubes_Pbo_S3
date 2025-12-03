package com.vending.ui;

import com.vending.model.Product;
import com.vending.patterns.VendingFacade;
import com.vending.patterns.VendingObserver;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VendingUI extends JFrame implements VendingObserver {
    private VendingFacade facade;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Referensi komponen untuk refresh data
    private JPanel productGridPanel; 
    private JTable productTable;
    private DefaultTableModel tableModel;

    private JTextField txtName;
    private JTextField txtPrice;
    private JTextField txtQty; // Field baru untuk Stok

    // Label Saldo
    private JLabel balanceLabelMoneyPage;
    private JLabel balanceLabelProductPage;

    public VendingUI() {
        this.facade = new VendingFacade();
        this.facade.registerObserver(this);
        initUI();
    }

    private void initUI() {
        setTitle("Vending Machine + Stok Database");
        setSize(900, 700); // Sedikit diperbesar agar muat
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMoneyPanel(), "PAGE_MONEY");
        mainPanel.add(createProductPanel(), "PAGE_PRODUCT");
        mainPanel.add(createAdminPanel(), "PAGE_ADMIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "PAGE_MONEY");
    }

    // --- HALAMAN 1: MASUKKAN UANG ---
    private JPanel createMoneyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Langkah 1: Masukkan Uang", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton btnAdmin = new JButton("Setting Vending Machine");
        btnAdmin.setFocusable(false);
        btnAdmin.addActionListener(e -> showAdminLogin());
        
        header.add(title, BorderLayout.CENTER);
        header.add(btnAdmin, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tombol Uang
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
        int[] validMoney = {5000, 10000, 20000, 50000};
        
        for (int m : validMoney) {
            JButton btn = new JButton("Rp " + m);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(e -> facade.insertMoney(m));
            buttonPanel.add(btn);
        }

        // Footer
        JPanel bottomPanel = new JPanel(new BorderLayout());
        balanceLabelMoneyPage = new JLabel("Saldo Saat Ini: Rp 0", SwingConstants.CENTER);
        balanceLabelMoneyPage.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JButton btnNext = new JButton("LANJUT PILIH BARANG >>");
        btnNext.setFont(new Font("Arial", Font.BOLD, 16));
        btnNext.setBackground(new Color(50, 150, 250));
        btnNext.setForeground(Color.WHITE);
        btnNext.setPreferredSize(new Dimension(100, 50));
        
        btnNext.addActionListener(e -> {
            if (facade.getCurrentBalance() > 0) {
                refreshProductButtons(); // Refresh tombol agar stok terbaru muncul
                cardLayout.show(mainPanel, "PAGE_PRODUCT");
            } else {
                JOptionPane.showMessageDialog(this, "Masukkan uang dulu!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        bottomPanel.add(balanceLabelMoneyPage, BorderLayout.NORTH);
        bottomPanel.add(btnNext, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(header, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // --- HALAMAN 2: PILIH PRODUK ---
    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Langkah 2: Pilih Produk", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        balanceLabelProductPage = new JLabel("Sisa Saldo: Rp 0", SwingConstants.CENTER);
        balanceLabelProductPage.setFont(new Font("Arial", Font.BOLD, 16));
        
        JPanel topContainer = new JPanel(new GridLayout(2,1));
        topContainer.add(title);
        topContainer.add(balanceLabelProductPage);

        // Grid Produk
        productGridPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        productGridPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JScrollPane scrollPane = new JScrollPane(productGridPanel);

        // Tombol Selesai
        JButton btnDone = new JButton("SELESAI & AMBIL KEMBALIAN");
        btnDone.setFont(new Font("Arial", Font.BOLD, 16));
        btnDone.setBackground(new Color(50, 200, 100));
        btnDone.setForeground(Color.WHITE);
        btnDone.setPreferredSize(new Dimension(100, 60));

        btnDone.addActionListener(e -> {
            String change = facade.finishAndGetChange();
            JOptionPane.showMessageDialog(this, change, "Transaksi Selesai", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "PAGE_MONEY");
        });

        panel.add(topContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnDone, BorderLayout.SOUTH);

        return panel;
    }

    // --- HELPER: Refresh Tombol Produk ---
    private void refreshProductButtons() {
        productGridPanel.removeAll();
        List<Product> products = facade.getProductList();
        
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            
            // <--- UPDATE: Tampilkan Stok di Tombol
            String label = "<html><center>" + p.getName() + 
                           "<br>Rp" + p.getPrice() + 
                           "<br><b>Stok: " + p.getQuantity() + "</b></center></html>";
            
            JButton btn = new JButton(label);
            int index = i;
            
            // <--- UPDATE: Cek Stok Habis
            if (p.getQuantity() <= 0) {
                btn.setEnabled(false); // Matikan tombol
                btn.setBackground(Color.GRAY);
                btn.setText("<html><center>" + p.getName() + "<br>HABIS</center></html>");
            } else {
                // Logic beli normal
                btn.addActionListener(e -> {
                    if(facade.buyProduct(index)) {
                         JOptionPane.showMessageDialog(this, "Berhasil membeli: " + p.getName());
                         refreshProductButtons(); // <--- PENTING: Refresh agar angka stok berkurang di layar langsung
                    }
                });
            }
            productGridPanel.add(btn);
        }
        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    // --- HALAMAN 3: ADMIN DASHBOARD ---
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("ADMIN DASHBOARD - STOK MANAGEMENT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

        // Tabel: Tambah kolom Stok
        String[] columns = {"No", "Nama Barang", "Harga", "Stok"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);

        // Form Input
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Row ditambah jadi 4
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField(); // <--- Field Stok Baru
        
        formPanel.add(new JLabel("Nama Barang:")); formPanel.add(txtName);
        formPanel.add(new JLabel("Harga (Angka):")); formPanel.add(txtPrice);
        formPanel.add(new JLabel("Stok Awal:")); formPanel.add(txtQty); // <--- Label Stok
        formPanel.setBorder(BorderFactory.createTitledBorder("Input Data"));

        // Tombol CRUD
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");
        JButton btnBack = new JButton("Keluar Admin");
        btnBack.setBackground(Color.RED);
        btnBack.setForeground(Color.WHITE);

        // Logic Tambah
        btnAdd.addActionListener(e -> {
            try {
                String name = txtName.getText();
                int price = Integer.parseInt(txtPrice.getText());
                int qty = Integer.parseInt(txtQty.getText()); // <--- Ambil Stok
                
                facade.addProduct(name, price, qty); // Panggil facade method baru
                refreshAdminTable();
                clearForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input Harga/Stok harus angka!"); }
        });

        // Logic Edit
        btnEdit.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                try {
                    String name = txtName.getText();
                    int price = Integer.parseInt(txtPrice.getText());
                    int qty = Integer.parseInt(txtQty.getText()); // <--- Ambil Stok
                    
                    facade.editProduct(row, name, price, qty); // Panggil facade method baru
                    refreshAdminTable();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Input salah!"); }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
            }
        });

        // Logic Hapus
        btnDelete.addActionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus?");
                if (confirm == JOptionPane.YES_OPTION) {
                    facade.removeProduct(row);
                    refreshAdminTable();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih baris dulu!");
            }
        });

        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "PAGE_MONEY"));

        // Auto-fill saat klik tabel (Listener)
        productTable.getSelectionModel().addListSelectionListener(e -> {
            int row = productTable.getSelectedRow();
            if (row >= 0) {
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                txtQty.setText(tableModel.getValueAt(row, 3).toString()); // <--- Isi Field Stok
            }
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnBack);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.add(formPanel, BorderLayout.NORTH);
        bottomContainer.add(btnPanel, BorderLayout.SOUTH);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomContainer, BorderLayout.SOUTH);
        
        return panel;
    }

    private void clearForm() {
        txtName.setText(""); 
        txtPrice.setText(""); 
        txtQty.setText("");
    }

    private void refreshAdminTable() {
        tableModel.setRowCount(0);
        List<Product> products = facade.getProductList();
        for (int i = 0; i < products.size(); i++) {
            tableModel.addRow(new Object[]{
                i + 1, 
                products.get(i).getName(), 
                products.get(i).getPrice(),
                products.get(i).getQuantity() // <--- Tampilkan Stok di Tabel
            });
        }
    }

    private void showAdminLogin() {
        String pass = JOptionPane.showInputDialog(this, "Masukkan Password Admin:");
        if (pass != null && facade.loginAdmin(pass)) {
            refreshAdminTable();
            cardLayout.show(mainPanel, "PAGE_ADMIN");
        } else if (pass != null) {
            JOptionPane.showMessageDialog(this, "Password Salah!", "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onStateChanged(String message, int currentBalance) {
        balanceLabelMoneyPage.setText("Saldo Saat Ini: Rp " + currentBalance);
        balanceLabelProductPage.setText("Sisa Saldo: Rp " + currentBalance);
    }

    @Override
    public void onErrorOccurred(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
}