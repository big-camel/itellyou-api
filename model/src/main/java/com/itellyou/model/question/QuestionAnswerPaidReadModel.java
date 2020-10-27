package com.itellyou.model.question;

import com.itellyou.util.CacheEntity;
import com.itellyou.model.user.UserBankType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerPaidReadModel implements CacheEntity {
    private Long answerId;
    private UserBankType paidType;
    private Boolean paidToRead;
    private Double paidAmount;
    private Boolean starToRead;
    private Double freeReadScale;

    @Override
    public Long cacheKey() {
        return answerId;
    }
}
