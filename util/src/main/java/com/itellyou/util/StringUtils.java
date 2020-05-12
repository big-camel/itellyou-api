package com.itellyou.util;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import sun.misc.BASE64Encoder;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static String createToken(){
        return createToken("");
    }
    public static String createToken(String key){
        String randomStr = randomString(10);
        String uuid = createUUID();
        String token = System.currentTimeMillis() + randomStr + key + uuid;
        token = md5(token);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(token.getBytes());
    }

    public static String md5(String str){
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] md5 =  md.digest(str.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(md5);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createUUID(){
        return UUID.randomUUID().toString();
    }

    public static String encoderPassword(String password){
        password = md5("itellyou" + password);
        String left = password.substring(0,8) + "itellyou_left";
        String center = new StringBuilder(password.substring(8,16) + "itellyou_center").reverse().toString();
        String right = password.substring(16) + "itellyou_right";
        password = md5(left) + md5(center) + md5(right);
        return md5(password);
    }

    @Getter
    public enum RandomType {
        DEFAULT(0,"default"),
        NUMBER(1,"number"),
        UPPERCASE(2,"uppercase"),
        LOWERCASE(3,"lowercase");

        private int value;
        private String type;
        RandomType(int value , String type){
            this.value = value;
            this.type = type;
        }
    }

    public static String randomString(int length){
        return randomString(length,RandomType.DEFAULT);
    }

    public static String randomString(int length,RandomType type){
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        switch (type.getValue()){
            case 1:
                str = "0123456789";
                break;
            case 2:
                str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                break;
            case 3:
                str = "abcdefghijklmnopqrstuvwxyz";
                break;
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            sb.append(str.charAt(random.nextInt(str.length())));
        }
        return sb.toString();
    }

    /**
     * 去除html代码中含有的标签
     * @param htmlStr
     * @return
     */
    public static String removeHtmlTags(String htmlStr) {
        //定义script的正则表达式，去除js可以防止注入
        String scriptRegex="<script[^>]*?>[\\s\\S]*?<\\/script>";
        //定义style的正则表达式，去除style样式，防止css代码过多时只截取到css样式代码
        String styleRegex="<style[^>]*?>[\\s\\S]*?<\\/style>";
        //定义HTML标签的正则表达式，去除标签，只提取文字内容
        String lineRegex = "<\\/p[^>]*?>";
        String brRegex = "<br[^>]*?\\/>";
        String htmlRegex="<[^>]+>";
        //定义空格,回车,换行符,制表符
        String spaceRegex = "\\s*|\t|\r|\n";
        htmlStr = htmlStr.replaceAll(spaceRegex, "");
        // 过滤script标签
        htmlStr = htmlStr.replaceAll(scriptRegex, "");
        // 过滤style标签
        htmlStr = htmlStr.replaceAll(styleRegex, "");
        // p标签和br标签替换城换行符
        htmlStr = htmlStr.replaceAll(lineRegex, "");
        htmlStr = htmlStr.replaceAll(brRegex,"");
        // 过滤html标签
        htmlStr = htmlStr.replaceAll(htmlRegex, "");
        // 过滤空格等
        //htmlStr = htmlStr.replaceAll(spaceRegex, "");
        return htmlStr.trim(); // 返回文本字符串
    }

    public static String getFragmenter(String content){
        return getFragmenter(content,100);
    }

    public static String getFragmenter(String content,int size){
        content = removeHtmlTags(content);
        return StringUtils.left(content,size) + (content.length() > size ? "..." : "");
    }

    public static List<String> getEditorContentAttrByName(String html, String element,String name, String attr) {
        List<String> result = new ArrayList<>();
        String reg = "<" + element + "[^<>]*?\\sname=['\"]?" + name + "['\"]?[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(html);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        return result;
    }

    public static List<JSONObject> getEditorElectionValueToJSON(String html, String element,String name, String attr){
        List<String> values = getEditorContentAttrByName(html,element,name,attr);
        List<JSONObject> data = new ArrayList<>();
        for (String value : values){
            data.add(getEditorElectionValueToJSON(value));
        }
        return data;
    }

    public static JSONObject getEditorElectionValueToJSON(String value){
        String prefix = "data:";
        try {
            if(StringUtils.startsWith(value,prefix)){
                value = URLDecoder.decode(value, "UTF-8");
                value = StringUtils.substringAfter(value,prefix);
            }
            return JSONObject.parseObject(value);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getEditorContentCover(String content){
        List<JSONObject> imagesValue = getEditorElectionValueToJSON(content,"section","image","value");
        JSONObject checkedValue = null;
        for (JSONObject imageValue : imagesValue){
            String status = imageValue.getString("status");
            if("done".equals(status)){
                int width = imageValue.getIntValue("originWidth");
                int height = imageValue.getIntValue("originHeight");
                if(checkedValue == null && width >= 240 && height >= 150){
                    checkedValue = imageValue;
                }else if(checkedValue != null && width > checkedValue.getIntValue("originWidth") && height > checkedValue.getIntValue("originHeight")){
                    checkedValue = imageValue;
                }
            }
        }
        return checkedValue;
    }
}
