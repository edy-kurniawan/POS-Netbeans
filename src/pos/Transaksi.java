/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pos;

import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.proteanit.sql.DbUtils;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author EDYKURNIAWAN-PC
 */
public class Transaksi extends javax.swing.JFrame {
    Connection koneksi;
    PreparedStatement pst, pst2;
    ResultSet rst;
    int istok = 0, istok2 = 0, iharga, ijumlah, kstok = 0, tstok = 0;
    String harga, barang, dbarang, KD, jam, tanggal,ssub, dkode;
    /**
     * Creates new form Login
     */
    public Transaksi() {
        initComponents();
        koneksi=database.koneksiDB();
        delay();
        detail();    
        autonumber();
        sum();
    }
    
    private void simpan(){
        String tgl=jtgl.getText();
        String jam=jjam.getText();
      try {
            String sql="insert into transaksi (Kode_Transaksi,Kode_Detail,Tanggal,Jam,Total) value (?,?,?,?,?)";
            pst=koneksi.prepareStatement(sql);
            pst.setString(1, jkodetransaksi.getText());
            pst.setString(2, KD);
            pst.setString(3, tgl);
            pst.setString(4, jam);
            pst.setString(5, jtotal.getText());
            pst.execute();
            JOptionPane.showMessageDialog(null, "Data Tersimpan");
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
            }
    }
    
    private void total(){
    int total, bayar, kembali;
        total= Integer.parseInt(jtotal.getText());
        bayar= Integer.parseInt(jbayar.getText());
        kembali=bayar-total;
        String ssub=String.valueOf(kembali);
        jkembalian.setText(ssub);
    }
    
    private void subtotal(){
    int diskon, jumlah, sub;
            if (jdis.getText().equals("")) {diskon=0;}
            else {diskon= Integer.parseInt(jdis.getText());}
         jumlah= Integer.parseInt(jqty.getText());
         sub=(jumlah*iharga)-diskon;
         ssub=String.valueOf(sub);     
    }
    
    public void kurangi_stok(){
    int qty;
    qty=Integer.parseInt(jqty.getText());
    kstok=istok-qty;
    }
    
    
    public void tambah_stok(){
    ambil_stock();
    tstok=ijumlah+istok2;
        try {
        String update="update barang set Stok='"+tstok+"' where Kode_Barang='"+dbarang+"'";
        pst2=koneksi.prepareStatement(update);
        pst2.execute();
        }catch (Exception e){JOptionPane.showMessageDialog(null, e);}
    }
    
    public void ambil_stock(){
    try {
    String sql="select Stok from barang where Kode_Barang='"+barang+"'";
    pst=koneksi.prepareStatement(sql);
    rst=pst.executeQuery();
    if (rst.next()) {    
    String stok=rst.getString(("Stok"));
    istok2= Integer.parseInt(stok);
    }
    }catch (Exception e) {JOptionPane.showMessageDialog(null, e);}
    }
    
    public void sum(){
        int totalBiaya = 0;
        int subtotal;
        DefaultTableModel dataModel = (DefaultTableModel) jTable1.getModel();
        int jumlah = jTable1.getRowCount();
        for (int i=0; i<jumlah; i++){
        subtotal = Integer.parseInt(dataModel.getValueAt(i, 5).toString());
        totalBiaya += subtotal;
        }
        jtotal.setText(String.valueOf(totalBiaya));
    }
    
     public void detail(){
    try {
        String Kode_detail=jkodetransaksi.getText();
        String KD="D"+Kode_detail;
        String sql="SELECT detail_barang.Kode_Barang, barang.Nama_Barang, detail_barang.Harga, detail_barang.Jumlah, detail_barang.Discount, detail_barang.Subtotal FROM detail_barang LEFT OUTER JOIN barang ON detail_barang.Kode_Barang = barang.Kode_Barang where detail_barang.Kode_Detail='"+KD+"'";
        pst=koneksi.prepareStatement(sql);
        rst=pst.executeQuery();
        jTable1.setModel(DbUtils.resultSetToTableModel(rst));
       } catch (Exception e){ JOptionPane.showMessageDialog(null, e);} 
    }
    
