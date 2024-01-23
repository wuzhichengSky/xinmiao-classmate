package com.yupi.springbootinit.utils.IDcard;

import com.squareup.okhttp.*;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import okhttp3.*;
import org.json.JSONObject;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.net.URLEncoder;

class Sample {
    public static final String API_KEY = "Oa9q74****lulGpP";
    public static final String SECRET_KEY = "xH2Fuu****u5z03G";

    //static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String []args) throws IOException{
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        // image 可以通过 getFileContentAsBase64("C:\fakepath\背面.jpg") 方法获取,如果Content-Type是application/x-www-form-urlencoded时,第二个参数传true
        RequestBody body = RequestBody.create(mediaType, "id_card_side=front&image=%2F9j%2F4UYMRXhpZgAATU0AKgAAAAgABgESAAMAAAABAAYAAAEaAAUAAAABAAAAVgEbAAUAAAABAAAAXgEoAAMAAAABAAIAAAIT...&detect_risk=true&detect_quality=false&detect_photo=true&detect_card=false&detect_direction=false");
        Request request = new Request.Builder()
            .url("https://aip.baidubce.com/rest/2.0/ocr/v1/idcard?access_token=" + getAccessToken())
            .method("POST", body)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "application/json")
            .build();
        //Response response = HTTP_CLIENT.newCall(request).execute();
        //System.out.println(response.body().string());

    }
    
    /**
     * 获取文件base64编码
     *
     * @param path      文件路径
     * @param urlEncode 如果Content-Type是application/x-www-form-urlencoded时,传true
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64(String path, boolean urlEncode) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));
        String base64 = Base64.getEncoder().encodeToString(b);
        if (urlEncode) {
            base64 = URLEncoder.encode(base64, "utf-8");
        }
        return base64;
    }
    
    
    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        //Response response = HTTP_CLIENT.newCall(request).execute();
        //return new JSONObject(response.body().string()).getString("access_token");
        return "";
    }
    
}