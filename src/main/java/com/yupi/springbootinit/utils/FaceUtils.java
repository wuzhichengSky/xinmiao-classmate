package com.yupi.springbootinit.utils;


import com.alibaba.fastjson.JSONObject;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.utils.baiduyun.GsonUtils;
import com.yupi.springbootinit.utils.baiduyun.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
* 人脸识别工具类
*/
@Slf4j
public class FaceUtils {

    private static String API_KEY="donid8HqNGyF0yaGvpTsZb5e";

    private static String SECRET_KEY="8qbWW45ypVVGqsE7OjoOVWuBYzFgqRt0";

    /**
     * 人脸注册
     * @param photo
     * @param userId
     * @return
     * @throws Exception
     */
    public static String add(MultipartFile photo,String userId) throws Exception {
        String image = PictureUtils.base64Encode(photo);
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
        Map<String, Object> map = new HashMap<>();
        map.put("image", image);
        map.put("group_id", "xinmiao");
        map.put("user_id", userId);
        map.put("image_type", "BASE64");
        map.put("quality_control", "LOW");

        String param = GsonUtils.toJson(map);

        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = IdcardUtils.getAccessToken(API_KEY,SECRET_KEY);
        String result = HttpUtil.post(url, accessToken, "application/json", param);
        log.info("注册人脸返回结果:"+result);

        if(!JSONObject.parseObject(result).get("error_code").toString().equals("0")){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return result;
    }


    /**
     * 人脸对比
     * @param idCard 身份证照片
     * @param cert   证件照
     * @return
     * @throws Exception
     */
    public static Boolean faceMatch(MultipartFile idCard,MultipartFile cert) throws Exception {
        String id = PictureUtils.base64Encode(idCard);
        String ce = PictureUtils.base64Encode(cert);

        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        Map<String, Object> map1 = new HashMap<>();
        map1.put("image", id);
        map1.put("image_type", "BASE64");
        map1.put("face_type", "IDCARD");

        Map<String, Object> map2 = new HashMap<>();
        map2.put("image", ce);
        map2.put("image_type", "BASE64");
        map2.put("face_type", "CERT");

        List<Map> lists = new ArrayList<>();
        lists.add(map1);
        lists.add(map2);

        String param = GsonUtils.toJson(lists);

        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = IdcardUtils.getAccessToken(API_KEY,SECRET_KEY);

        String result = HttpUtil.post(url, accessToken, "application/json", param);
        log.info("人脸对比返回结果:"+result);

        if(!JSONObject.parseObject(result).get("error_code").toString().equals("0")){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        String score = JSONObject.parseObject(result).getJSONObject("result").get("score").toString();

        if(Double.parseDouble(score)>=80){
            return true;
        }else{
            return false;
        }
    }

}
