package com.sysu.codeDetect.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    String systemPrompt = "你是一个腾讯的面试官";
    String userPrompt = "请生成一道经典的Java面试题";
    String model = "deepseek-v3-250324";

    @Test
    void doChat() {
        String re = aiManager.doChat(userPrompt);
        System.out.println(re);
    }

    @Test
    void testDoChat() {
        String re = aiManager.doChat(systemPrompt,userPrompt,model);
        System.out.println(re);
    }
}