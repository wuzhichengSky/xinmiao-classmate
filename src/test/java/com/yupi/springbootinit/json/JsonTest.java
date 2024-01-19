package com.yupi.springbootinit.json;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wzc
 * 2024/1/16
 */
@SpringBootTest
public class JsonTest {

    @Test
    void toJsonStr(){
        List<String> str= new ArrayList<>();
        str.add("apple");
        str.add("banana");
        str.add("orange");
        System.out.println(str);
        for (String tag : str) {
            System.out.println(tag);
        }
    }
}
