package com.itellyou.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class Params {

    final static Logger logger = LoggerFactory.getLogger(Params.class);

    final static String ORDER_FIELD = "order_field";
    final static String ORDER_SORT = "order_sort";
    final static String PAGE_OFFSET = "offset";
    final static String PAGE_LIMIT = "limit";

    private final Map<String,Object> values;

    public Params(Map<String, Object> values) {
        this.values = values;
    }

    public interface Customer {
        <T> T value();
    }

    /**
     * IP
     */
    public static class IPLong implements Customer {
        private Long ip;
        private Object defaultValue;

        public IPLong(Object value,Object defaultValue){
            this.defaultValue = defaultValue;
            this.ip = value != null ? IPUtils.toLong(value.toString()) : null;
        }

        @Override
        public Long value() {
            if(ip == null) return defaultValue != null ? Long.parseLong(defaultValue.toString()) : null;
            return this.ip;
        }
    };

    /**
     * 时间戳
     */
    public static class Timestamp implements Customer {
        private Long timestamp;
        private Object defaultValue;

        public Timestamp(Object value,Object defaultValue,String format){
            this.defaultValue = defaultValue;
            try {
                if(value != null) this.timestamp = DateUtils.getTimestamp(value.toString(), format);
            }catch (Exception e){
                logger.warn(e.getLocalizedMessage());
            }
        }
        @Override
        public Long value() {
            if(timestamp == null) return defaultValue != null ? Long.parseLong(defaultValue.toString()) : null;
            return this.timestamp;
        }
    };

    /**
     * 集合存在则返回值，不存在则返回默认值
     * @param <T>
     */
    public static class InCollection<T> implements Customer {
        private T value;
        private Object defaultValue;

        public InCollection(Collection<T> data,T value,Object defaultValue){
            this.defaultValue = defaultValue;
            if(data != null) this.value = data.contains(value) ? value : null;
        }

        public InCollection(Map<T,T> data,T value,Object defaultValue){
            this.defaultValue = defaultValue;
            if(data != null) this.value = value != null ? data.get(value) : null;
        }

        @Override
        public T value() {
            if(value == null) return defaultValue != null ? (T)defaultValue : null;
            return this.value == null ? null : value;
        }
    };

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param <T>
     * @return
     */
    public <T> InCollection<T> get(String key,Collection<T> data){
        return getOrDefault(key,data,null);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param <T>
     * @return
     */
    public <T> InCollection<T> get(String key,Map<T,T> data){
        return getOrDefault(key,data,null);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param clazz 集合数据类型
     * @param <T>
     * @return
     */
    public <T> InCollection<T> get(String key,Collection<T> data,Class<T> clazz) {
        return getOrDefault(key,data,clazz,null);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param clazz 集合数据类型
     * @param <T>
     * @return
     */
    public <T> InCollection<T> get(String key,Map<T,T> data,Class<T> clazz) {
        return getOrDefault(key,data,clazz,null);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param defaultValue 默认值，未匹配集合中的值则返回默认值
     * @param <T>
     * @return
     */
    public <T> InCollection<T> getOrDefault(String key,Collection<T> data,Object defaultValue) {
        return getOrDefault(key,data,null,defaultValue);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param defaultValue 默认值，未匹配集合中的值则返回默认值
     * @param <T>
     * @return
     */
    public <T> InCollection<T> getOrDefault(String key,Map<T,T> data,Object defaultValue) {
        return getOrDefault(key,data,null,defaultValue);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param clazz 集合数据类型
     * @param defaultValue 默认值，未匹配集合中的值则返回默认值
     * @param <T>
     * @return
     */
    public <T> InCollection<T> getOrDefault(String key,Collection<T> data,Class<T> clazz,Object defaultValue) {
        return getOrDefault(key,InCollection.class,defaultValue,data,clazz);
    }

    /**
     * 从集合中获取值
     * @param key 参数Key
     * @param data 集合数据
     * @param clazz 集合数据类型
     * @param defaultValue 默认值，未匹配集合中的值则返回默认值
     * @param <T>
     * @return
     */
    public <T> InCollection<T> getOrDefault(String key,Map<T,T> data,Class<T> clazz,Object defaultValue) {
        return getOrDefault(key,InCollection.class,defaultValue,data,clazz);
    }

    /**
     * 获取参数字符串值
     * @param key 参数Key
     * @return
     */
    public String get(String key) {
        return getOrDefault(key,String.class,null);
    }

    /**
     * 获取参数字符串值，不存在Key，返回默认值
     * @param key 参数Key
     * @param defaultValue 未匹配默认值
     * @return
     */
    public String getOrDefault(String key,String defaultValue) {
        return getOrDefault(key,String.class,defaultValue);
    }

    /**
     * 获取参数值
     * @param key 参数Key
     * @param clazz 值类型
     * @param <T>
     * @return
     */
    public <T> T get(String key,Class<T> clazz) {
        return getOrDefault(key,clazz,null);
    }

    /**
     * 获取参数值
     * @param key 参数Key
     * @param clazz 值类型
     * @param defaultValue 默认值
     * @param <T>
     * @return
     */
    public <T> T getOrDefault(String key,Class<T> clazz,Object defaultValue) {
        return getOrDefault(key,clazz,defaultValue,null);
    }

    /**
     * 获取参数值
     * @param key 参数Key
     * @param clazz 值类型
     * @param args 其它参数
     * @param <T>
     * @return
     */
    public <T> T get(String key,Class<T> clazz,Object... args){
        return getOrDefault(key,clazz,null,args);
    }

    /**
     * 获取排序参数
     * @param fields 需要过滤的排序字段集合
     * @return
     */
    public Map<String,String> getOrder(String... fields){
        return getOrderDefault(null,"asc",fields);
    }

    /**
     * 获取排序参数
     * @param fields 需要过滤的排序字段集合
     * @return
     */
    public Map<String,String> getOrder(Map<String,String> fields){
        return getOrderDefault(null,"asc",fields);
    }

    /**
     * 获取排序参数，如果不传过滤字段，将返回默认值，不然用户传入的参数会有安全风险
     * @param defaultField 默认排序字段
     * @param defaultSort 默认排序方向
     * @param fields 需要过滤的排序字段集合
     * @return
     */
    public Map<String,String> getOrderDefault(String defaultField,String defaultSort,String... fields){
        String orderField = getOrDefault(ORDER_FIELD, fields != null ? Arrays.asList(fields) : null,defaultField).value();
        if(orderField == null) return null;
        String orderSort = getOrderSort(defaultSort);
        return new HashMap<String,String>(){{put(orderField,orderSort);}};
    }

    /**
     * 获取排序参数
     * @param defaultField 默认排序字段
     * @param defaultSort 默认排序方向
     * @param fields 需要过滤的排序字段集合
     * @return
     */
    public Map<String,String> getOrderDefault(String defaultField,String defaultSort,Map<String,String> fields){
        String orderField = getOrDefault(ORDER_FIELD, fields,defaultField).value();
        if(orderField == null) return null;
        String orderSort = getOrderSort(defaultSort);
        return new HashMap<String,String>(){{put(orderField,orderSort);}};
    }

    /**
     * 获取排序方向参数
     * @param defaultSort 默认方向
     * @param values 方向集合
     * @return
     */
    public String getOrderSort(String defaultSort,String... values){
        return getOrDefault(ORDER_SORT, values != null ? Arrays.asList(values) : null,defaultSort).value();
    }

    /**
     * 获取排序方向参数
     * @param defaultSort 默认方向
     * @param values 方向集合
     * @return
     */
    public String getOrderSort(String defaultSort,Map<String,String> values){
        return getOrDefault(ORDER_SORT, values,defaultSort).value();
    }

    /**
     * 获取排序方向参数
     * @param defaultSort 默认方向
     * @return
     */
    public String getOrderSort(String defaultSort){
        return getOrDefault(ORDER_SORT, new HashMap<String,String>(){{
            put("ascend","asc");
            put("descend","desc");
        }},defaultSort).value();
    }

    public Long getTimestamp(String key,Long defaultValue){
        return getOrDefault(key,Params.Timestamp.class,defaultValue).value();
    }

    public Long getTimestamp(String key){
        return getTimestamp(key,null);
    }

    public Long getIPLong(Long defaultValue){
        return getOrDefault("ip",Params.IPLong.class,defaultValue).value();
    }

    public Long getIPLong(){
        return getIPLong(null);
    }

    public Integer getPageOffset(Integer defaultValue){
        return getInteger(PAGE_OFFSET,defaultValue);
    }

    public Integer getPageOffset(){
        return getInteger(PAGE_OFFSET,null);
    }

    public Integer getPageLimit(Integer defaultValue){
        return getInteger(PAGE_LIMIT,defaultValue);
    }

    public Integer getPageLimit(){
        return getInteger(PAGE_LIMIT,null);
    }

    public Set<String> getInclude(){
        String[] include = getOrDefault("include","").split(",");
        return Arrays.asList(include).stream().collect(Collectors.toSet());
    }

    public Integer getInteger(String key,Integer defaultValue){
        return getOrDefault(key,Integer.class,defaultValue);
    }

    public Integer getInteger(String key){
        return getInteger(key,null);
    }

    public Long getLong(String key,Long defaultValue){
        return getOrDefault(key,Long.class,defaultValue);
    }

    public Long getLong(String key){
        return getLong(key,null);
    }

    public Double getDouble(String key,Double defaultValue){
        return getOrDefault(key,Double.class,defaultValue);
    }

    public Double getDouble(String key){
        return getDouble(key,null);
    }

    public Boolean getBoolean(String key,Boolean defaultValue){
        return getOrDefault(key,Boolean.class,defaultValue);
    }

    public Boolean getBoolean(String key){
        return getOrDefault(key,Boolean.class,null);
    }

    public Short getShort(String key,Short defaultValue){
        return getOrDefault(key,Short.class,defaultValue);
    }

    public Short getShort(String key){
        return getShort(key,null);
    }

    public Float getFloat(String key,Float defaultValue){
        return getOrDefault(key,Float.class,defaultValue);
    }

    public Float getFloat(String key){
        return getFloat(key,null);
    }

    public char getChar(String key,char defaultValue){
        return getOrDefault(key,char.class,defaultValue);
    }

    public char getChar(String key){
        return getChar(key,(char) 0);
    }

    public Byte getByte(String key,Byte defaultValue){
        return getOrDefault(key,Byte.class,defaultValue);
    }

    public Byte getByte(String key){
        return getByte(key,null);
    }

    /**
     * 获取参数值
     * @param key 参数Key
     * @param clazz 值类型
     * @param defaultValue 默认值
     * @param args 其它参数
     * @param <T>
     * @return
     */
    public <T> T getOrDefault(String key,Class<T> clazz,Object defaultValue,Object... args){
        Object value = values.get(key);

        if(clazz.equals(IPLong.class)){
            return (T)new IPLong(value,defaultValue);
        }
        if(clazz.equals(Timestamp.class)){
            return (T)new Timestamp(value,defaultValue,args != null && args.length > 0 ? args[0].toString() : null);
        }
        if(clazz.equals(InCollection.class)){
            if(args != null && args.length > 1 && args[1] != null){
                Class<?> valueType = (Class<?>)args[1];
                value = get(key,valueType);
            }
            if(Map.class.isAssignableFrom(args[0].getClass()))
                return (T)new InCollection<>(args[0] != null ? (Map)args[0] : null, value, defaultValue);
            return (T)new InCollection<>(args[0] != null ? (Collection<? super Object>)args[0] : null, value, defaultValue);
        }
        value = values.getOrDefault(key,defaultValue);
        if(value == null) return null;

        if (clazz.equals(Boolean.class)) {
            return (T)Boolean.valueOf(value.toString());
        }

        if (clazz.equals(Integer.class)) {
            return (T)Integer.valueOf(value.toString());
        }

        if (clazz.equals(char.class)) {
            return clazz.cast(value.toString().charAt(0));
        }

        if (clazz.equals(Short.class)) {
            return (T)Short.valueOf(value.toString());
        }

        if (clazz.equals(Long.class)) {
            return (T)Long.valueOf(value.toString());
        }

        if (clazz.equals(Float.class)) {
            return (T)Float.valueOf(value.toString());
        }

        if (clazz.equals(Double.class)) {
            return (T)Double.valueOf(value.toString());
        }

        if (clazz.equals(Byte.class)) {
            return (T)Byte.valueOf(value.toString());
        }

        if(BaseEnum.class.isAssignableFrom(clazz)){
            BaseEnum[] enumConstants = (BaseEnum[])clazz.getEnumConstants();
            for(BaseEnum e : enumConstants) {
                if(e.toString().equals(value.toString())) {
                    return (T)e;
                }
            }
            return defaultValue == null ? null : (T)defaultValue;
        }

        return clazz.cast(value);
    }
}
