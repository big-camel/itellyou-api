package com.itellyou.api.handler.response;

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
public class Result {
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

    public Result(Object data){
        this.data = data;
    }

    public Result(Object data,String... includes){
        this(data);
        this.setIncludes(getLabels(includes));
    }

    public Result(Object data,Map<Class,String[]> includes){
        this(data);
        this.includes = includes;
    }

    public Result(Object data, Labels.LabelModel... includes){
        this(data);
        this.setIncludes(getLabels(includes));
    }

    public Result(int status , String message){
        this.status = status;
        this.result = this.status == 200;
        this.message = message;
    }

    public Result(String message,Object data){
        this(200,message,data);
    }

    public Result(String message,Object data,String... includes){
        this(message,data);
        this.setIncludes(getLabels(includes));
    }

    public Result(String message,Object data,Map<Class,String[]> includes){
        this(message,data);
        this.includes = includes;
    }

    public Result(String message,Object data, Labels.LabelModel... includes){
        this(message,data);
        this.setIncludes(getLabels(includes));
    }

    public Result(int status,String message,Object data){
        this(status,message);
        this.data = data;
    }

    public Result(int status,String message,Object data,String... includes){
        this(status,message,data);
        this.setIncludes(getLabels(includes));
    }

    public Result(int status,String message,Object data,Map<Class,String[]> includes){
        this(status,message,data);
        this.setIncludes(includes);
    }

    public Result(int status,String message,Object data, Labels.LabelModel... includes){
        this(status,message,data);
        this.setIncludes(getLabels(includes));
    }

    public Result excludes(String... labels){
        this.setExcludes(getLabels(labels));
        return this;
    }

    public Result excludes(Map<Class,String[]> labels){
        this.setExcludes(labels);
        return this;
    }

    public Result excludes(Labels.LabelModel... labels){
        this.setExcludes(getLabels(labels));
        return this;
    }

    public Result includes(String... labels){
        this.setIncludes(getLabels(labels));
        return this;
    }

    public Result includes(Map<Class,String[]> labels){
        this.setIncludes(labels);
        return this;
    }

    public Result includes(Labels.LabelModel... labels){
        this.setIncludes(getLabels(labels));
        return this;
    }

    public Result extend(String key,Object data){
        this.getExtend().put(key,data);
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

    public ResultJson toResultJson(){
        Map<Class,String[]> includes = this.getIncludes();
        List<SerializeFilter> listSerializeFilter = new ArrayList<>();
        if(includes != null && includes.size() > 0){
            listSerializeFilter.add(Labels.includes(includes));
        }
        Map<Class,String[]> excludes = this.getExcludes();
        if(excludes != null && excludes.size() > 0){
            listSerializeFilter.add(Labels.excludes(excludes));
        }
        if(listSerializeFilter.size() == 0){
            //添加一个过滤器，执行 @JSONDefault
            listSerializeFilter.add(Labels.includes("*"));
        }
        if(listSerializeFilter.size() > 0){
            listSerializeFilter.add(new LabelFilterHandler());
        }
        SerializeFilter[] filters = new SerializeFilter[listSerializeFilter.size()];
        listSerializeFilter.toArray(filters);
        String jsonData = JSON.toJSONString(this.getData(), filters, SerializerFeatureConfig.getDefault());

        if(this.getExtend().size() > 0){
            JSONObject jsonObject = JSON.parseObject(jsonData);
            if(jsonObject == null) jsonObject = new JSONObject();
            jsonObject.putAll(this.getExtend());
            jsonData = jsonObject.toJSONString();
        }

        return new ResultJson(this.getStatus(),this.getMessage(),jsonData);
    }
}
