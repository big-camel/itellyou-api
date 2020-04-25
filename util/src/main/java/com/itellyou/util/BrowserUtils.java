package com.itellyou.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class BrowserUtils {
    public static class BrowserModel {
        private String name;
        private String[] alias;
        private String[] excludes;

        public BrowserModel(String name,String[] alias,String[] excludes){
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

        public BrowserModel(String name,String... alias){
            this(name,alias,null);
        }

        public BrowserModel(String name){
            this(name,null,null);
        }
    }

    private static List<BrowserModel> getList(){
        List<BrowserModel> list = new ArrayList<>();
        list.add(new BrowserModel("Outlook 2007","MSOffice 12"));
        list.add(new BrowserModel("Outlook 2013","Microsoft Outlook 15"));
        list.add(new BrowserModel("Outlook 2010","MSOffice 14","Microsoft Outlook 14"));
        list.add(new BrowserModel("Outlook","MSOffice"));
        list.add(new BrowserModel("Windows Live Mail","Outlook-Express/7.0"));
        list.add(new BrowserModel("IE Mobile 11","IEMobile/11"));
        list.add(new BrowserModel("IE Mobile 10","IEMobile/10"));
        list.add(new BrowserModel("IE Mobile 9","IEMobile/9"));
        list.add(new BrowserModel("IE Mobile 7","IEMobile/7"));
        list.add(new BrowserModel("IE Mobile 6","IEMobile/6"));
        list.add(new BrowserModel("Xbox","xbox"));
        list.add(new BrowserModel("Internet Explorer 11",new String[]{"Trident/7","IE 11."},new String[]{"MSIE 7","BingPreview"}));
        list.add(new BrowserModel("Internet Explorer 10","MSIE 10"));
        list.add(new BrowserModel("Internet Explorer 9","MSIE 9"));
        list.add(new BrowserModel("Internet Explorer 8","MSIE 8"));
        list.add(new BrowserModel("Internet Explorer 7","MSIE 7"));
        list.add(new BrowserModel("Internet Explorer 6","MSIE 6"));
        list.add(new BrowserModel("Internet Explorer 5.5","MSIE 5.5"));
        list.add(new BrowserModel("Internet Explorer 5","MSIE 5"));
        list.add(new BrowserModel("Internet Explorer",new String[]{"MSIE","Trident","IE"},new String[]{"BingPreview","Xbox","Xbox One"}));
        list.add(new BrowserModel("Microsoft Edge Mobile","Mobile Safari"));
        list.add(new BrowserModel("Microsoft Edge Mobile 12","Mobile Safari","Edge/12"));
        list.add(new BrowserModel("Microsoft Edge 13",new String[]{"Edge/13"},new String[]{"Mobile"}));
        list.add(new BrowserModel("Microsoft Edge 12",new String[]{"Edge/12"},new String[]{"Mobile"}));
        list.add(new BrowserModel("Microsoft Edge","Edge"));
        list.add(new BrowserModel("Chrome Mobile",new String[]{"CrMo","CriOS","Mobile Safari"},new String[]{"OPR/"}));
        for(int i = 100;i >= 40; i--){
            list.add(new BrowserModel("Chrome " + i,new String[]{"Chrome/" + i},new String[]{"OPR/","Web Preview","Vivaldi"}));
        }
        for(int i = 39;i >= 19; i--) {
            list.add(new BrowserModel("Chrome " + i, new String[]{"Chrome/" + i}, new String[]{"OPR/", "Web Preview"}));
        }
        list.add(new BrowserModel("Chrome 18","Chrome/18"));
        list.add(new BrowserModel("Chrome 17","Chrome/17"));
        list.add(new BrowserModel("Chrome 16","Chrome/16"));
        list.add(new BrowserModel("Chrome 15","Chrome/15"));
        list.add(new BrowserModel("Chrome 14","Chrome/14"));
        list.add(new BrowserModel("Chrome 13","Chrome/13"));
        list.add(new BrowserModel("Chrome 12","Chrome/12"));
        list.add(new BrowserModel("Chrome 11","Chrome/11"));
        list.add(new BrowserModel("Chrome 10","Chrome/10"));
        list.add(new BrowserModel("Chrome 9","Chrome/9"));
        list.add(new BrowserModel("Chrome 8","Chrome/8"));
        list.add(new BrowserModel("Chrome",new String[]{"Chrome","CrMo","CriOS"},new String[]{"OPR/","Web Preview","Vivaldi"}));
        list.add(new BrowserModel("Omniweb","OmniWeb"));
        list.add(new BrowserModel("Firefox 3 Mobile","Firefox/3.5 Maemo"));
        list.add(new BrowserModel("Firefox Mobile","Mobile"));
        list.add(new BrowserModel("Firefox Mobile 23","Firefox/23"));
        list.add(new BrowserModel("Firefox Mobile (iOS)","FxiOS"));
        for (int i = 100;i >= 22;i--){
            list.add(new BrowserModel("Firefox " + i,"Firefox/" + i));
        }
        list.add(new BrowserModel("Firefox 21",new String[]{"Firefox/21"},new String[]{"WordPress.com mShots"}));
        list.add(new BrowserModel("Firefox",new String[]{"Firefox","FxiOS"},new String[]{"camino","flock","ggpht.com","WordPress.com mShots"}));
        list.add(new BrowserModel("BlackBerry","BB10"));
        list.add(new BrowserModel("Mobile Safari",new String[]{"Mobile Safari","Mobile/"},new String[]{"bot","preview","OPR/","Coast/","Vivaldi","CFNetwork","FxiOS"}));
        list.add(new BrowserModel("Silk","Silk/"));
        for(int i = 20;i >= 8 ; i--){
            list.add(new BrowserModel("Safari " + i,new String[]{"Version/" + i},new String[]{"Applebot"}));
        }
        list.add(new BrowserModel("Safari 7",new String[]{"Version/7"},new String[]{"bing"}));
        list.add(new BrowserModel("Safari 6",new String[]{"Version/6"}));
        list.add(new BrowserModel("Safari 5",new String[]{"Version/5"},new String[]{"Google Web Preview"}));
        list.add(new BrowserModel("Safari 4",new String[]{"Version/4"},new String[]{"Googlebot-Mobile"}));
        list.add(new BrowserModel("Safari",new String[]{"Safari"},new String[]{"bot","preview","OPR/","Coast/","Vivaldi","CFNetwork","Phantom"}));
        list.add(new BrowserModel("Opera"," Coast/1."));
        list.add(new BrowserModel("Opera"," Coast/"));
        list.add(new BrowserModel("Opera Mobile","Mobile Safari"));
        list.add(new BrowserModel("Opera Mini","Opera Mini"));
        for (int i = 100;i >=15;i--){
            list.add(new BrowserModel("Opera " + i,"OPR/" + i + "."));
        }
        list.add(new BrowserModel("Opera 12","OPR/12","Version/12."));
        list.add(new BrowserModel("Opera 11","Version/11."));
        list.add(new BrowserModel("Opera 10","Opera/9.8"));
        list.add(new BrowserModel("Opera 9","Opera/9"));
        list.add(new BrowserModel("Opera"," OPR/","Opera"));
        list.add(new BrowserModel("Konqueror",new String[]{"Konqueror"},new String[]{"Exabot"}));
        list.add(new BrowserModel("Samsung Dolphin 2","Dolfin/2"));
        list.add(new BrowserModel("iTunes","iTunes"));
        list.add(new BrowserModel("App Store","MacAppStore"));
        list.add(new BrowserModel("Adobe AIR application","AdobeAIR"));
        list.add(new BrowserModel("Apple WebKit",new String[]{"AppleWebKit"},new String[]{"bot","preview","OPR/","Coast/","Vivaldi","Phantom"}));
        list.add(new BrowserModel("Lotus Notes","Lotus-Notes"));
        list.add(new BrowserModel("Camino","Camino"));
        list.add(new BrowserModel("Camino 2","Camino/2"));
        list.add(new BrowserModel("Flock","Flock"));
        list.add(new BrowserModel("Thunderbird 12","Thunderbird/12"));
        list.add(new BrowserModel("Thunderbird 11","Thunderbird/11"));
        list.add(new BrowserModel("Thunderbird 10","Thunderbird/10"));
        list.add(new BrowserModel("Thunderbird 9","Thunderbird/9"));
        list.add(new BrowserModel("Thunderbird 8","Thunderbird/8"));
        list.add(new BrowserModel("Thunderbird 7","Thunderbird/7"));
        list.add(new BrowserModel("Thunderbird 6","Thunderbird/6"));
        list.add(new BrowserModel("Thunderbird 3","Thunderbird/3"));
        list.add(new BrowserModel("Thunderbird 2","Thunderbird/2"));
        list.add(new BrowserModel("Thunderbird","Thunderbird"));
        list.add(new BrowserModel("Vivaldi","Vivaldi"));
        list.add(new BrowserModel("SeaMonkey","SeaMonkey"));
        list.add(new BrowserModel("Mobil Robot/Spider","Googlebot-Mobile"));
        list.add(new BrowserModel("Robot/Spider","Googlebot", "Mediapartners-Google", "Web Preview", "bot", "Applebot", "spider", "crawler", "Feedfetcher", "Slurp",
                "Twiceler", "Nutch", "BecomeBot", "bingbot", "BingPreview", "Google Web Preview", "WordPress.com mShots", "Seznam",
                "facebookexternalhit", "YandexMarket", "Teoma", "ThumbSniper", "Phantom"));
        list.add(new BrowserModel("Mozilla",new String[]{"Mozilla","Moozilla"},new String[]{"ggpht.com"}));
        list.add(new BrowserModel("CFNetwork","CFNetwork"));
        list.add(new BrowserModel("Eudora","Eudora","EUDORA"));
        list.add(new BrowserModel("PocoMail","PocoMail"));
        list.add(new BrowserModel("The Bat!","The Bat"));
        list.add(new BrowserModel("NetFront","NetFront"));
        list.add(new BrowserModel("Evolution","CamelHttpStream"));
        list.add(new BrowserModel("Lynx","Lynx"));
        list.add(new BrowserModel("Downloading Tool","cURL","wget","ggpht.com","Apache-HttpClient"));
        list.add(new BrowserModel("Unknown"));
        return list;
    }

    public static String getClientBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent");
        if (!StringUtils.isNotEmpty(userAgent)) {
            return "Unknown";
        }
        userAgent = userAgent.toLowerCase();
        List<BrowserModel> list = getList();
        for (BrowserModel browser : list){
            String[] alias = browser.getAlias();
            String[] excludes = browser.getExcludes();
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
                            return browser.getName();
                        }
                    }
                }
            }
        }
        return "Unknown";
    }
}
