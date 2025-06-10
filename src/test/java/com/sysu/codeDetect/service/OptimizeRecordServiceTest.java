package com.sysu.codeDetect.service;
import java.util.Date;

import com.sysu.codeDetect.model.domain.OptimizeRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class OptimizeRecordServiceTest {

    @Resource
    private OptimizeRecordService optimizeRecordService;

    @Test
    void testAddOptimizeRecord() {
        OptimizeRecord optimizeRecord = new OptimizeRecord();
        optimizeRecord.setId(1);
        optimizeRecord.setUserAccount("cyw");
        optimizeRecord.setOriginalCodeFileUrl("aaaa");
        optimizeRecord.setOptimizedCodeFileUrl("bbb");
        optimizeRecord.setReportUrl("ccc");
        optimizeRecord.setCreateTime(new Date());
        optimizeRecord.setUpdateTime(new Date());
        boolean result = optimizeRecordService.save(optimizeRecord);
        System.out.println(optimizeRecord.getId());
        Assertions.assertTrue(result);
    }
}