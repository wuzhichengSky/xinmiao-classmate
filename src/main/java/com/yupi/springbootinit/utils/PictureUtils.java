package com.yupi.springbootinit.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * @author wzc
 * 2024/1/31
 *
 * 图片处理工具类
 */
public class PictureUtils {
    public static String base64Encode(MultipartFile picture) throws IOException {
        // 创建临时文件
        File file = File.createTempFile("temp", ".txt");
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            // 获取文件输入流
            inputStream = picture.getInputStream();

            if (!file.exists()) {
                file.createNewFile();
            }
            // 创建输出流
            outputStream = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len;
            // 写入到创建的临时文件
            while ((len = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // 关流
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (outputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 将图片文件转化为字节数组
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        byte[] data = baos.toByteArray();

        fis.close();
        baos.close();

        // 使用Base64编码算法将字节数组转化为Base64编码的字符串
        String base = Base64.getEncoder().encodeToString(data);

        return base;
    }

    public static String base64Encode(File file) throws IOException {
        // 将图片文件转化为字节数组
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }

        byte[] data = baos.toByteArray();

        fis.close();
        baos.close();

        // 使用Base64编码算法将字节数组转化为Base64编码的字符串
        String base = Base64.getEncoder().encodeToString(data);

        return base;
    }
}
