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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import View.View;
import java.util.Random;
import Utils.Utils;

public class Controller {

    public static int CountFinished = 1;
    public static int TotalInfo = 0;

    public void Start() {
        List<AccountInfo> lists = AccountService.getAccountInfofromTxt(System.getProperty("user.dir") + "\\Account.txt");
        List<String> listIp = Doc_file_kieu_txt.readFile(System.getProperty("user.dir") + "\\ip.txt");
        TotalInfo = lists.size();
        View.jframe.txt_total.setText("" + lists.size());
        try {
            int counter = 0;
            while (true) {
                ExecutorService executor = Executors.newCachedThreadPool();
                for (int i = 0; i < Integer.parseInt(View.jframe.txt_thread.getText()); i++) {
                    if (counter > lists.size() - 1) {
                        break;
                    }
                    Runnable worker = new WorkerThread(lists.get(counter).getPhoneNumber(), listIp.get(Utils.getRandomNumberInRange(0, listIp.size() - 1)));
                    executor.execute(worker);
                    counter++;
                    //Thread.sleep(400);
                }
                executor.shutdown();

                // Wait until all threads are finish
                while (!executor.isTerminated()) {
                    // Running ...
                }
                if (counter > lists.size() - 1) {
                    break;
                }
            }
            JOptionPane.showConfirmDialog(null, "Đã hoàn thành!");
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e.getMessage());
        }
    }
}
