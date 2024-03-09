package com.yupi.springbootinit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author wzc
 * 2023/8/7
 */
@Slf4j
public class JwtUtil {

    //常量（单位为ms）  为了测试，设为二十天
    public static final long EXPIRE = 1000 * 60 * 60 * 24 *20; //token过期时间
    public static final String APP_SECRET = "Everything will be OK"; //秘钥，加盐

    //	@param id 当前用户ID
    //	@param issuer 该JWT的签发者，是否使用是可选的
    //	@param subject 该JWT所面向的用户，是否使用是可选的
    //	@param ttlMillis 什么时候过期，这里是一个Unix时间戳，是否使用是可选的
    //	@param audience 接收该JWT的一方，是否使用是可选的
    //生成token字符串的方法
    public static String createToken(String id) {

        String JwtToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")    //头部信息
                .setHeaderParam("alg", "HS256")    //头部信息
                //下面这部分是payload部分
                // 设置默认标签
                .setSubject("dailyblue")    //设置jwt所面向的用户
                .setIssuedAt(new Date())    //设置签证生效的时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))    //设置签证失效的时间
                //自定义的信息，这里存储id和姓名信息
                .claim("id", id)  //设置token主体部分 ，存储用户信息
                //下面是第三部分
                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();
        // 生成的字符串就是jwt信息，这个通常要返回出去
        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * 直接判断字符串形式的jwt字符串
     *
     * @param jwtToken
     * @return
     */
    public static boolean verify(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)){
            return false;
        }
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * 因为通常jwt都是在请求头中携带，此方法传入的参数是请求
     *
     * @param request
     * @return
     */
    public static boolean verify(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");//注意名字必须为token才能获取到jwt
            if (StringUtils.isEmpty(jwtToken)){
                return false;
            }
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token字符串获取会员id
     * 这个方法也直接从http的请求中获取id的
     *
     */
    public static String getUserIdByToken(String jwtToken) {
        if (StringUtils.isEmpty(jwtToken)){
            return "";
        }
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        return claims.get("id").toString();
    }

    /**
     * 解析JWT
     * @param jwt
     * @return
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwt).getBody();
        return claims;
    }
}