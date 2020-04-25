package com.itellyou.util.serialize.filter;

import com.alibaba.fastjson.serializer.NameFilter;

public class UnderScoreNameFilter implements NameFilter {
    @Override
    public String process(Object source, String name, Object value) {
        if (name != null && name.length() != 0) {
            char[] chars = name.toCharArray();
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : chars){
                if(Character.isUpperCase(c)){
                    stringBuilder.append("_");
                    stringBuilder.append(Character.toLowerCase(c));
                }else{
                    stringBuilder.append(c);
                }
            }
            return stringBuilder.toString();
        } else {
            return name;
        }
    }
}
