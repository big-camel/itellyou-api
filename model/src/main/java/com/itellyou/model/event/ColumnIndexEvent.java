package com.itellyou.model.event;

import java.util.Collection;

public class ColumnIndexEvent extends ColumnEvent {

    private Collection<Long> ids;
    public Collection<Long> getIds(){return ids;}
    public void setIds(Collection<Long> ids){
        this.ids = ids;
    }

    public ColumnIndexEvent(Object source, Collection<Long> ids) {
        super(source);
        this.setIds(ids);
    }
}
