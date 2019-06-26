/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class AccountService {

    public static List<AccountInfo> getAccountInfofromTxt(String path) {
        List<AccountInfo> listInfo = new ArrayList<>();
        List<String> lists = null;
        try {
            lists = Doc_file_kieu_txt.readFile(path);
            if (lists != null) {
                for (int i = 0; i < lists.size(); i++) {
                    if (!lists.get(i).equals("") && !lists.get(i).equals(" ")) {
                        AccountInfo info = new AccountInfo();
                        info.setPhoneNumber(lists.get(i).trim().replaceAll("\\s{2,}", ""));
                        listInfo.add(info);
                    }
                }
            }
            return listInfo;
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(null, e.getMessage());
            System.out.println("loi doc file");
        }
        return null;
    }
}