    public void autonumber(){
    try{
        String sql = "SELECT MAX(RIGHT(Kode_Transaksi,3)) AS NO FROM transaksi";
        pst=koneksi.prepareStatement(sql);
        rst=pst.executeQuery();
        while (rst.next()) {
                if (rst.first() == false) {
                    jkodetransaksi.setText("TRX001");
                } else {
                    rst.last();
                    int auto_id = rst.getInt(1) + 1;
                    String no = String.valueOf(auto_id);
                    int NomorJual = no.length();
                    for (int j = 0; j < 3 - NomorJual; j++) {
                        no = "0" + no;
                    }
                    jkodetransaksi.setText("TRX" + no);
                }
            }
        rst.close();
        }catch (Exception e){JOptionPane.showMessageDialog(null, e);}
    }
    
     public void delay(){
    Thread clock=new Thread(){
        public void run(){
            for(;;){
                Calendar cal=Calendar.getInstance();
                SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat format2=new SimpleDateFormat("yyyy-MM-dd");
                jjam.setText(format.format(cal.getTime()));
                jtgl.setText(format2.format(cal.getTime()));
                
            try { sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Transaksi.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
      };
    clock.start();
    }
     
    public void cari(){
    try {
        String sql="select * from barang where Nama_Barang LIKE '%"+jcari.getText()+"%'";
        pst=koneksi.prepareStatement(sql);
        rst=pst.executeQuery();
        jTable2.setModel(DbUtils.resultSetToTableModel(rst));
       } catch (Exception e){ JOptionPane.showMessageDialog(null, e);} 
    }
    
    
    private void clsr(){
        jcari.setText("");
        jqty.setText("");
        jdis.setText("");
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jkembalian = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jkodetransaksi = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jcari = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jqty = new javax.swing.JTextField();
        jtotal = new javax.swing.JTextField();
        jbayar = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jdis = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jjam = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jtgl = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(52, 152, 219));
        jPanel1.setLayout(null);

        jLabel4.setFont(new java.awt.Font("Constantia", 1, 27)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Point Of Sales");
        jPanel1.add(jLabel4);
        jLabel4.setBounds(30, 30, 200, 50);

        jLabel1.setFont(new java.awt.Font("Century Gothic", 1, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("____________");
        jPanel1.add(jLabel1);
        jLabel1.setBounds(10, 40, 220, 40);

        jPanel5.setBackground(new java.awt.Color(116, 185, 255));
        jPanel5.setLayout(null);

        jLabel15.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Transaksi");
        jPanel5.add(jLabel15);
        jLabel15.setBounds(50, 10, 150, 30);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/store.png"))); // NOI18N
        jPanel5.add(jLabel11);
        jLabel11.setBounds(10, 0, 35, 50);

        jPanel1.add(jPanel5);
        jPanel5.setBounds(0, 160, 250, 50);

        jPanel6.setBackground(new java.awt.Color(41, 128, 185));
        jPanel6.setLayout(null);

        jLabel3.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Logout");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        jPanel6.add(jLabel3);
        jLabel3.setBounds(50, 10, 150, 40);

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_lock_96px_1.png"))); // NOI18N
        jPanel6.add(jLabel8);
        jLabel8.setBounds(10, 0, 40, 50);

        jPanel1.add(jPanel6);
        jPanel6.setBounds(0, 480, 250, 50);

        jLabel7.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Laporan Transaksi");
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel7);
        jLabel7.setBounds(50, 270, 170, 30);

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_ledger_96px.png"))); // NOI18N
        jPanel1.add(jLabel9);
        jLabel9.setBounds(10, 260, 35, 50);

        jLabel12.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Laporan Barang");
        jLabel12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel12MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel12);
        jLabel12.setBounds(50, 220, 150, 30);

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_report_card_96px.png"))); // NOI18N
        jPanel1.add(jLabel13);
        jLabel13.setBounds(10, 210, 35, 50);

        jLabel21.setFont(new java.awt.Font("Gabriola", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Edy Kurniawan");
        jLabel21.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.add(jLabel21);
        jLabel21.setBounds(70, 70, 100, 40);

        jLabel5.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Data Barang");
        jLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel5MouseClicked(evt);
            }
        });
        jPanel1.add(jLabel5);
        jLabel5.setBounds(50, 120, 150, 30);

        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/barang.png"))); // NOI18N
        jPanel1.add(jLabel25);
        jLabel25.setBounds(10, 110, 40, 50);

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 250, 532));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(null);

        jLabel16.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Disc");
        jPanel2.add(jLabel16);
        jLabel16.setBounds(540, 40, 30, 30);

        jkembalian.setEditable(false);
        jkembalian.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jkembalian.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jkembalian);
        jkembalian.setBounds(570, 330, 120, 30);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel2.add(jScrollPane1);
        jScrollPane1.setBounds(10, 200, 460, 160);

