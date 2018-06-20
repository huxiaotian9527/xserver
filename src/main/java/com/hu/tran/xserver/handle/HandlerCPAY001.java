package com.hu.tran.xserver.handle;

import java.util.Map;

/**
 * @author hutiantian
 * @create 2018/6/20 11:33
 * @since 1.0.0
 */
public class HandlerCPAY001 implements Handler{

    public void handler(Map<String,Object> request, Map<String,Object> response){
        response.put("Mac","0000000000001");
        response.put("TargetSysId","300050");
        response.put("MsgId","2bc7f1cc-3757-4381-b9c8-5ba787de39aa");
        response.put("ReturnCode","00000000000000");
        response.put("ReturnMsg","交易成功");
        response.put("TranDate","20180508");
        response.put("TranTime","20180508");
        response.put("GlobalSeqNo","3000501506240286ad09080063e7");


        response.put("CtrNo","123456");
    }
}
