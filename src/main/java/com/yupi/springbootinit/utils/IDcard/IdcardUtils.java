package com.yupi.springbootinit.utils.IDcard;


import cn.hutool.json.JSONObject;
import com.squareup.okhttp.*;
import com.yupi.springbootinit.model.IDcard;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;

/**
* 身份证识别
*/
public class IdcardUtils {

    public static final String API_KEY = "Oa9q74IwHGa678EOrSlulGpP";
    public static final String SECRET_KEY = "xH2Fuuv38N82EjbRSepnIvLqdxu5z03G";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    public static IDcard idcard(MultipartFile file) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
        try {
            // 本地文件路径
            byte[] imgData =file.getBytes();
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "id_card_side=" + "front" + "&image=" + imgParam   +"&detect_photo=true";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAccessToken();
            String result = HttpUtil.post(url, accessToken, param);

            JSONObject jsonObject = new cn.hutool.json.JSONObject(result);
            JSONObject words = jsonObject.getJSONObject("words_result");

            String name = words.getJSONObject("姓名").get("words").toString();
            String number = words.getJSONObject("公民身份号码").get("words").toString();
            String address = words.getJSONObject("住址").get("words").toString();
            String birth = words.getJSONObject("出生").get("words").toString();
            String gender = words.getJSONObject("性别").get("words").toString();
            String ethnic = words.getJSONObject("民族").get("words").toString();
            String photo = jsonObject.get("photo").toString();

            IDcard iDcard = new IDcard();
            iDcard.setName(name);
            iDcard.setNumber(number);
            iDcard.setAddress(address);
            iDcard.setBirth(birth);
            iDcard.setGender(gender);
            iDcard.setEthnic(ethnic);
            iDcard.setPhoto(photo);

            return iDcard;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new org.json.JSONObject(response.body().string()).getString("access_token");
    }

}
