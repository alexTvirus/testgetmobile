/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import SendURL.Controller;
import Utils.Doc_file_kieu_txt;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Alex
 */
public class View extends javax.swing.JFrame {

    /**
     * Creates new form Views
     */
    public View() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_start = new javax.swing.JButton();
        txt_thread = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txt_rs = new javax.swing.JTextField();
        txt_total = new javax.swing.JTextField();
        btn_file = new javax.swing.JButton();
        lb_rs = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btn_start.setText("Start");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        txt_thread.setText("5");

        jLabel1.setText("Thread");

        btn_file.setText("Chọn file sdt");
        btn_file.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_fileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_rs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btn_start)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btn_file))
                            .addComponent(txt_rs, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_thread, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(22, 22, 22))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txt_total)
                                .addContainerGap())))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_start)
                    .addComponent(txt_thread, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(btn_file))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_rs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lb_rs, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_startActionPerformed
        // TODO add your handling code here:
        if (PathFilePhone.isEmpty()) {
            int clickrs = JOptionPane.showConfirmDialog(null, "Hãy chọn đường dẫn đến file chứa sdt");
            if (clickrs == JOptionPane.YES_OPTION) {
                // Saving code here
            }
            if (clickrs == JOptionPane.NO_OPTION) {
                // Saving code here
            }
        } else {
            if (isFirst) {
                Thread jobThread = new Thread() {
                    @Override
                    public void run() {
                        Controller app = new Controller();
                        app.Start();

                    }
                };
                jobThread.start();
                isFirst = false;

            }
        }

    }//GEN-LAST:event_btn_startActionPerformed

    private void btn_fileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_fileActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        int returnVal = fc.showOpenDialog(View.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            PathFilePhone = file.getPath();
            txt_total.setText(""+Doc_file_kieu_txt.readFile(PathFilePhone).size()); 
        } else {
            int clickrs = JOptionPane.showConfirmDialog(null, "Hãy chọn đường dẫn đến file chứa sdt");
            if (clickrs == JOptionPane.YES_OPTION) {
                // Saving code here
            }
            if (clickrs == JOptionPane.NO_OPTION) {
                // Saving code here
            }
        }
    }//GEN-LAST:event_btn_fileActionPerformed

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
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(View.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jframe = new View();
                jframe.setVisible(true);

                jframe.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                jframe.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {

                        int clickrs = JOptionPane.showConfirmDialog(null, "Are you sure you want to close this window?");
                        if (clickrs == JOptionPane.YES_OPTION) {
                            // Saving code here
                            System.exit(0);
                        }
                    }
                });
            }
        });
    }
    private boolean isFirst = true;
    public static String PathFilePhone="";
    public static View jframe;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_file;
    private javax.swing.JButton btn_start;
    private javax.swing.JLabel jLabel1;
    public javax.swing.JLabel lb_rs;
    public javax.swing.JTextField txt_rs;
    public javax.swing.JTextField txt_thread;
    public javax.swing.JTextField txt_total;
    // End of variables declaration//GEN-END:variables
}
