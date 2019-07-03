/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SendURL;

import Utils.OverloadSystemException;
import Utils.Utils;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Alex
 */
public class VNPT {

    public static boolean isFirst = true;
    private final String USER_AGENT
            = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";
    private String ip = "";
    private String phone = "";
    public static CookieManager msCookieManager = new CookieManager();
    public static String filename = "";
    public int counter = 1;

    static {
        CookieHandler.setDefault(msCookieManager);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        filename = sdf.format(timestamp) + "result.csv";
        try {
            writeHeaderCsv(filename);
        } catch (IOException ex) {
            Logger.getLogger(VNPT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public VNPT() {

    }

    public void Create(String phonenumber, String ip, String user, String pass) throws Exception {
        this.ip = ip;
        this.phone = phonenumber;
        synchronized (Controller.lock) {
            if (isFirst) {
                String check1 = dangNhap("http://" + ip + "/1090/login.jsp", "POST", user, pass);
                counter = 0;
                while (check1.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")) {
                    check1 = dangNhap("http://" + ip + "/1090/login.jsp", "POST", user, pass);
                    counter++;
                    Controller.sendMessage("đang kết nối ..." + counter);
                    if (counter == 300) {
                        Utils.writeFile("timeout: " + phonenumber, "error.txt");
                        throw new SocketTimeoutException();
                    }
                }

                isFirst = false;
            }
        }
        String response = sendPost(phonenumber);
        counter = 0;
        while (true) {
            if (!response.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")
                    && !response.contains("<form name=\"frmLogin\"")
                    && !response.contains("<![CDATA[<form id=\"frm_login\" name=\"frm_login\"")
                    && !response.contains("Vui lòng nhấn F5 để load lại")) {
                saveInfomation(getStringInforCsv(response).toString(), phonenumber, filename);
                break;
            }
            synchronized (Controller.lock) {
                while (response.contains("Hệ thống đang quá tải vui lòng chờ trong giây lát")
                        || response.contains("<![CDATA[<form id=\"frm_login\" name=\"frm_login\"")
                        || response.contains("<form name=\"frmLogin\"")
                        || response.contains("Vui lòng nhấn F5 để load lại")) {
                    response = dangNhap("http://" + ip + "/1090/login.jsp", "POST", user, pass);
                    counter++;
                    Controller.sendMessage("đang kết nối ..." + counter);
                    if (counter == 300) {
                        Utils.writeFile("timeout: " + phonenumber, "error.txt");
                        throw new SocketTimeoutException();
                    }
                }
            }
            response = sendPost(phonenumber);
        }
    }

    private synchronized String getCookies(String url, String Method) throws Exception {
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

            return response.toString();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                Utils.writeFile("timeout: " + phone, "error.txt");
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private String dangNhap(String url, String Method, String user, String pass) throws Exception {
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
                    = "txtUserName=" + user
                    + "&txtPassword=" + pass
                    + "&DONE=%C4%90%E1%BB%93ng+%C3%BD"
                    + "&action=check";

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Host", ip);
            con.setRequestProperty("Origin", "http://" + ip);
            con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            con.setRequestProperty("Upgrade-Insecure-Requests", "1");
            con.setRequestProperty("Referer", "http://" + ip + "/1090/login.jsp");
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
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
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

    private String sendPost(String sdt) throws MalformedURLException, IOException {
        try {
            String url = "http://" + ip + "/1090/mobicard.jsp";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(2000);
            String urlParameters
                    = "txtHLR_ISDN=" + sdt
                    + "&txtSub_ID="
                    + "&txtIMSI=&txtID_No="
                    + "&txtBusNo=&txtTIN="
                    + "&txtCorCode="
                    + "&chkActive="
                    + "&chkCheck=ACT"
                    + "&frmAction=SearchSub";

            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String StringCookies = "";
            StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");

            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Cookie", StringCookies);
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            con.setRequestProperty("origin", "http://" + ip);
            con.setRequestProperty("Host", ip);
            con.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            con.setRequestProperty("Upgrade-Insecure-Requests", "1");
            con.setRequestProperty("Referer", "http://" + ip + "/1090/mobicard.jsp");
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
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
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

//lay hlr
    public String getHlr(String mathuebao, String sdt, String imsi) throws Exception {
        try {
            String urlParameters
                    = "http://10.50.8.210/1090/view_hlr.jsp?"
                    + "p_Type=HLR"
                    + "&p_MobType=0"
                    + "&p_SubID=" + mathuebao
                    + "&p_ISDN=" + sdt
                    + "&p_IMSI=" + imsi
                    + "&pIn_Status=1"
                    + "&pHlr_Status=1"
                    + "&pAct_Status=02"
                    + "&pCenter=1";
            URL obj = new URL(urlParameters);
            HttpURLConnection con = null;
            String pro = obj.getProtocol();
            if (pro.equals("http")) {
                con = (HttpURLConnection) obj.openConnection();
            } else {
                con = (HttpsURLConnection) obj.openConnection();
            }

            con.setRequestMethod("GET");
            con.setConnectTimeout(2000);
            //add request header
            String StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
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

            return response.toString();
        } catch (Exception e) {
            if (e instanceof SocketTimeoutException) {
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }
// IMSI 

    public String getIMSI(String mathuebao, String imsi) throws Exception {
        try {
            String url = "http://" + ip + "/1090/viewIMSI_mobicard.jsp?p_Type=0&p_QueryType=1";
            String urlParameters
                    = "p_Sub_ID=" + mathuebao
                    + "&p_IMSI=" + imsi
                    + "&pCenter=1";
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;

            URL obj = new URL(url);
            HttpURLConnection con = null;
            String pro = obj.getProtocol();
            if (pro.equals("http")) {
                con = (HttpURLConnection) obj.openConnection();
            } else {
                con = (HttpsURLConnection) obj.openConnection();
            }

            con.setRequestMethod("POST");
            con.setConnectTimeout(2000);
            //add request header
            String StringCookies = StringUtils.join(msCookieManager.getCookieStore().getCookies(), ";");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
            con.setRequestProperty("Host", ip);
            con.setRequestProperty("Referer", "http://" + ip + "/1090/mobicard.jsp");
            con.setRequestProperty("Upgrade-Insecure-Requests", "1");
            con.setRequestProperty("Cookie", StringCookies);
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
                throw new SocketTimeoutException();
            } else {
                throw e;
            }
        }
    }

    private static void writeHeaderCsv(String filename) throws UnsupportedEncodingException, IOException {
        Utils.writeFile("sdt,LocationTime1,DoiTuong,TenKhachHang,DiaChiKhachHang,CardType,NgayDauHLRI,CH-DL-Xuat,TinhTrang,LocationTime2,MSCNUM,IMSI,LoaiKhachHang,LoaiThueBao,CMND,NgayCap,NoiCap,NgaySinh,GoiCuoc", filename);
    }

    private synchronized void saveInfomation(String content, String phone, String filename) throws UnsupportedEncodingException, IOException, OverloadSystemException, Exception {
        Utils.writeFile(content, filename);
    }
// xy ly' chuoi content

    public StringBuffer getStringInforCsv(String content) throws OverloadSystemException, Exception {
        StringBuffer sb = new StringBuffer();
        Pattern pattern = null;
        Matcher matcher = null;
        Document doc = null;
        Elements el = null;
        String[] temp;
        //-----------tim ma thue bao
        doc = Jsoup.parse(content);
        el = doc.select("input[type=\"hidden\"][name=\"p_Sub_ID\"]");
        String mathuebao = el.attr("value");
//        el = doc.select("tr > td[align=\"CENTER\"][nowrap=\"\"] > font[face=\"Tahoma\"][color=\"Blue\"]");

        //---------lay toan bo ket qua
        el = doc.select("tr > td[bgcolor=\"#F7F7F7\"] > font[face=\"Tahoma\"]");
        //------tim imsi
        String imsi = el.get(1).text();
        pattern = Pattern.compile("\\b\\d{15,}\\b");
        matcher = pattern.matcher(imsi);

        if (matcher.find()) {
            imsi = matcher.group();
        } else {
            imsi = "";
        }
        //--------tim hlr
        String responese = getHlr(mathuebao, el.get(0).text(), imsi);
        //-------------location
        pattern = Pattern.compile("LOCATIONTIME = \\b(\\d{4}[\\/\\-.]\\d{1,2}[\\/\\-.]\\d{1,2}\\s{1}(\\d{1,2}[\\/\\-:]\\d{1,2}[\\/\\-:]\\d{1,2}|\\d{1,2}[\\/\\-:]\\d{1,2}[\\/\\-:]\\d{1,2}(.*?)))\\b");
        matcher = pattern.matcher(responese);
        int count = 0;
        String[] location = new String[2];
        while (matcher.find()) {
            if (matcher.group(3) == null) {
                location[count] = matcher.group(1);
            } else {
                temp = matcher.group(1).split(matcher.group(3), 2);
                location[count] = temp[0];
            }
            count++;
        }

        //--------MSCNUM
        pattern = Pattern.compile("MSCNUM = \\b(\\d{3,11}|\\d{3,11}(.*?))\\b");
        matcher = pattern.matcher(responese);
        String MSCNUM = "";
        count = 0;
        while (matcher.find()) {
            if (matcher.group(2) == null) {
                MSCNUM = matcher.group(1);
            } else {
                temp = matcher.group(1).split(matcher.group(2), 2);
                MSCNUM = temp[0];
            }
            count++;
        }

        //--------cardtype
        pattern = Pattern.compile("CardType = \\b(\\w{3,4}|\\w{3,4}(.*?))\\b");
        matcher = pattern.matcher(responese);
        String cardtype = "";
        count = 0;
        while (matcher.find()) {
            if (matcher.group(2) == null) {
                cardtype = matcher.group(1);
            } else {
                temp = matcher.group(1).split(matcher.group(2), 2);
                cardtype = temp[0];
            }
            count++;
        }

        //--------tim HLRI
        responese = getIMSI(mathuebao, imsi);
        doc = Jsoup.parse(responese);
        Elements el1 = doc.select("tr > td[bgcolor=\"#F7F7F7\"] >font");

        //---------thong tin bang chinh
        //sdt
        sb.insert(0, ((el.get(0).text().equals("") ? "" : el.get(0).text().trim().replaceAll("\\,", " ")) + ","));
        //location1
        sb.append((location.length > 0 ? location[0] : "") + ",");
        //đối tượng
        sb.append((el.get(22).text().equals("") ? "" : el.get(22).text().trim().replaceAll("\\,", " ")) + ",");
        //Tên khách hàng
        sb.append((el.get(9).text().equals("") ? "" : el.get(9).text().trim().replaceAll("\\,", " ")) + ",");
        //Địa chỉ khách hàng
        sb.append((el.get(12).text().equals("") ? "" : el.get(12).text().trim().replaceAll("\\,", " ")) + ",");
        //cardtype
        sb.append(cardtype + ",");
        //Ngày đấu HLRI -----
        sb.append((el1.get(6).text().equals("") ? "" : el1.get(6).text().trim().replaceAll("\\,", " ")) + ",");
        //CH - ĐL xuất -----
        sb.append((el1.get(15).text().equals("") ? "" : el1.get(15).text().trim().replaceAll("\\,", " ")) + ",");
        //tinh trang
        sb.append((el.get(2).text().equals("") ? "" : el.get(2).text().trim().replaceAll("\\,", " ")) + ",");
        //location1
        sb.append((location.length > 1 ? location[1] : "") + ",");
        //MSCNUM
        sb.append((MSCNUM.trim()) + ",");
        //imde
        sb.append((el.get(1).text().equals("") ? "" : el.get(1).text().trim().replaceAll("\\,", " ")) + ",");
        //Loại khách hàng
        sb.append((el.get(4).text().equals("") ? "" : el.get(4).text().trim().replaceAll("\\,", " ")) + ",");
        //Loại thuê bao
        sb.append((el.get(5).text().equals("") ? "" : el.get(5).text().trim().replaceAll("\\,", " ")) + ",");
        //Số CMND
        sb.append((el.get(14).text().equals("") ? "" : el.get(14).text().trim().replaceAll("\\,", " ")) + ",");
        //ngay cap
        sb.append((el.get(15).text().equals("") ? "" : el.get(15).text().trim().replaceAll("\\,", " ")) + ",");
        //noi cap
        sb.append((el.get(16).text().equals("") ? "" : el.get(16).text().trim().replaceAll("\\,", " ")) + ",");
        //ngay sinh
        sb.append((el.get(25).text().equals("") ? "" : el.get(25).text().trim().replaceAll("\\,", " ")) + ",");
        //goi cước
        sb.append((el.get(39).text().equals("") ? "" : el.get(39).text().trim().replaceAll("\\,", " ")) + ",");
        return sb;
    }
}
