package com.itellyou.api.handler.response;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ResultJson extends Result {

    @JSONField(jsonDirect = true)
    private String data;

    @Override
    public String getData(){
        return this.data;
    }

    public void setData(String data){
        this.data = data;
    }

    public ResultJson(String data){
        super(data);
        this.data = data;
    }

    public ResultJson(String message,String data){
        super(message,data);
        this.data = data;
    }

    public ResultJson(int status,String message,String data){
        super(status,message,null);
        this.data = data;
    }
}
