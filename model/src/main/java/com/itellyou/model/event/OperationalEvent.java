package com.itellyou.model.event;

import com.itellyou.model.common.OperationalModel;
import org.springframework.context.ApplicationEvent;

public class OperationalEvent extends ApplicationEvent {

    private OperationalModel operationalModel;

    public void setOperationalModel(OperationalModel operationalModel){this.operationalModel=operationalModel;}
    public OperationalModel getOperationalModel(){return operationalModel;}

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public OperationalEvent(Object source, OperationalModel operationalModel) {
        super(source);
        setOperationalModel(operationalModel);
    }

    public OperationalEvent(Object source) {
        super(source);
    }
}
