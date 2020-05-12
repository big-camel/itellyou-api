package com.itellyou.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {

    private final static String defaultFormat = "yyyy-MM-dd HH:mm:ss";

    public static ZoneId getZoneId(int hours){
        return ZoneOffset.ofHours(hours);
    }

    public static ZoneId getZoneId(){
        return ZoneOffset.ofHours(8);
    }

    public static String format(long timestamp,ZoneId zoneId,String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = toLocalDateTime(timestamp,zoneId);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String format(long timestamp,String format){
        return format(timestamp,getZoneId(),format);
    }

    public static String format(long timestamp,ZoneId zoneId){
        return format(timestamp,zoneId,defaultFormat);
    }

    public static LocalDateTime formatToDateTime(String datetime,String format){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(datetime,dateTimeFormatter);
    }

    public static LocalDateTime formatToDateTime(String datetime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(defaultFormat);
        return LocalDateTime.parse(datetime,dateTimeFormatter);
    }

    public static String format(long timestamp){
        return format(timestamp,defaultFormat);
    }

    public static String format(String format){
        return format(getTimestamp(),getZoneId(),format);
    }

    public static String format(){
        return format(getTimestamp(),defaultFormat);
    }

    public static String formatDuration(long seconds,String format){
        return DurationFormatUtils.formatDuration(seconds * 1000,format,true);
    }

    public static String formatDuration(long seconds){
        return formatDuration(seconds,"HH:mm:ss");
    }

    public static Long getTimestamp(Date date){
        if (null == date) {
            return 0L;
        }
        return getTimestamp(date.getTime());
    }

    public static Long getTimestamp(Long time){
        return Long.valueOf(time / 1000);
    }

    public static Long getTimestamp(){
        return LocalDateTime.now().toEpochSecond((ZoneOffset) getZoneId());
    }

    public static Long getTimestamp(LocalDateTime dateTime){
        return dateTime.toEpochSecond((ZoneOffset) getZoneId());
    }

    public static Long getTimestamp(String datetime){
        if(datetime == null) return null;
        return getTimestamp(formatToDateTime(datetime));
    }

    public static LocalDateTime toLocalDateTime(long timestamp,ZoneId zoneId){
        return Instant.ofEpochSecond(timestamp).atZone(zoneId).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(long timestamp){
        return toLocalDateTime(timestamp,getZoneId());
    }

    public static LocalDateTime toLocalDateTime(Date date){
        return date.toInstant().atZone(getZoneId()).toLocalDateTime();
    }

    public static Date toDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(getZoneId()).toInstant());
    }
}
