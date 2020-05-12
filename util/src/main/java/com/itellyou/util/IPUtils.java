package com.itellyou.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

public class IPUtils {
    public static Long toLong(HttpServletRequest request){
        return toLong(request,0l);
    }
    public static Long toLong(HttpServletRequest request,Long defaultValue){
        String ip = getClientIp(request);
        return toLong(ip,defaultValue);
    }

    public static Long toLong(String ipv4_string){
        return toLong(ipv4_string,0l);
    }
    public static Long toLong(String ipv4_string,Long defaultValue) {
        if(!StringUtils.isNotEmpty(ipv4_string) || ipv4_string == "unknow"){
            return defaultValue;
        }
        // 取 ip 的各段
        String[] ipSlices = ipv4_string.split("\\.");

        long result = 0;

        for (int i = 0; i < ipSlices.length; i++) {
            // 将 ip 的每一段解析为 int，并根据位置左移 8 位
            long intSlice = Long.parseLong(ipSlices[i]) << 8 * i;
            // 求或
            result = result | intSlice;
        }

        return result;
    }

    public static String toIpv4(Long ipv4Long) {
        String[] ipString = new String[4];

        for (int i = 0; i < 4; i++) {
            // 每 8 位为一段，这里取当前要处理的最高位的位置
            int pos = i * 8;

            // 取当前处理的 ip 段的值
            Long and = ipv4Long & (255 << pos);

            // 将当前 ip 段转换为 0 ~ 255 的数字，注意这里必须使用无符号右移
            ipString[i] = String.valueOf(and >>> pos);
        }

        return String.join(".", ipString);
    }

    public static String getClientIp(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknow".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getHeader ("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
            ip = request.getRemoteAddr ();
            if (ip.equals ("127.0.0.1") || ip.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost ();
                } catch (Exception e) {
                    e.printStackTrace ();
                }
                ip = inet.getHostAddress ();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length () > 15) {
            if (ip.indexOf (",") > 0) {
                ip = ip.substring (0, ip.indexOf (","));
            }
        }
        return ip;
    }
}
