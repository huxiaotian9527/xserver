package com.hu.tran.xserver.util;

import java.util.Random;

/**
 * @author hutiantian
 * @date: 2018/7/18 14:18
 * @since 1.0.0
 */
public class CommonUtil {

    public static String getRandString(int len){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++){
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }
}
