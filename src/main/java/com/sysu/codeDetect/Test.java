package com.sysu.codeDetect;

import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test {
    static String apiKey = "45e15eac-3cc5-4166-aad8-13651689f532";
    static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    static Dispatcher dispatcher = new Dispatcher();
    static ArkService service = ArkService.builder()
            .dispatcher(dispatcher)
            .connectionPool(connectionPool)
            .baseUrl(baseUrl)
            .apiKey(apiKey)
            .build();

    public static void main(String[] args) {
        // 构建消息列表
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content("你是一名程序员.")
                .build());
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content("帮我生成一道关于Java的面试题？")
                .build());

        // 构建请求对象
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("deepseek-v3-250324")
                .messages(messages)
                .build();

        // 调用 API 并输出结果
        service.createChatCompletion(request)
                .getChoices()
                .forEach(choice ->
                        System.out.println(choice.getMessage().getContent())
                );

        // 关闭服务
        service.shutdownExecutor();
    }
}