        jButton1.setBackground(new java.awt.Color(52, 152, 219));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_cash_in_hand_96px.png"))); // NOI18N
        jButton1.setText("Bayar");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1);
        jButton1.setBounds(260, 160, 80, 30);

        jkodetransaksi.setEditable(false);
        jkodetransaksi.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jkodetransaksi.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jkodetransaksi);
        jkodetransaksi.setBounds(120, 160, 130, 30);

        jLabel17.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel17.setText("Kode Transaksi");
        jPanel2.add(jLabel17);
        jLabel17.setBounds(10, 160, 110, 30);

        jLabel18.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel18.setText("Cari Barang :");
        jPanel2.add(jLabel18);
        jLabel18.setBounds(10, 10, 110, 30);

        jLabel19.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jLabel19.setText("Total :");
        jPanel2.add(jLabel19);
        jLabel19.setBounds(480, 240, 70, 30);

        jcari.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jcari.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jcari);
        jcari.setBounds(100, 10, 170, 30);

        jTable2.setAutoCreateRowSorter(true);
        jTable2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.setGridColor(new java.awt.Color(255, 255, 255));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jPanel2.add(jScrollPane2);
        jScrollPane2.setBounds(10, 50, 460, 70);

        jqty.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jqty.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jqty);
        jqty.setBounds(490, 70, 40, 30);

        jtotal.setEditable(false);
        jtotal.setFont(new java.awt.Font("Book Antiqua", 1, 24)); // NOI18N
        jtotal.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jtotal);
        jtotal.setBounds(540, 230, 150, 50);

        jbayar.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jbayar.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jbayar);
        jbayar.setBounds(540, 290, 150, 30);

        jLabel20.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel20.setText("Kembalian :");
        jPanel2.add(jLabel20);
        jLabel20.setBounds(480, 330, 90, 30);

        jLabel23.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel23.setText("Data Transaksi :");
        jPanel2.add(jLabel23);
        jLabel23.setBounds(10, 130, 110, 30);

        jLabel24.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel24.setText("Bayar :");
        jPanel2.add(jLabel24);
        jLabel24.setBounds(480, 290, 50, 30);

        jButton2.setBackground(new java.awt.Color(52, 152, 219));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_search_96px.png"))); // NOI18N
        jButton2.setText("Cari");
        jButton2.setBorder(null);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2);
        jButton2.setBounds(280, 10, 70, 30);

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_cancel_96px.png"))); // NOI18N
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel14);
        jLabel14.setBounds(350, 160, 30, 30);

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/icons8_plus_96px.png"))); // NOI18N
        jLabel26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel26MouseClicked(evt);
            }
        });
        jPanel2.add(jLabel26);
        jLabel26.setBounds(590, 70, 30, 30);

        jLabel27.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Qty");
        jPanel2.add(jLabel27);
        jLabel27.setBounds(490, 40, 30, 30);

        jdis.setFont(new java.awt.Font("Book Antiqua", 1, 14)); // NOI18N
        jdis.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.add(jdis);
        jdis.setBounds(540, 70, 40, 30);

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 162, 710, 370));

        jPanel3.setBackground(new java.awt.Color(41, 128, 185));

        jLabel6.setFont(new java.awt.Font("Book Antiqua", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Form Transaksi __________________");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(98, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 530, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 60, 710, 110));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(null);

        jjam.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jjam.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jjam.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.add(jjam);
        jjam.setBounds(570, 10, 130, 40);

        jLabel22.setFont(new java.awt.Font("Gabriola", 0, 18)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Aplikasi Transaksi Penjualan");
        jLabel22.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.add(jLabel22);
        jLabel22.setBounds(10, 10, 170, 40);

        jtgl.setFont(new java.awt.Font("Book Antiqua", 1, 18)); // NOI18N
        jtgl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jtgl.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel4.add(jtgl);
        jtgl.setBounds(480, 10, 120, 40);

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 0, 710, 100));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        String Kode_detail=jkodetransaksi.getText();
        String KD="D"+Kode_detail;
        try {
    int row=jTable1.getSelectedRow();
    dbarang=(jTable1.getModel().getValueAt(row, 0).toString());
    String sql="select * from detail_barang where Kode_Barang='"+dbarang+"' and Kode_Detail='"+KD+"'";
    pst=koneksi.prepareStatement(sql);
    rst=pst.executeQuery();
    if (rst.next()) {   
    String jumlah=rst.getString(("Jumlah"));
    ijumlah= Integer.parseInt(jumlah);
    }
}catch (Exception e) {JOptionPane.showMessageDialog(null, e);}
     ambil_stock();
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      total();
      simpan();
      autonumber();
      detail();
      jtotal.setText("");
      jkembalian.setText("");
      jbayar.setText("");
      clsr();
      cari();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
    try {
    int row=jTable2.getSelectedRow();
    String tabel_klik=(jTable2.getModel().getValueAt(row, 0).toString());
    String sql="select * from barang where Kode_Barang='"+tabel_klik+"'";
    pst=koneksi.prepareStatement(sql);
    rst=pst.executeQuery();
    if (rst.next()) {
    barang=rst.getString(("Kode_Barang"));    
    String stok=rst.getString(("Stok"));
    istok= Integer.parseInt(stok);
    harga=rst.getString(("Harga"));
    iharga= Integer.parseInt(harga);
    }
    }catch (Exception e) {JOptionPane.showMessageDialog(null, e);}
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cari();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jLabel26MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel26MouseClicked
        subtotal();
        kurangi_stok();
        try {
        String diskon;
            if (jdis.getText().equals("")) {diskon="0";}
            else {diskon=jdis.getText();}
        String Kode_detail=jkodetransaksi.getText();
        KD="D"+Kode_detail;
            String sql="insert into detail_barang (Kode_Detail,Kode_Barang,Harga,Jumlah,Discount,Subtotal) value (?,?,?,?,?,?)";
            String update="update barang set Stok='"+kstok+"' where Kode_Barang='"+barang+"'";
            pst=koneksi.prepareStatement(sql);
            pst2=koneksi.prepareStatement(update);
            pst.setString(1, KD);
            pst.setString(2, barang);
            pst.setString(3, harga);
            pst.setString(4, jqty.getText());
            pst.setString(5, diskon);
            pst.setString(6, ssub);
            pst.execute();
            pst2.execute();
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
            }
        detail();
        clsr();
        cari();
        sum();
    }//GEN-LAST:event_jLabel26MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        String Kode_detail=jkodetransaksi.getText();
        String KD="D"+Kode_detail; 
        try {
            String sql="delete from detail_barang where Kode_Barang=? and Kode_Detail=?";
            pst=koneksi.prepareStatement(sql);
            pst.setString(1, dbarang);
            pst.setString(2, KD);
            pst.execute();
        }catch (Exception e){JOptionPane.showMessageDialog(null, e);}
        detail();
        sum();
        tambah_stok();
        cari();
        sum();
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel5MouseClicked
        new Barang().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel5MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        new Login().setVisible(true);
        dispose();
    }//GEN-LAST:event_jLabel3MouseClicked

    private void jLabel12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel12MouseClicked
        try {
        String path="src/pos/barang.jasper";
        Map parameter = new HashMap();
        JasperPrint print = JasperFillManager.fillReport(path,parameter, koneksi);
        JasperViewer.viewReport(print, false);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(rootPane,"Dokumen Tidak Ada"+ex);
      }  
    }//GEN-LAST:event_jLabel12MouseClicked

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
       try {
        String path="src/pos/transaksi.jasper";
        Map parameter = new HashMap();
        JasperPrint print = JasperFillManager.fillReport(path,parameter, koneksi);
        JasperViewer.viewReport(print, false);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(rootPane,"Dokumen Tidak Ada"+ex);
      }  
    }//GEN-LAST:event_jLabel7MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Transaksi().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jbayar;
    private javax.swing.JTextField jcari;
    private javax.swing.JTextField jdis;
    private javax.swing.JLabel jjam;
    private javax.swing.JTextField jkembalian;
    private javax.swing.JTextField jkodetransaksi;
    private javax.swing.JTextField jqty;
    private javax.swing.JLabel jtgl;
    private javax.swing.JTextField jtotal;
    // End of variables declaration//GEN-END:variables
}
