/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SendURL;

import Utils.OverloadSystemException;
import Utils.Utils;
import View.View;
import java.io.BufferedReader;
import java.io.DataOutputStream;

import java.io.IOException;

import java.io.InputStreamReader;

import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import java.text.SimpleDateFormat;

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
    private static String token = "";
    private final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private String ip = "";
    private String phone = "";
    public static CookieManager msCookieManager = new CookieManager();
    public static String filename = "";
    public static String result = "";
    public int counter = 1;

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
        this.phone = phonenumber;
        if (isFirst) {
            preDangNhap("http://" + ip + "/b9_cskh/login.xhtml", "GET");
            String x2 = dangNhap("http://" + ip + "/b9_cskh/login.xhtml", "POST", token);
            counter = 0;
            while (x2.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")) {
                preDangNhap("http://" + ip + "/b9_cskh/login.xhtml", "GET");
                x2 = dangNhap("http://" + ip + "/b9_cskh/login.xhtml", "POST", token);
                counter++;
                View.jframe.lb_rs.setText("đang kết nối ..." + counter);
                if (counter == 100) {
                    break;
                }
            }

            String x3 = getCookies("http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml", "GET");
            counter = 0;

            while (x3.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")) {
                Utils.writeFile("2", "error.txt");
                preDangNhap("http://" + ip + "/b9_cskh/login.xhtml", "GET");
                x3 = dangNhap("http://" + ip + "/b9_cskh/login.xhtml", "POST", token);
                counter++;
                View.jframe.lb_rs.setText("đang kết nối ..." + counter);
                if (counter == 100) {
                    break;
                }
            }
            if (!x3.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")) {
                getCookies("http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml", "GET");
            }
            isFirst = false;
        }
//        PresendPost(phonenumber);
        String response = sendPost(phonenumber, "", "");
        counter = 0;
        while (true) {
            if (!response.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")
                    && !response.contains("<form id=\"frm_login\" name=\"frm_login\"")
                    && !response.contains("<![CDATA[<form id=\"frm_login\" name=\"frm_login\"")
                    && !response.contains("Vui lòng nhấn F5 để load lại")) {
                saveInfomation(response, filename);
                break;
            }

            while (response.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")
                    || response.contains("<![CDATA[<form id=\"frm_login\" name=\"frm_login\"")
                    || response.contains("<form id=\"frm_login\" name=\"frm_login\"")
                    || response.contains("Vui lòng nhấn F5 để load lại")) {
                preDangNhap("http://" + ip + "/b9_cskh/login.xhtml", "GET");
                response = dangNhap("http://" + ip + "/b9_cskh/login.xhtml", "POST", token);
                counter++;
                View.jframe.lb_rs.setText("đang kết nối ..." + counter);
                if (counter == 300) {
                    throw new OverloadSystemException("Hệ thống đang quá tải vui lòng chờ trong giây lát--");
                }
            }
            response = sendPost(phonenumber, "", "");
        }
    }

    private String getCookies(String url, String Method) throws Exception {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = null;
            String pro = obj.getProtocol();
            if (pro.equals("http")) {
                con = (HttpURLConnection) obj.openConnection();
            } else {
                con = (HttpsURLConnection) obj.openConnection();
            }

            con.setRequestMethod(Method);
            con.setConnectTimeout(2000);
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
            return response.toString();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Utils.writeFile("sdt loi timeout: " + phone, "error.txt");
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private String preDangNhap(String url, String Method) throws Exception {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = null;
            String pro = obj.getProtocol();
            if (pro.equals("http")) {
                con = (HttpURLConnection) obj.openConnection();
            } else {
                con = (HttpsURLConnection) obj.openConnection();
            }

            con.setRequestMethod(Method);
            con.setConnectTimeout(2000);
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

            Pattern pattern = Pattern.compile(
                    "<input\\s{1,3}type=\"hidden\"\\s{1,3}name=\"javax.faces.ViewState\"\\s{1,3}id=\"j_id1:javax.faces.ViewState:0\"\\s{1,3}value=\"(.*?)\"(.*?)>");
            Matcher matcher = pattern.matcher(response.toString());
            if (matcher.find()) {
                token = matcher.group(1);
            }
            return response.toString();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Utils.writeFile("sdt loi timeout: " + phone, "error.txt");
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private String dangNhap(String url, String Method, String token) throws Exception {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = null;
            String pro = obj.getProtocol();
            if (pro.equals("http")) {
                con = (HttpURLConnection) obj.openConnection();
            } else {
                con = (HttpsURLConnection) obj.openConnection();
            }
            con.setConnectTimeout(2000);
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
                    + "&frm_login%3Apassword=hanoi123"
                    + "&frm_login%3Aj_idt18_input=on";

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

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
            return response.toString();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Utils.writeFile("sdt loi timeout: " + phone, "error.txt");
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private String sendPost(String IsdnId, String shopIdFull, String shopId) throws MalformedURLException, IOException {
        try {
            String url = "http://" + ip + "/b9_cskh/presentation/cm/utility/TIENICH-5.1.1-TrangThaiTongDai.xhtml";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(2000);
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
//        con.setRequestProperty("Accept", "application/xml, text/xml, */*; q=0.01");
//        con.setRequestProperty("Accept-Language", "vi,en-GB;q=0.8,en;q=0.6");
//        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            con.setRequestProperty("origin", "http://" + ip);
            con.setRequestProperty("Host", ip);
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
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Utils.writeFile("sdt loi timeout: " + phone, "error.txt");
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private void saveInfomation(String content, String filename) throws UnsupportedEncodingException, IOException, OverloadSystemException {
        Utils.writeFile(Utils.getStringInforCsv(content, phone).toString(), filename);
    }

}
