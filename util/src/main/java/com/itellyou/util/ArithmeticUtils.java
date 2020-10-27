package com.itellyou.util;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ArithmeticUtils {
    //构造器私有化，让这个类不能实例化
    private ArithmeticUtils(){}
    //提供精确的加法运算
    public static double add(double v1, @NotNull double... values)
    {
        BigDecimal bigDecimalV1 = BigDecimal.valueOf(v1);
        for (double value : values){
            BigDecimal bigDecimal = BigDecimal.valueOf(value);
            bigDecimalV1 = bigDecimalV1.add(bigDecimal);
        }
        return bigDecimalV1.doubleValue();
    }
    //精确的减法运算
    public static double subtract(double v1, @NotNull double... values)
    {
        BigDecimal bigDecimalV1 = BigDecimal.valueOf(v1);
        for (double value : values){
            BigDecimal bigDecimal = BigDecimal.valueOf(value);
            bigDecimalV1 = bigDecimalV1.subtract(bigDecimal);
        }
        return bigDecimalV1.doubleValue();
    }
    //精确的乘法运算
    public static double multiply(double v1, @NotNull double... values)
    {
        BigDecimal bigDecimalV1 = BigDecimal.valueOf(v1);
        for (double value : values){
            BigDecimal bigDecimal = BigDecimal.valueOf(value);
            bigDecimalV1 = bigDecimalV1.multiply(bigDecimal);
        }
        return bigDecimalV1.doubleValue();
    }
    //提供（相对）精确的除法运算，当发生除不尽的情况时
    public static double divide(double v1,int scale, int roundingMode, @NotNull double... values)
    {
        BigDecimal bigDecimalV1 = BigDecimal.valueOf(v1);
        for (double value : values){
            BigDecimal bigDecimal = BigDecimal.valueOf(value);
            bigDecimalV1 = bigDecimalV1.divide(bigDecimal,scale, roundingMode);
        }
        return bigDecimalV1.doubleValue();
    }

    public static double divide(double v1,int scale, @NotNull double... values)
    {
        return divide(v1,scale,BigDecimal.ROUND_HALF_UP,values);
    }

    public static double divide(double v1,double v2,int scale)
    {
        return divide(v1,scale,v2);
    }

    public static double divide(double v1, @NotNull double... values)
    {
        return divide(v1,2,values);
    }
}
