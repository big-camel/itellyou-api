package com.itellyou.model.event;

import java.util.HashSet;

public class ColumnIndexEvent extends ColumnEvent {

    private HashSet<Long> ids;
    public HashSet<Long> getIds(){return ids;}
    public void setIds(HashSet<Long> ids){
        this.ids = ids;
    }

    public ColumnIndexEvent(Object source, HashSet<Long> ids) {
        super(source);
        this.setIds(ids);
    }
}
