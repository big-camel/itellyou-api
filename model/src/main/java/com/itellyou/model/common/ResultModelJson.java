package com.itellyou.model.common;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResultModelJson extends ResultModel {

    @JSONField(jsonDirect = true)
    private String data;

    @Override
    public String getData(){
        return this.data;
    }

    public void setData(String data){
        this.data = data;
    }

    public ResultModelJson(String data){
        super(data);
        this.data = data;
    }

    public ResultModelJson(String message, String data){
        super(message,data);
        this.data = data;
    }

    public ResultModelJson(int status, String message, String data){
        super(status,message,null);
        this.data = data;
    }
}
