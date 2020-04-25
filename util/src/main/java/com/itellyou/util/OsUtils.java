package com.itellyou.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsUtils {
    public static class OsModel {
        private String name;
        private String[] alias;
        private String[] excludes;

        public OsModel(String name,String[] alias,String[] excludes){
            this.name = name;
            this.alias = alias;
            this.excludes = excludes;
        }

        public String getName(){
            return this.name;
        }

        public String[] getAlias(){
            return this.alias;
        }

        public String[] getExcludes(){
            return this.excludes;
        }

        public OsModel(String name,String... alias){
            this(name,alias,null);
        }

        public OsModel(String name){
            this(name,null,null);
        }
    }

    private static List<OsModel> getList(){
        List<OsModel> list = new ArrayList<>();
        list.add(new OsModel("Windows 10","Windows NT 6.4","Windows NT 10"));
        list.add(new OsModel("Windows 8.1","Windows NT 6.3"));
        list.add(new OsModel("Windows 8",new String[]{"Windows NT 6.2"},new String[]{"Xbox","Xbox One"}));
        list.add(new OsModel("Windows 7",new String[]{"Windows NT 6.1"},new String[]{"Xbox","Xbox One"}));
        list.add(new OsModel("Windows Vista",new String[]{"Windows NT 6"},new String[]{"Xbox","Xbox One"}));
        list.add(new OsModel("Windows 2000","Windows NT 5.0"));
        list.add(new OsModel("Windows XP",new String[]{"Windows NT 5"},new String[]{"ggpht.com"}));
        list.add(new OsModel("Windows 10 Mobile","Windows Phone 10"));
        list.add(new OsModel("Windows Phone 8.1","Windows Phone 8.1"));
        list.add(new OsModel("Windows Phone 8","Windows Phone 8"));
        list.add(new OsModel("Windows Phone 7","Windows Phone OS 7"));
        list.add(new OsModel("Windows Mobile","Windows CE"));
        list.add(new OsModel("Windows 98",new String[]{"Windows 98","Win98"},new String[]{"Palm"}));
        list.add(new OsModel("Xbox OS","xbox"));
        list.add(new OsModel("Windows",new String[]{"Windows"},new String[]{"Palm","ggpht.com"}));
        list.add(new OsModel("Android 6.x",new String[]{"Android 6","Android-6"},new String[]{"glass"}));
        list.add(new OsModel("Android 6.x Tablet",new String[]{"Android 6","Android-6"},new String[]{"mobile","glass"}));
        list.add(new OsModel("Android 5.x",new String[]{"Android 5","Android-5"},new String[]{"glass"}));
        list.add(new OsModel("Android 5.x Tablet",new String[]{"Android 5","Android-5"},new String[]{"mobile","glass"}));
        list.add(new OsModel("Android 4.x",new String[]{"Android 4","Android-4"},new String[]{"glass","ubuntu"}));
        list.add(new OsModel("Android 4.x Tablet",new String[]{"Android 4","Android-4"},new String[]{"glass","ubuntu","mobile"}));
        list.add(new OsModel("Android 4.x",new String[]{"Android 4"},new String[]{"ubuntu"}));
        list.add(new OsModel("Android 3.x Tablet","Android 3"));
        list.add(new OsModel("Android 2.x","Android 2"));
        list.add(new OsModel("Android 2.x Tablet","Kindle Fire","GT-P1000","SCH-I800"));
        list.add(new OsModel("Android 1.x","Android 1"));
        list.add(new OsModel("Android Mobile",new String[]{"Mobile"},new String[]{"ubuntu"}));
        list.add(new OsModel("Android Tablet","Tablet"));
        list.add(new OsModel("Android",new String[]{"Android"},new String[]{"Ubuntu"}));
        list.add(new OsModel("Chrome OS","CrOS"));
        list.add(new OsModel("WebOS","webOS"));
        list.add(new OsModel("PalmOS","Palm"));
        list.add(new OsModel("MeeGo","MeeGo"));
        list.add(new OsModel("iOS 9 (iPhone)","iPhone OS 9"));
        list.add(new OsModel("iOS 8.4 (iPhone)","iPhone OS 8_4"));
        list.add(new OsModel("iOS 8.3 (iPhone)","iPhone OS 8_3"));
        list.add(new OsModel("iOS 8.2 (iPhone)","iPhone OS 8_2"));
        list.add(new OsModel("iOS 8.1 (iPhone)","iPhone OS 8_1"));
        list.add(new OsModel("iOS 8 (iPhone)","iPhone OS 8"));
        list.add(new OsModel("iOS 7 (iPhone)","iPhone OS 7"));
        list.add(new OsModel("iOS 6 (iPhone)","iPhone OS 6"));
        list.add(new OsModel("iOS 5 (iPhone)","iPhone OS 5"));
        list.add(new OsModel("iOS 4 (iPhone)","iPhone OS 4"));
        list.add(new OsModel("iOS 9 (iPad)","OS 9"));
        list.add(new OsModel("iOS 8.4 (iPad)","OS 8_4"));
        list.add(new OsModel("iOS 8.3 (iPad)","OS 8_3"));
        list.add(new OsModel("iOS 8.2 (iPad)","OS 8_2"));
        list.add(new OsModel("iOS 8.1 (iPad)","OS 8_1"));
        list.add(new OsModel("iOS 8 (iPad)","OS 8_0"));
        list.add(new OsModel("iOS 7 (iPad)","OS 7"));
        list.add(new OsModel("iOS 6 (iPad)","OS 6"));
        list.add(new OsModel("Mac OS X (iPad)","iPad"));
        list.add(new OsModel("Mac OS X (iPhone)","iPhone"));
        list.add(new OsModel("Mac OS X (iPod)","iPod"));
        list.add(new OsModel("iOS","iPhone OS","like Mac OS X"));
        list.add(new OsModel("Mac OS X","Mac OS X","CFNetwork"));
        list.add(new OsModel("Mac OS","Mac"));
        list.add(new OsModel("Maemo","Maemo"));
        list.add(new OsModel("Bada","Bada"));
        list.add(new OsModel("Android (Google TV)","GoogleTV"));
        list.add(new OsModel("Linux (Kindle 3)","Kindle/3"));
        list.add(new OsModel("Linux (Kindle 2)","Kindle/2"));
        list.add(new OsModel("Linux (Kindle)","Kindle"));
        list.add(new OsModel("Ubuntu","ubuntu"));
        list.add(new OsModel("Ubuntu Touch (mobile)","mobile"));
        list.add(new OsModel("Linux","Linux","CamelHttpStream"));
        list.add(new OsModel("Symbian OS 9.x","SymbianOS/9","Series60/3"));
        list.add(new OsModel("Symbian OS 8.x","SymbianOS/8","Series60/2.6","Series60/2.8"));
        list.add(new OsModel("Symbian OS 7.x","SymbianOS/7"));
        list.add(new OsModel("Symbian OS 6.x","SymbianOS/6"));
        list.add(new OsModel("Symbian OS","Symbian","Series60"));
        list.add(new OsModel("Series 40","Nokia6300"));
        list.add(new OsModel("Sony Ericsson","SonyEricsson"));
        list.add(new OsModel("SunOS","SunOS"));
        list.add(new OsModel("Sony Playstation","Playstation"));
        list.add(new OsModel("Nintendo Wii","Wii"));
        list.add(new OsModel("BlackBerry 7","Version/7"));
        list.add(new OsModel("BlackBerry 6","Version/6"));
        list.add(new OsModel("BlackBerry Tablet OS","RIM Tablet OS"));
        list.add(new OsModel("BlackBerryOS","BlackBerry"));
        list.add(new OsModel("Roku OS","Roku"));
        list.add(new OsModel("Proxy","ggpht.com"));
        list.add(new OsModel("Unknown mobile","Mobile"));
        list.add(new OsModel("Unknown tablet","Tablet"));
        list.add(new OsModel("Unknown"));
        return list;
    }

    public static String getClientOs(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (!StringUtils.isNotEmpty(userAgent)) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();
        List<OsModel> list = getList();
        for (OsModel os : list){
            String[] alias = os.getAlias();
            String[] excludes = os.getExcludes();
            if(alias!= null && alias.length > 0){
                for(String name : alias){
                    name = name.toLowerCase();
                    boolean isExclude = false;
                    if(userAgent.indexOf(name) >= 0){
                        if(excludes != null && excludes.length > 0){
                            for (String exclude : excludes){
                                isExclude = userAgent.indexOf(exclude) >= 0;
                            }
                        }
                        if(isExclude == false){
                            return os.getName();
                        }
                    }
                }
            }
        }
        return "Unknown";
    }
}
