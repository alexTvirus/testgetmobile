/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author Alex
 */
public class Utils {

    public static StringBuffer getStringInforCsv(String content, String phone) throws OverloadSystemException {
        if (!content.contains("ACCOUNT NOT FOUND")) {
            String[] temp = content.split("Msisdn", 2);
            String infomation = "";
            if (temp.length >= 2) {
                temp = temp[1].split("</textarea>", 2);
                infomation = StringEscapeUtils.unescapeHtml4("Msisdn" + temp[0]);
            } else {
                throw new OverloadSystemException("Hệ thống đang quá tải vui lòng chờ trong giây lát--");
            }

//        if (matcher.find()) {
//            infomation = content.substring(matcher.start(), matcher.end()).replaceAll("<\\/textarea>", "");
//        }
            StringBuffer sb = new StringBuffer(infomation);
            List<String> rs = Utils.getHistoryDate(infomation);
            sb=new StringBuffer(sb.toString());
            
            if (sb.indexOf("Profile") != -1) {
                sb.insert(sb.indexOf("Profile"), ",");
            }
            if (sb.indexOf("Units Available") != -1) {
                sb.insert(sb.indexOf("Units Available"), ",");
            }
            if (sb.indexOf("Refill Error ICC") != -1) {
                sb.insert(sb.indexOf("Refill Error ICC"), ",");
            }
            if (sb.indexOf("First Call Date") != -1) {
                sb.insert(sb.indexOf("First Call Date"), ",");
            }
            if (sb.indexOf("Beg. Validation Date") != -1) {
                sb.insert(sb.indexOf("Beg. Validation Date"), ",");
            }
            if (sb.indexOf("Bonus Account") != -1) {
                sb.insert(sb.indexOf("Bonus Account"), ",");
            }
            if (sb.indexOf("Day before Deactive") != -1) {
                sb.insert(sb.indexOf("Day before Deactive"), ",");
            }
            if (sb.indexOf("Account Block") != -1) {
                sb.insert(sb.indexOf("Account Block"), ",");
            }
            if (sb.indexOf("Recharge & Bonus Units") != -1) {
                sb.insert(sb.indexOf("Recharge & Bonus Units"), ",");
            }
            if (sb.indexOf("Allow P2P") != -1) {
                sb.insert(sb.indexOf("Allow P2P"), ",");
            }
            if (sb.indexOf("City Location 2") != -1) {
                sb.insert(sb.indexOf("City Location 2"), ",");
            }
            if (sb.indexOf("City Location 4") != -1) {
                sb.insert(sb.indexOf("City Location 4"), ",");
            }
            if (sb.indexOf("CALL HISTORY") != -1) {
                sb.insert(sb.indexOf("CALL HISTORY"), ",");
            }
            
            
            
            
            for (String item : rs) {
                sb.insert(0, item + ",");
            }
            sb.insert(0, phone + ",");
            return sb;
        } else {
            return new StringBuffer(phone + "-ACCOUNT NOT FOUND");

        }
    }

    public static List<String> getHistoryDate(String Content) {
        List<String> rs = new ArrayList();
        String[] temp = Content.split("Amount", 2);
        Pattern pattern = Pattern.compile("\\b(0?[1-9]|[12]\\d|3[01])[\\/](0?[1-9]|[12]\\d)[\\/](\\d{2}|\\d{4})\\b");
        Matcher matcher = pattern.matcher(temp[1]);
        int counter = 1;
        while (matcher.find()) {
            rs.add(matcher.group());
            counter++;
            if (counter > 3) {
                break;
            }
        }
        return rs;
    }

    public static void writeFile(String content, String name) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\" + name, true)));
            out.println(content);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static int getRandomNumberInRange(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public String generateRandomString(int length) {
        Random random = new Random();
        char[] values = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'U', 'I', 'K', 'L', 'M', 'N', 'T', 'Z'};
        String out = "";

        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(values.length);
            out += values[idx];
        }
        return out;
    }

}
