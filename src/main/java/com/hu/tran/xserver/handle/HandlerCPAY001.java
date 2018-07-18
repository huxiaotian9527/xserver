package com.hu.tran.xserver.handle;

import com.hu.tran.xserver.util.CommonUtil;

import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/20 11:33
 * @since 1.0.0
 */
public class HandlerCPAY001 implements Handler{

    public void handler(Map<String,Object> request, Map<String,Object> response){
        if(request.get("ServiceScene").equals("02")){
            response.put("CtrNo","JK"+CommonUtil.getRandString(12));
        }else {
            response.put("DbtNo","JJ"+CommonUtil.getRandString(12));
        }
        response.put("ReturnCode","00000000000000");
        response.put("BackendSeqNo","TT"+CommonUtil.getRandString(16));

    }

}
