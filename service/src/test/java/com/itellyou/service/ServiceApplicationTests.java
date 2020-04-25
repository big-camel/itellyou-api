package com.itellyou.service;

import com.itellyou.service.ali.SmsLogService;
import com.itellyou.service.ali.impl.SmsLogServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

class ServiceApplicationTests {

    @TestConfiguration
    static class prepareCustomServices{
        @Bean
        public SmsLogService getSmsLogServiceImpl() {
            return new SmsLogServiceImpl();
        }
    }

    @Autowired
    private SmsLogService smsLogService;
    @Test
    void contextLoads() {
    }

}
