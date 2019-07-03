/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alex
 */
public class Utils {

    // lay ra 3 sdt trong historycall
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
        if (rs.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                rs.add("0");
            }
        }
        return rs;
    }

    // viet vao file
    public synchronized static void writeFile(String content, String name) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        PrintWriter out = null;
        try {
            OutputStream os = new FileOutputStream(System.getProperty("user.dir") + "\\" + name,true);
            os.write(239);
            os.write(187);
            os.write(191);

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(os, "UTF-8")));
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
