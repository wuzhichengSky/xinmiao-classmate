package com.yupi.springbootinit.utils;


import cn.hutool.json.JSONObject;
import com.squareup.okhttp.*;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.IDcard;
import com.yupi.springbootinit.utils.baiduyun.Base64Util;
import com.yupi.springbootinit.utils.baiduyun.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Calendar;

/**
* 身份证识别
*/
@Slf4j
public class IdcardUtils {
                                        //donid8HqNGyF0yaGvpTsZb5e
    private static final String API_KEY = "Oa9q74IwHGa678EOrSlulGpP";
    private static final String SECRET_KEY = "xH2Fuuv38N82EjbRSepnIvLqdxu5z03G";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

    public static IDcard idcard(MultipartFile file) throws Exception {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";

            // 本地文件路径
            byte[] imgData =file.getBytes();
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "id_card_side=" + "front" + "&image=" + imgParam   +"&detect_photo=true"+"&detect_risk=true";
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = getAccessToken(API_KEY,SECRET_KEY);
            String result = HttpUtil.post(url, accessToken, param);

            log.info("身份证信息获取接口结果:"+result);

            JSONObject jsonObject = new cn.hutool.json.JSONObject(result);
            JSONObject words = jsonObject.getJSONObject("words_result");
            String riskType = jsonObject.get("risk_type").toString();

            //身份证不合法（翻拍、复印件、临时身份证等）
            if(!"normal".equals(riskType)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"身份证不合法（翻拍/复印件/临时身份证...）");
            }

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
    }



    static String getAccessToken(String api_key,String secret_key) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + api_key
                + "&client_secret=" + secret_key);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new org.json.JSONObject(response.body().string()).getString("access_token");
    }

    public static String decode(String base64Str) {
        // 解码
        byte [] base64Data = Base64.getDecoder().decode(base64Str);
        // byte[]-->String（解码后的字符串）
        String str = new String(base64Data, StandardCharsets.UTF_8);
        return str;
    }

    public static boolean isValidID(String id) {
        // 首先检查身份证号是否为空
        if (id == null || id.isEmpty()) {
            return false;
        }

        // 正则表达式匹配身份证号格式
        String regex = "\\d{17}[0-9Xx]";
        if (!id.matches(regex)) {
            return false;
        }

        // 验证身份证号最后一位校验码
        char[] chars = id.toUpperCase().toCharArray();
        int[] coefficient = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (chars[i] - '0') * coefficient[i];
        }
        int remainder = sum % 11;
        char lastChar = chars[17];
        char expectedChar = checkCode[remainder];

        if (lastChar != expectedChar) {
            return false;
        }

        // 验证身份证号中的生日是否合法
        int year = Integer.parseInt(id.substring(6, 10));
        int month = Integer.parseInt(id.substring(10, 12));
        int day = Integer.parseInt(id.substring(12, 14));

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        if (year < 1900 || year > currentYear || month < 1 || month > 12 || day < 1 || day > 31) {
            return false;
        }

        // 验证闰年和平年的日期范围
        boolean isLeapYear = (year % 400 == 0) || (year % 4 == 0 && year % 100 != 0);
        int[] daysInMonth = {31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (day > daysInMonth[month - 1]) {
            return false;
        }

        // 最后都通过了，则认为身份证号合法
        return true;
    }

}
