package com.itellyou.model.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.itellyou.util.serialize.config.SerializerFeatureConfig;
import com.itellyou.util.serialize.filter.LabelFilterHandler;
import com.itellyou.util.serialize.filter.Labels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultModel {
    private Boolean result=true;
    private Integer status=200;
    private String message="Successful";
    private Object data;
    @JSONField(serialize = false)
    private Map<String,Object> extend = new HashMap<>();
    @JSONField(serialize = false)
    private Map<Class,String[]> includes;

    @JSONField(serialize = false)
    private Map<Class,String[]> excludes;

    @JSONField(serialize = false)
    private List<SerializeFilter> filters = new ArrayList<>();

    public ResultModel(Object data){
        this.data = data;
    }

    public ResultModel(Object data, String... includes){
        this(data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel(Object data, Map<Class,String[]> includes){
        this(data);
        this.includes = includes;
    }

    public ResultModel(Object data, Labels.LabelModel... includes){
        this(data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel(int status , String message){
        this.status = status;
        this.result = this.status == 200;
        this.message = message;
    }

    public ResultModel(String message, Object data){
        this(200,message,data);
    }

    public ResultModel(String message, Object data, String... includes){
        this(message,data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel(String message, Object data, Map<Class,String[]> includes){
        this(message,data);
        this.includes = includes;
    }

    public ResultModel(String message, Object data, Labels.LabelModel... includes){
        this(message,data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel(int status, String message, Object data){
        this(status,message);
        this.data = data;
    }

    public ResultModel(int status, String message, Object data, String... includes){
        this(status,message,data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel(int status, String message, Object data, Map<Class,String[]> includes){
        this(status,message,data);
        this.setIncludes(includes);
    }

    public ResultModel(int status, String message, Object data, Labels.LabelModel... includes){
        this(status,message,data);
        this.setIncludes(getLabels(includes));
    }

    public ResultModel excludes(String... labels){
        this.setExcludes(getLabels(labels));
        return this;
    }

    public ResultModel excludes(Map<Class,String[]> labels){
        this.setExcludes(labels);
        return this;
    }

    public ResultModel excludes(Labels.LabelModel... labels){
        this.setExcludes(getLabels(labels));
        return this;
    }

    public ResultModel includes(String... labels){
        this.setIncludes(getLabels(labels));
        return this;
    }

    public ResultModel includes(Map<Class,String[]> labels){
        this.setIncludes(labels);
        return this;
    }

    public ResultModel includes(Labels.LabelModel... labels){
        this.setIncludes(getLabels(labels));
        return this;
    }

    public ResultModel extend(String key, Object data){
        this.getExtend().put(key,data);
        return this;
    }

    public ResultModel addFilters(SerializeFilter... filters){
        for (SerializeFilter filter : filters){
            this.filters.add(filter);
        }
        return this;
    }

    public Map<Class,String[]> getLabels(String... includes){
        Map<Class,String[]> mapLabels = new HashMap<>();
        mapLabels.put(Labels.LabelFilter.Default.class,includes);
        return mapLabels;
    }

    public Map<Class,String[]> getLabels(Labels.LabelModel... includes){
        Map<Class,String[]> mapLabels = new HashMap<>();
        for(Labels.LabelModel labelModel : includes){
            mapLabels.put(labelModel.getClazz(),labelModel.getLabels());
        }
        return mapLabels;
    }

    public ResultModelJson toResultJson(){
        Map<Class,String[]> includes = this.getIncludes();

        if(includes != null && includes.size() > 0){
            addFilters(Labels.includes(includes));
        }
        Map<Class,String[]> excludes = this.getExcludes();
        if(excludes != null && excludes.size() > 0){
            addFilters(Labels.excludes(excludes));
        }
        if(filters.size() == 0){
            //添加一个过滤器，执行 @JSONDefault
            addFilters(Labels.includes("*"));
        }
        addFilters(new LabelFilterHandler());

        String jsonData = JSON.toJSONString(this.getData(), filters.toArray(new SerializeFilter[filters.size()]), SerializerFeatureConfig.getDefault());

        if(this.getExtend().size() > 0){
            JSONObject jsonObject = JSON.parseObject(jsonData);
            if(jsonObject == null) jsonObject = new JSONObject();
            for (Map.Entry<String,Object> entry : this.getExtend().entrySet()){
                String json = JSON.toJSONString(entry.getValue(), filters.toArray(new SerializeFilter[filters.size()]), SerializerFeatureConfig.getDefault());
                jsonObject.put(entry.getKey(),JSON.parse(json));
            }
            jsonData = jsonObject.toJSONString();
        }

        return new ResultModelJson(this.getStatus(),this.getMessage(),jsonData);
    }

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }
}
