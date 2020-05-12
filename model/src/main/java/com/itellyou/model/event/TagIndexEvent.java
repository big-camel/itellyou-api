package com.itellyou.model.event;

import java.util.HashSet;

public class TagIndexEvent extends TagEvent {

    private HashSet<Long> ids;
    public HashSet<Long> getIds(){return ids;}
    public void setIds(HashSet<Long> ids){
        this.ids = ids;
    }

    public TagIndexEvent(Object source, HashSet<Long> ids) {
        super(source);
        this.setIds(ids);
    }
}
