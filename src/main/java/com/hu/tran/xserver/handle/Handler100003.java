package com.hu.tran.xserver.handle;

import com.hu.tran.xserver.util.CommonUtil;

import java.util.Map;

/**
 * @author hutiantian
 * @date: 2018/7/18 11:53
 * @since 1.0.0
 */
public class Handler100003 implements Handler {
    public void handler(Map<String,Object> request, Map<String,Object> response){
        response.put("status","S");
        response.put("txnSeqId","xx"+CommonUtil.getRandString(12));
    }
}
