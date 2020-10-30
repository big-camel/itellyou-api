package com.itellyou.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.InetAddress;

public class IPUtils {

    private static final Logger logger = LoggerFactory.getLogger(IPUtils.class);

    public static Long toLong(HttpServletRequest request){
        return toLong(request,0l);
    }
    public static Long toLong(HttpServletRequest request,Long defaultValue){
        String ip = getClientIp(request);
        return toLong(ip,defaultValue);
    }

    public static Long toLong(String ip){
        return toLong(ip,0l);
    }
    public static Long toLong(String ip,Long defaultValue) {
        if(StringUtils.isEmpty(ip) || !Util.isIpAddress(ip)) return defaultValue;
        return Util.ip2long(ip);
    }

    public static String toString(Long ip) {
        return Util.long2ip(ip);
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegionModel {
        private String country;
        private String area;
        private String province;
        private String city;
        private String isp;
    }

    public static RegionModel getRegion(long ip){
        try {
            //根据ip进行位置信息搜索
            DbConfig config = new DbConfig();
            // 读取本地的ip2region.db文件
            String rootPath = System.getProperty("user.dir");
            String dbPath = rootPath + File.separator + ".data/ip2region.db";
            File file = new File(dbPath);

            if (file.exists() == false) {
                logger.warn("ip2region not exists");
                return null;
            }
            DbSearcher searcher = new DbSearcher(config, dbPath);
            //采用Btree搜索
            DataBlock block = searcher.btreeSearch(ip);
            String regionString = block.getRegion();
            String[] regionArray = StringUtils.split(regionString,'|');
            RegionModel regionModel = new RegionModel();
            if(StringUtils.isEmpty(regionArray[0]) || regionArray[0].equals("0")) return null;
            regionModel.setCountry(regionArray[0]);
            regionModel.setArea(StringUtils.isEmpty(regionArray[1]) || regionArray[1].equals("0") ? "" : regionArray[1]);
            regionModel.setProvince(StringUtils.isEmpty(regionArray[2]) || regionArray[2].equals("0") ? "" : regionArray[2]);
            regionModel.setCity(StringUtils.isEmpty(regionArray[3]) || regionArray[3].equals("0") ? "" : regionArray[3]);
            regionModel.setIsp(StringUtils.isEmpty(regionArray[4]) || regionArray[4].equals("0") ? "" : regionArray[4]);
            return regionModel;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    public static RegionModel getRegion(String ip){
        return getRegion(toLong(ip));
    }

    public static RegionModel getRegion(HttpServletRequest request){
        return getRegion(toLong(request));
    }
}
