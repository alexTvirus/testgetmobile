/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SendURL;

import Utils.OverloadSystemException;
import Utils.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Alex
 */
public class VNPT {

    private String UrlImg = "";
    private String token = "";
    private final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private String ip = "";
    public static CookieManager msCookieManager = new CookieManager();
    public static String filename = "";

    static {
        CookieHandler.setDefault(msCookieManager); 
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        filename = sdf.format(timestamp) + "result.txt";
    }

    public static boolean isFirst = true;

    public VNPT() {

    }

    public void Create(String phonenumber, String ip) throws Exception {
        this.ip = ip;
        if (isFirst) {
            getCookies("http://" + ip + "/b9_cskh/login.xhtml", "GET");
            dangNhap("http://" + ip + "/b9_cskh/login.xhtml", "POST", token);
            isFirst = false;
        }

        getCookies("http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml", "GET");
        String response = sendPost(phonenumber, "", "");
        if (!response.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")) {
            saveInfomation(response, filename);
        } else {
            throw new OverloadSystemException("Hệ thống đang quá tải vui lòng chờ trong giây lát");
        }
    }

    private void getCookies(String url, String Method) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = null;
        String pro = obj.getProtocol();
        if (pro.equals("http")) {
            con = (HttpURLConnection) obj.openConnection();
        } else {
            con = (HttpsURLConnection) obj.openConnection();
        }

        con.setRequestMethod(Method);

        //add request header
        String StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Host", ip);
        con.setRequestProperty("Cookie", StringCookies);

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        Map<String, List<String>> headerFields = con.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");

        if (cookiesHeader != null) {
            for (String cookie : cookiesHeader) {
                if (!cookie.isEmpty()) {
                    msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                }
            }
        }

        Pattern pattern = Pattern.compile(
                "<input\\s{1,3}type=\"hidden\"\\s{1,3}name=\"javax.faces.ViewState\"\\s{1,3}id=\"j_id1:javax.faces.ViewState:0\"\\s{1,3}value=\"(.*?)\"(.*?)>");
        Matcher matcher = pattern.matcher(response.toString());
        if (matcher.find()) {
            token = matcher.group(1);
        }
    }

    private void dangNhap(String url, String Method, String token) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = null;
        String pro = obj.getProtocol();
        if (pro.equals("http")) {
            con = (HttpURLConnection) obj.openConnection();
        } else {
            con = (HttpsURLConnection) obj.openConnection();
        }

        // optional default is GET
        String urlParameters
                = "javax.faces.partial.ajax=true"
                + "&javax.faces.source=frm_login%3Aj_idt20"
                + "&javax.faces.partial.execute=%40all"
                + "&javax.faces.partial.render=frm_login"
                + "&frm_login%3Aj_idt20=frm_login%3Aj_idt20"
                + "&frm_login=frm_login"
                + "&javax.faces.ViewState=" + token
                + "&frm_login%3Ausername=c1_thphuong_01_dl"
                + "&frm_login%3Apassword=hanoi123";

        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("origin", "http://" + ip);
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setRequestProperty("Faces-Request", "partial/ajax");
        con.setRequestProperty("Referer", "http://" + ip + "/b9_cskh/login.xhtml");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        con.setUseCaches(false);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }

    private String sendPost(String IsdnId, String shopIdFull, String shopId) throws MalformedURLException, IOException {

        String url = "http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        String urlParameters
                = "javax.faces.partial.ajax=true"
                + "&javax.faces.source=formMain%3Aj_idt145"
                + "&javax.faces.partial.execute=%40all"
                + "&javax.faces.partial.render=formMain%3ApnlStatusMsg+formMain%3AfocusOnId"
                + "&formMain%3Aj_idt145=formMain%3Aj_idt145"
                + "&formMain=formMain&javax.faces.ViewState=" + token
                + "&formMain%3AfocusOnId="
                + "&formMain%3AinpIsdnId=" + IsdnId
                + "&formMain%3AinpImsiId="
                + "&formMain%3AinpSerialId="
                + "&formMain%3AshopId_input=" + shopIdFull
                + "&formMain%3AshopId_hinput=" + shopId
                + "&formMain%3Aj_idt135=1"
                + "&formMain%3Aj_idt154=";

        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;

        String StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Cookie", StringCookies);
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Cookie", StringCookies);
        con.setRequestProperty("origin", "http://" + ip);
        con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        con.setRequestProperty("Faces-Request", "partial/ajax");
        con.setRequestProperty("Referer", "http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml?jftfdi=&jffi=%2Fpresentation%2Fcm%2Futility%2FTIENICH-5.1.1-TrangThaiTongDai.xhtml");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

        con.setUseCaches(false);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(postData);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        return response.toString();
    }

    private void saveInfomation(String content, String filename) throws UnsupportedEncodingException, IOException, OverloadSystemException {
//        Pattern pattern = Pattern.compile(
//                "Msisdn((.|\\n|\\r)+)<\\/textarea>");
//        Matcher matcher = pattern.matcher(content);
        if (!content.contains("ACCOUNT NOT FOUND")) {
            String[] temp = content.split("Msisdn", 2);
            String infomation = "";
            if (temp.length >= 2) {
                temp = temp[1].split("</textarea>", 2);
                infomation = StringEscapeUtils.unescapeHtml4("Msisdn" + temp[0]);
            } else {
                throw new OverloadSystemException("Hệ thống đang quá tải vui lòng chờ trong giây lát");
            }

//        if (matcher.find()) {
//            infomation = content.substring(matcher.start(), matcher.end()).replaceAll("<\\/textarea>", "");
//        }
            StringBuffer sb = new StringBuffer(infomation);

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
            if (sb.indexOf("Call Type") != -1) {
                sb.insert(sb.indexOf("Call Type"), ",");
            }
            if (sb.indexOf("Amount") != -1) {
                sb.insert(sb.indexOf("Amount") + 6, ",");
            }
            writeFile(sb.toString(), filename);
        } else {
            String[] temp = content.split("msisdn=", 2);
            if (temp.length >= 2) {
                temp[1] = temp[1].substring(0, 8);
            }
            writeFile(temp[1] + "-ACCOUNT NOT FOUND", filename);
        }
    }

    public void writeFile(String content, String name) throws FileNotFoundException, UnsupportedEncodingException, IOException {
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

}
