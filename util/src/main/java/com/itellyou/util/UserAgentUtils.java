package com.itellyou.util;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

import javax.servlet.http.HttpServletRequest;

public class UserAgentUtils {

    public static String getBrowser(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        if (StringUtils.isEmpty(ua)) {
            return Browser.UNKNOWN.getName();
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(ua);

        Browser browser = userAgent.getBrowser();
        return browser.getGroup().getName();
    }

    public static String getOs(HttpServletRequest request) {
        String ua = request.getHeader("user-agent");
        if (StringUtils.isEmpty(ua)) {
            return OperatingSystem.UNKNOWN.getName();
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(ua);

        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        return operatingSystem.getGroup().getName();
    }
}
