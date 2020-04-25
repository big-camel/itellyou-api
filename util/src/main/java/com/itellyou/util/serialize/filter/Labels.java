package com.itellyou.util.serialize.filter;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Labels {
    public Labels() {
    }

    public static LabelFilter includes(String... labels) {
        Map<Class, String[]> map = new HashMap<>();
        map.put(LabelFilter.Default.class,labels);
        return Labels.includes(map);
    }

    public static LabelFilter includes(Map<Class,String[]> includes) {
        return new Labels.LabelFilter(includes, null);
    }

    public static LabelFilter includes(LabelModel... labelModels) {
        Map<Class, String[]> map = new HashMap<>();
        for (LabelModel labelModel : labelModels){
            map.put(labelModel.getClazz(),labelModel.getLabels());
        }
        return Labels.includes(map);
    }

    public static LabelFilter excludes(String... labels) {
        Map<Class, String[]> map = new HashMap<>();
        map.put(LabelFilter.Default.class,labels);
        return Labels.excludes(map);
    }

    public static LabelFilter excludes(LabelModel... labelModels) {
        Map<Class, String[]> map = new HashMap<>();
        for (LabelModel labelModel : labelModels){
            map.put(labelModel.getClazz(),labelModel.getLabels());
        }
        return Labels.excludes(map);
    }

    public static LabelFilter excludes(Map<Class,String[]> excludes) {
        return new Labels.LabelFilter(null, excludes);
    }

    @Data
    public static class LabelModel {
        private Class clazz;
        private String[] labels;

        public LabelModel(Class clazz,String...labels){
            this.clazz = clazz;
            this.labels = labels;
        }
    }

    public static class LabelFilter implements com.alibaba.fastjson.serializer.LabelFilter {
        private Map<Class,String[]> includes;
        private Map<Class,String[]> excludes;
        private Class clazz;
        private String[] clazzIncludes;
        private String[] clazzExcludes;

        public interface Default {};

        public LabelFilter(Map<Class,String[]> includes, Map<Class,String[]> excludes) {
            if (includes != null) {
                HashMap<Class,String[]> includesMap = new HashMap<>();
                for(Map.Entry<Class, String[]> entry : includes.entrySet()){
                    Class clazz = entry.getKey();
                    String[] labels = entry.getValue();
                    Arrays.sort(labels);
                    includesMap.put(clazz,labels);
                }
                this.includes = includesMap;
            }

            if (excludes != null) {
                HashMap<Class,String[]> excludesMap = new HashMap<>();
                for(Map.Entry<Class, String[]> entry : excludes.entrySet()){
                    Class clazz = entry.getKey();
                    String[] labels = entry.getValue();
                    Arrays.sort(labels);
                    excludesMap.put(clazz,labels);
                }
                this.excludes = excludesMap;
            }
        }

        public boolean apply(String[] labels,Map<Class,String[]> labelMap,boolean includes){
            String[] clazzLabels = null;
            if(clazz != null){
                clazzLabels = labelMap.get(clazz);
                if(clazzLabels != null) {
                    if(Arrays.binarySearch(clazzLabels, "*") >= 0){
                        return includes;
                    }
                    for (String c : labels){
                        if(Arrays.binarySearch(clazzLabels, c) >= 0){
                            setClazz(null);
                            return includes;
                        }
                    }
                }
            }
            String[] defaultLabels = labelMap.get(Default.class);
            if(defaultLabels == null || ((clazzIncludes != null || clazzExcludes != null ) && Arrays.binarySearch(defaultLabels, "*") >= 0)){
                defaultLabels = includes ? clazzIncludes : clazzExcludes;
            }
            if(defaultLabels == null && clazz != null && clazzExcludes == null){
                setClazz(null);
                return true;
            }

            if(defaultLabels != null){
                if(Arrays.binarySearch(defaultLabels, "*") >= 0){
                    setClazz(null);
                    return includes;
                }
                for (String c : labels){
                    if(Arrays.binarySearch(defaultLabels, c) >= 0){
                        setClazz(null);
                        return includes;
                    }
                }
            }
            return !includes;
        }

        @Override
        public boolean apply(String label) {
            String[] labels = StringUtils.split(label,',');
            if (this.excludes != null && this.excludes.size() > 0) {
                return apply(labels,this.excludes,false);
            } else if(this.includes != null && this.includes.size() > 0){
                return apply(labels,this.includes,true);
            }
            return false;
        }

        public void setClazz(Class clazz){
            this.clazz = clazz;
            this.clazzIncludes = null;
            this.clazzExcludes = null;
        }

        public void setClazz(Class clazz,String[] defaultIncludes,String[] defaultExcludes){
            this.clazz = clazz;
            this.clazzIncludes = defaultIncludes;
            this.clazzExcludes = defaultExcludes;
        }
    }
}
