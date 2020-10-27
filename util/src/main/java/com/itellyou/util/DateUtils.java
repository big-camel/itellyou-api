package com.itellyou.util;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtils {

    public final static DateTimeFormatter defaultFormat = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd[[' 'HH][:mm][:ss]]")
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
            .toFormatter();

    public static ZoneId getZoneId(int hours){
        return ZoneOffset.ofHours(hours);
    }

    public static ZoneId getZoneId(){
        return ZoneOffset.ofHours(8);
    }

    public static String format(long timestamp,ZoneId zoneId,String format){
        DateTimeFormatter dateTimeFormatter = StringUtils.isNotEmpty(format) ? DateTimeFormatter.ofPattern(format) : defaultFormat;
        LocalDateTime localDateTime = toLocalDateTime(timestamp,zoneId);
        return localDateTime.format(dateTimeFormatter);
    }

    public static String format(long timestamp,String format){
        return format(timestamp,getZoneId(),format);
    }

    public static String format(long timestamp,ZoneId zoneId){
        return format(timestamp,zoneId,null);
    }

    public static LocalDateTime formatToDateTime(String datetime,String format){
        DateTimeFormatter dateTimeFormatter = defaultFormat;
        if(StringUtils.isEmpty(format)){
            if(datetime.indexOf("T") > 0){
                format = "yyyy-MM-dd'T'HH:mm:ss";
                if(datetime.indexOf(".") > 0){
                    format += ".";
                    for(int i = datetime.indexOf(".") + 1;i < datetime.length();i++){
                        String text = datetime.substring(i,i + 1);
                        if(Pattern.matches("[0-9]",text)){
                            format += "S";
                        }else{
                            format += "'" + text + "'";
                        }
                    }
                }
            }

        }
        if(!StringUtils.isEmpty(format)){
            dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        }
        return LocalDateTime.parse(datetime,dateTimeFormatter);
    }

    public static LocalDateTime formatToDateTime(String datetime){
        return formatToDateTime(datetime,null);
    }

    public static String format(long timestamp){
        return format(timestamp, "");
    }

    public static String format(String format){
        return format(getTimestamp(),getZoneId(),format);
    }

    public static String format(LocalDate date,String format){
        return format(getTimestamp(date),format);
    }

    public static String format(LocalDate date){
        return format(date,null);
    }

    public static String format(LocalDateTime date,String format){
        return format(getTimestamp(date),format);
    }

    public static String format(LocalDateTime date){
        return format(date,null);
    }

    public static String format(){
        return format(getTimestamp(),"");
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
        return getTimestamp(dateTime,null);
    }

    public static Long getTimestamp(LocalDateTime dateTime,Long defaultValue){
        if(dateTime == null) return defaultValue;
        return dateTime.toEpochSecond((ZoneOffset) getZoneId());
    }

    public static Long getTimestamp(LocalDate dateTime){
        return dateTime.atStartOfDay().toEpochSecond((ZoneOffset) getZoneId());
    }

    public static Long getTimestamp(String datetime){
        return getTimestamp(datetime,null);
    }

    public static Long getTimestamp(String datetime,String format){
        if(datetime == null) return null;
        return getTimestamp(formatToDateTime(datetime,format));
    }

    public static LocalDateTime toLocalDateTime(long timestamp,ZoneId zoneId){
        return Instant.ofEpochSecond(timestamp).atZone(zoneId).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(long timestamp){
        return toLocalDateTime(timestamp,getZoneId());
    }

    public static LocalDateTime toLocalDateTime(){
        return toLocalDateTime(getTimestamp());
    }

    public static LocalDate toLocalDate(long timestamp,ZoneId zoneId){
        return Instant.ofEpochSecond(timestamp).atZone(zoneId).toLocalDate();
    }

    public static LocalDate toLocalDate(long timestamp){
        return toLocalDate(timestamp,getZoneId());
    }

    public static LocalDate toLocalDate(){
        return toLocalDate(getTimestamp());
    }

    public static LocalDateTime toLocalDateTime(Date date){
        return date.toInstant().atZone(getZoneId()).toLocalDateTime();
    }

    public static LocalDate toLocalDate(Date date){
        return date.toInstant().atZone(getZoneId()).toLocalDate();
    }

    public static Date toDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(getZoneId()).toInstant());
    }

    public static Long minus(LocalDateTime begin,LocalDateTime end){
        return getTimestamp(begin) - getTimestamp(end);
    }

    public static Long minus(LocalDateTime end){
        return getTimestamp() - getTimestamp(end);
    }
}
