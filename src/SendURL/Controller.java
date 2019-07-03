/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SendURL;

import Utils.AccountInfo;
import Utils.AccountService;
import Utils.Doc_file_kieu_txt;
import Utils.WorkerThread;
import java.util.List;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import View.View;
import Utils.Utils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    public static int CountFinished = 0;
    public static Object lock = new Object();
    public static int NUM_OF_THREAD = 3;

    public void Start() {
        List<AccountInfo> lists = AccountService.getAccountInfofromTxt(View.PathFilePhone);
        List<String> listIp = Doc_file_kieu_txt.readFile(System.getProperty("user.dir") + "\\ip.txt");
        List<AccountInfo> listuserpass = AccountService.getAccountInfofromTxt(System.getProperty("user.dir") + "\\UserPass.txt");
        View.jframe.txt_total.setText("" + lists.size());
        if (Integer.parseInt(View.jframe.txt_thread.getText()) < 100) {
            NUM_OF_THREAD = Integer.parseInt(View.jframe.txt_thread.getText());
        }
        try {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(NUM_OF_THREAD);
            for (int i = 0; i < lists.size(); i++) {
                Runnable worker = new WorkerThread(lists.get(i).getPhoneNumber(),
                        listIp.get(Utils.getRandomNumberInRange(0, listIp.size() - 1)),
                        listuserpass.get(0).getUser(), listuserpass.get(0).getPass()
                );
                View.jframe.lb_rs.setText("đang khởi tạo ..." + (i + 1));
                executor.schedule(worker, 300, TimeUnit.MILLISECONDS);
            }
            executor.shutdown();

            // Wait until all threads are finish
            while (!executor.isTerminated()) {
                // Running ...
            }

            JOptionPane.showConfirmDialog(null, "Đã hoàn thành!");
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e.getMessage());
        }
    }

    public static synchronized void sendMessage(String ms) {
        View.jframe.lb_rs.setText(ms);
    }
}
