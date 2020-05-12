package com.itellyou.model.common;

import com.itellyou.model.sys.EntityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexQueueModel {
    EntityType type;
    boolean delete;
    Long id;

    public IndexQueueModel(EntityType type,Long id){
        this.setType(type);
        this.setId(id);
    }
}
