package com.itellyou.service.common;

import com.itellyou.model.common.DataUpdateQueueModel;
import com.itellyou.model.common.DataUpdateStepModel;

import java.util.function.BiConsumer;

public interface DataUpdateManageService {

    <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model);

    <T extends DataUpdateStepModel> void put(DataUpdateQueueModel<T> model, BiConsumer<T,T> cumulative);

    <T extends DataUpdateStepModel> void cumulative(T stepModel,T model);
}
