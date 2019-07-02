/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import SendURL.Controller;
import SendURL.VNPT;
import javax.swing.JOptionPane;
import View.View;
import java.net.SocketTimeoutException;

public class WorkerThread implements Runnable {

    private String phonenumber;
    private int id = 0;
    private String ip = "";
    private String user;
    private String pass;

    public WorkerThread() {
        super();
    }

    public WorkerThread(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public WorkerThread(String phonenumber, String ip, String user, String pass) {
        this.phonenumber = phonenumber;
        this.ip = ip;
        this.user = user;
        this.pass = pass;
    }

    @Override
    public void run() {
        try {
            // dung object vnpt de chay ham create
            VNPT obj = new VNPT();
            obj.Create(phonenumber, ip,user,pass);
            //moi lan thanh cong tang bien dem len 1
            synchronized (Controller.lock) {
                Controller.CountFinished++;
                //cap nhat thong tin
                View.jframe.txt_rs.setText("" + Controller.CountFinished);
                View.jframe.lb_rs.setText("sdt đã xử lý: " + phonenumber);
            }

        } catch (Exception ex) {
            if (ex instanceof OverloadSystemException) {
                JOptionPane.showConfirmDialog(null, ((OverloadSystemException) ex).getMessage());
//                System.out.println(((OverloadSystemException) ex).getMessage());
                System.exit(0);
            } else if (ex instanceof SocketTimeoutException) {
                View.jframe.lb_rs.setText("time out kết nối ...");
                System.out.println("time out");
            } else {
                View.jframe.lb_rs.setText(ex.getMessage());
                //JOptionPane.showConfirmDialog(null, ex.getMessage());
                System.out.println("loi"+ex.getMessage());
                // System.exit(0);
            }

        }
    }

}
