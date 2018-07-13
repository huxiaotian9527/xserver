package com.hu.tran.xserver.handle;

import java.util.Map;
import java.util.Random;

/**
 * @author hutiantian
 * @create 2018/6/20 11:33
 * @since 1.0.0
 */
public class HandlerCPAY001 implements Handler{

    public void handler(Map<String,Object> request, Map<String,Object> response){
        if(request.get("ServiceScene").equals("02")){
            response.put("CtrNo","JK"+getRandString(12));
        }else {
            response.put("DbtNo","JJ"+getRandString(12));
        }
        response.put("ReturnCode","00000000000000");
        response.put("BackendSeqNo","TT"+getRandString(16));

    }

    private String getRandString(int len){
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<len;i++){
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }
}
