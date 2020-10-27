package com.itellyou.model.sys;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class PageModel<T> {
    private boolean isStart;
    private boolean isEnd;
    private Integer offset;
    private Integer limit;
    private Integer total;
    private List<T> data;
    @JSONField(unwrapped = true)
    private Object extend = new HashMap<>();

    public PageModel(boolean isStart,boolean isEnd,Integer offset,Integer limit,Integer total,List<T> data){
        this.isStart = isStart;
        this.isEnd = isEnd;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
        this.data = data;
    }

    public PageModel(boolean isStart,boolean isEnd,Integer offset,Integer limit,Integer total,List<T> data,Object extend){
        this(isStart,isEnd,offset,limit,total,data);
        this.extend = extend;
    }

    public PageModel(Integer offset,Integer limit,Integer total,List<T> data){
        this(offset == null || offset == 0,offset == null || limit == null || offset + limit >= total,offset,limit,total,data);
    }
}
