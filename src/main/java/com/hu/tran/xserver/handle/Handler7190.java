package com.hu.tran.xserver.handle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/15 11:27
 * @since 1.0.0
 */
public class Handler7190 implements Handler{

    public void handler(Map<String,Object> request, Map<String,Object> response){
        response.put("ReturnCode","00000000000000");
        response.put("AcctBal","10000000000.0");
    }
}
