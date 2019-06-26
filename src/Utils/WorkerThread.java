/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import SendURL.Controller;
import SendURL.VNPT;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import View.View;

public class WorkerThread implements Runnable {

    private String phonenumber;
    private int id = 0;
    private String ip = "";

    public WorkerThread() {
        super();
    }

    public WorkerThread(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public WorkerThread(String phonenumber, String ip) {
        this.phonenumber = phonenumber;
        this.ip = ip;
    }

    @Override
    public void run() {
        try {
            VNPT obj = new VNPT();
            obj.Create(phonenumber, ip);
            Controller.CountFinished++;
            Controller.TotalInfo--;
            System.out.println(ip);
            View.jframe.txt_rs.setText("" + Controller.CountFinished);
        } catch (Exception ex) {
            if (ex instanceof OverloadSystemException) {
                JOptionPane.showConfirmDialog(null, ((OverloadSystemException) ex).getMessage());
                System.out.println(((OverloadSystemException) ex).getMessage());
                System.exit(0);
            } else {
                JOptionPane.showConfirmDialog(null, ex.getMessage());
                System.exit(0);
                System.out.println(ex.getMessage());
            }

        }
    }

}
