package com.itellyou.service.sys.impl;

import com.itellyou.dao.sys.SysIncomeDao;
import com.itellyou.model.sys.SysIncomeModel;
import com.itellyou.model.sys.SysIncomeRelatedModel;
import com.itellyou.service.sys.SysIncomeRelatedService;
import com.itellyou.service.sys.SysIncomeService;
import com.itellyou.util.ArithmeticUtils;
import com.itellyou.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class SysIncomeServiceImpl implements SysIncomeService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SysIncomeDao incomeDao;
    private final SysIncomeRelatedService relatedService;

    public SysIncomeServiceImpl(SysIncomeDao incomeDao, SysIncomeRelatedService relatedService) {
        this.incomeDao = incomeDao;
        this.relatedService = relatedService;
    }

    @Override
    public int insertOrUpdate(SysIncomeModel model) {
        return incomeDao.insertOrUpdate(model);
    }

    @Override
    @Transactional
    public boolean add(LocalDate date, Map<Long,Double> value, Long userId, Long ip) {
        try{
            if(value == null || value.size() == 0) throw new Exception("没有可以添加的配置");
            AtomicReference<Double> amountTotal = new AtomicReference<>(0.00);
            List<SysIncomeRelatedModel> relatedModels = new LinkedList<>();
            value.forEach((id,amount) -> {
                amountTotal.set(ArithmeticUtils.add(amountTotal.get(), amount));
                relatedModels.add(new SysIncomeRelatedModel(null,null,id,amount,DateUtils.toLocalDateTime(),userId,ip));
            });

            SysIncomeModel incomeModel = new SysIncomeModel();
            incomeModel.setDate(date);
            incomeModel.setAmount(amountTotal.get());
            incomeModel.setCreatedIp(ip);
            incomeModel.setCreatedUserId(userId);
            incomeModel.setCreatedTime(DateUtils.toLocalDateTime());
            int result = insertOrUpdate(incomeModel);
            if(result < 1) throw new Exception("添加失败");
            SysIncomeRelatedModel[] relatedArray = relatedModels.stream().map(relatedModel -> {
                relatedModel.setIncomeId(incomeModel.getId());
                return relatedModel;
            }).toArray(SysIncomeRelatedModel[]::new);
            result = relatedService.insertModels(relatedArray);
            if(result < relatedArray.length) throw new Exception("添加失败");
            return true;
        }catch (Exception e){
            logger.error(e.getLocalizedMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
}
