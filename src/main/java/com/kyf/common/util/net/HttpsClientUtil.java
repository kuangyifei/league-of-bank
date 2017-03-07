package com.kyf.common.util.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by kuangyifei on 2017/3/7.
 */
public class HttpsClientUtil {
    
    /**
     * get 方法
     * @param url 
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getAsString(String url) throws MalformedURLException, IOException {
        return getAsString(url, false);
    }
    
    /**
     * get 方法
     * @param url 
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getAsString(String url, boolean isTrustAnyHost) throws MalformedURLException, IOException {
        URL cUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) cUrl.openConnection();
        conn.setRequestMethod("GET");
        setCommonRequestProperty(conn);
        if (isTrustAnyHost) {
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            result.append(line + "\n");
        }
        is.close();
        br.close();
        conn.disconnect();
        return result.toString();
    }
    
    /**
     * post 方法
     * @param url
     * @param content
     * @param contentType
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String postAsString(
            String url, 
            String content, 
            String contentType ) 
                    throws MalformedURLException, IOException {
        return postAsString(url, content, contentType, false);
    }
    
    /**
     * post 方法
     * @param url
     * @param content
     * @param contentType
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String postAsString(
            String url, 
            String content, 
            String contentType,
            boolean isTrustAnyHost) 
                    throws MalformedURLException, IOException {
        URL cUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) cUrl.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", contentType);
        setCommonRequestProperty(conn);
        conn.setDoOutput(true);
        if (isTrustAnyHost) {
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        }       
        OutputStream os = conn.getOutputStream();
        os.write(content.getBytes("utf-8"));        
        os.close();
        
        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = br.readLine()) != null) {
            result.append(line + "\n");
        }
        is.close();
        br.close();
        conn.disconnect();
        return result.toString();
    }
    
    public static String postAsString(
            String url, 
            String content) throws MalformedURLException, IOException {
        return postAsString(url, content, ContentType.FORM_URL_ENCODED.getType());
    }
    
    private static void setCommonRequestProperty(HttpURLConnection conn) {
        conn.setRequestProperty("Accept-Charset", "utf-8");
    }
    
    public static enum ContentType {
        FORM_URL_ENCODED("application/x-www-form-urlencoded"),
        TEXT("application/text")
        ;
        
        private String type;
        private ContentType(String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }
    }
    
    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
    
    public static void main(String[] args) throws Exception {
        String result = getAsString("https://www.baidu.com");
        System.out.println(result);
    }
}
