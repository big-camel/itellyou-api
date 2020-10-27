package com.itellyou.model.event;

import com.itellyou.model.common.OperationalModel;
import org.springframework.context.ApplicationEvent;

import java.util.HashMap;
import java.util.Map;

public class OperationalEvent<T extends OperationalModel> extends ApplicationEvent {

    private T operationalModel;
    private Map<String,Object> args = new HashMap<>();

    public void setOperationalModel(T operationalModel){this.operationalModel=operationalModel;}
    public T getOperationalModel(){return operationalModel;}

    public void setArgs(Map<String,Object> args){this.args = args;}
    public Map<String,Object> getArgs(){return this.args;}

    public OperationalEvent(Object source, T operationalModel,Map<String,Object> args) {
        super(source);
        setOperationalModel(operationalModel);
        setArgs(args);
    }

    public OperationalEvent(Object source, T operationalModel,String key,Object value){
        this(source,operationalModel);
        this.args.put(key, value);
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public OperationalEvent(Object source, T operationalModel) {
        this(source,operationalModel,new HashMap<>());
    }

    public OperationalEvent(Object source) {
        this(source,null);
    }
}
