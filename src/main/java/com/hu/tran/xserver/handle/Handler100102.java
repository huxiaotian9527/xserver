package com.hu.tran.xserver.handle;

import com.hu.tran.xserver.util.CommonUtil;

import java.util.Map;

/**
 * @author hutiantian
 * @date: 2018/7/18 14:21
 * @since 1.0.0
 */
public class Handler100102 implements Handler{

    public void handler(Map<String,Object> request, Map<String,Object> response){
        response.put("status","S");
        response.put("txnSeqId","yy"+CommonUtil.getRandString(12));
    }
}
