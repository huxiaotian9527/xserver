package com.hu.tran.xserver.core;

import com.hu.tran.xserver.pack.Field;
import com.hu.tran.xserver.pack.Pack;
import com.hu.tran.xserver.pack.PackMapper;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务处理类
 * @author hutiantian
 * @create 2018/6/12 10:05
 * @since 1.0.0
 */
public class TaskExecutor {
    private static final Logger log = LoggerFactory.getLogger(TaskExecutor.class);

    private static final String charset = "utf-8";				//请求消息编码
    private static final int lengthInfo = 8;				    //请求报文中长度字节的位数
    private static TaskExecutor taskHandler = new TaskExecutor();

    private TaskExecutor(){}

    public static  TaskExecutor getInstance(){
        return taskHandler;
    }

    /**
     * 消息处理类
     */
    public void execute(ByteArrayOutputStream baos) throws Exception{
        byte[] origin = baos.toByteArray();                     //原始报文长度
        baos.reset();                                           //重置baos
        byte[] copy = new byte[origin.length-lengthInfo];       //截取字段后的长度
        //截取除长度字段外的xml报文
        System.arraycopy(origin, lengthInfo, copy, 0, origin.length - lengthInfo);
        Document reqDoc =  DocumentHelper.parseText(new String(copy,charset));
        Document resDoc =  DocumentHelper.createDocument();
        String serviceCode = reqDoc.getRootElement().element("SYS_HEAD").element("ServiceCode").getText();
        Map<String,Object> request = new HashMap<String,Object>();		//传给具体handler类的request
        Map<String,Object> response = new HashMap<String,Object>();		//传给具体handler类的response
        //获取请求ServiceCode对应的pack对象
        Pack pack = PackMapper.getInstance().getPack(serviceCode);
        if(pack==null){             //没有serviceId的情况下，直接返回错误报文，不记录日志
            log.error("未找到请求服务编码"+serviceCode+"对应的服务！");
            response.put("code","");
            response.put("message","未找到请求服务编码"+serviceCode+"对应的服务！");
            return;
        }
        //将请求报文按xml标签转换后赋值给request对象
        try{
            unpackRequest(reqDoc.getRootElement(),pack,request);
        }catch(Exception e){
            log.error("MQ消息处理----------解析请求报文"+serviceCode+"异常！",e);
            if(e.getMessage().startsWith("NULLABLE")){			//必送字段未上从报错
                response.put("code","");
                response.put("message",e.getMessage().split(",")[1]);
            }else{
                response.put("code","");
                response.put("message","解析请求报文异常！");
            }
            return;
        }
        //调用xml中配置的serviceId对应的处理类handler方法
        try{
            //处理结果为失败，直接返回错误信息错误码，不需要添加响应字段
            pack.getHandler().handler(request, response);
        }catch(Exception e){
            log.error("MQ消息处理----------解析请求报文"+serviceCode+"异常！",e);
            return;
        }




    }

    /**
     * 将请求报文中的字段按xml配置赋值给request
     * @param root 请求报文的根元素
     * @param pack 请求serviceId对应的MQPack对象
     * @param request 请求报文转换成map后的指针
     */
    private void unpackRequest(Element root,Pack pack,Map<String,Object> request) throws Exception{
        //遍历请求字段，
        for(int i=0;i<pack.getRequestList().size();i++){
            Element tempRoot = root;
            Field field = pack.getRequestList().get(i);
            String loop = field.getLoop();
            if(loop.equals("")){							//非循环域字段
                String value = getValueFromXml(tempRoot,field.getTag());
                //目前只对上送报文的非循环域标签的字段做非空判断，其它非空校验有时间在优化
                if(field.getNullable().equals("true")&&value.equals("")){
                    throw new Exception("NULLABLE异常,必传字段："+field.getDesc()+"未上送或值为空！");
                }
                request.put(field.getName(), value);
            }else{											//循环域字段一次性处理完
                ArrayList<Field> fieldList = new ArrayList<Field>();
                List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
                for(Field field1:pack.getRequestList()){
                    if(loop.equals(field1.getLoop())){
                        fieldList.add(field1);
                    }
                }
                getLoopFromXml(fieldList,mapList,tempRoot);
                if(mapList.size()==0){					//xml循环域标签不符合规范，循环内标签默认给空
                    Map<String,String> map = new HashMap<String,String>();
                    for(Field field2:fieldList){
                        map.put(field2.getName(), "");
                    }
                    mapList.add(map);
                }
                request.put(loop, mapList);
                i = i+fieldList.size()-1;
            }
        }
        //返回公共报文头的部分字段赋值
        request.put("branch_id",root.element("Service_Header").element("branch_id").getTextTrim());		//机构号
    }

    /**
     * 解析请求xml的普通标签
     * @param elem xml的root
     * @param tag 请求字段对应的tag
     * @return 该tag在xml中的value，未找到至赋空字符串
     */
    private String getValueFromXml(Element elem,String tag){
        String value = "";
        String[] strAarry = tag.split("/");
        for(int i=1;i<strAarry.length;i++){
            if((elem = elem.element(strAarry[i]))==null){		//xml中没有此标签，默认空
                break;
            }
            if(i==strAarry.length-1){
                value = elem.getTextTrim();
            }
        }
        return value;
    }

    /**
     * 解析请求xml的循环标签
     * @param list 同一loop的标签集合
     * @param mapList 解析后的结果list
     * @param elem xml的root
     */
    private void getLoopFromXml(ArrayList<Field> list,List<Map<String,String>> mapList,Element elem){
        String[] strAarry = list.get(0).getTag().split("/");
        //这里默认tag标签的倒数第二层为循环标签，且标签长度大于3
        for(int i=1;i<strAarry.length-2;i++){				//遍历到循环域标签的倒数第三位
            if((elem = elem.element(strAarry[i]))==null){
                break;
            }
            if(i==strAarry.length-3){
                List<Element> elemList = elem.elements(strAarry[i+1]); //拿到循环域
                if(elemList==null){
                    break;
                }
                for(Element ele:elemList){								//遍历循环域标签
                    Map<String,String> map = new HashMap<String,String>();
                    for(Field field:list){							//遍历循环域字段并赋值
                        Element e = ele;
                        String[] temp = field.getTag().split("/");
                        if((e = e.element(temp[temp.length-1]))==null){
                            map.put(field.getName(), "");
                        }else{
                            map.put(field.getName(), e.getTextTrim());
                        }
                    }
                    mapList.add(map);
                }
            }
        }
    }
}
