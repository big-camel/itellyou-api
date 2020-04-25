package com.itellyou.util.serialize.filter;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.LabelFilter;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.itellyou.util.annotation.JSONDefault;

import java.lang.annotation.Annotation;
import java.util.List;

public class LabelFilterHandler implements PropertyPreFilter {
    @Override
    public boolean apply(JSONSerializer jsonSerializer, Object object, String name) {
        List<LabelFilter> list = jsonSerializer.getLabelFilters();
        Class clazz = object.getClass();
        if(clazz != null){
            String[] excludes = null;
            String[] includes = null;
            Annotation annotation = clazz.getAnnotation(JSONDefault.class);
            if(annotation != null){
                JSONDefault jsonDefault = (JSONDefault)annotation;
                includes = jsonDefault.includes();
                excludes = jsonDefault.excludes();
            }
            for (LabelFilter labelFilter : list){
                if(labelFilter instanceof Labels.LabelFilter){
                    ((Labels.LabelFilter) labelFilter).setClazz(clazz,includes,excludes);
                }
            }
        }
        return true;
    }
}
