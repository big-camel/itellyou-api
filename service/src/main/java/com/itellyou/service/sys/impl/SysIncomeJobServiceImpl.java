package com.itellyou.service.sys.impl;

import com.itellyou.model.statistics.StatisticsIncomeTotalModel;
import com.itellyou.model.sys.EntityAction;
import com.itellyou.model.sys.EntityType;
import com.itellyou.model.user.UserBankType;
import com.itellyou.service.statistics.StatisticsIncomeSingleService;
import com.itellyou.service.user.bank.UserBankService;
import com.itellyou.util.DateUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 定时统计工作类，每月1日为用户结算平台收益
 */
public class SysIncomeJobServiceImpl extends QuartzJobBean {
    //日志
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StatisticsIncomeSingleService incomeSingleService;
    private final UserBankService bankService;

    public SysIncomeJobServiceImpl(StatisticsIncomeSingleService incomeSingleService, UserBankService bankService) {
        this.incomeSingleService = incomeSingleService;
        this.bankService = bankService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {

        //查询1个月以前1号到月底的数据
        LocalDate prevMonthsDate = DateUtils.toLocalDate().minusMonths(1);
        LocalDate firstDay = prevMonthsDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = prevMonthsDate.with(TemporalAdjusters.lastDayOfMonth());

        List<StatisticsIncomeTotalModel> data = incomeSingleService.totalByUser(null,DateUtils.getTimestamp(firstDay),DateUtils.getTimestamp(lastDay),null,null,null,null,null);
        try {
            Map<Long,Double> userAmount = new HashMap<>();
            data.forEach(model -> {
                Long userId = model.getUserId();
                Double amount = model.getSharingAmount();
                if(amount > 0){
                    userAmount.put(userId,amount);
                }
            });
            userAmount.forEach((userId , amount) -> {
                try {
                    bankService.update(amount, UserBankType.CASH,EntityAction.PAYMENT, EntityType.SYSTEM,"",userId,DateUtils.format(firstDay,"yyyy-MM")+"平台收益分成派发",0l);
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }
}
