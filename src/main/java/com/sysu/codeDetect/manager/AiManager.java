package com.sysu.codeDetect.manager;

import com.sysu.codeDetect.common.ErrorCode;
import com.sysu.codeDetect.exception.BusinessException;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionChoice;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;

//通用的AI调用代码
@Service
public class AiManager {

    @Resource
    private ArkService aiService;

    private final String DEFAULT_MODEL = "deepseek-v3-250324";

    //调用AI接口获取响应字符串
    public String doChat(String userPrompt){
        return doChat("",userPrompt, DEFAULT_MODEL);
    }

    //调用AI接口获取响应字符串
    public String doChat(String systemPrompt,String userPrompt,String model){
        // 构建消息列表
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.SYSTEM)
                .content(systemPrompt)
                .build());
        messages.add(ChatMessage.builder()
                .role(ChatMessageRole.USER)
                .content(userPrompt)
                .build());

        // 构建请求对象
        ChatCompletionRequest request = ChatCompletionRequest.builder()
//                .model("deepseek-v3-250324")
                .model(model)
                .messages(messages)
                .build();

        // 调用 API 并输出结果
        List<ChatCompletionChoice> choices = aiService.createChatCompletion(request).getChoices();
        if(CollUtil.isNotEmpty(choices)){
            return (String) choices.get(0).getMessage().getContent();
        }
        throw new BusinessException(ErrorCode.AI_ERROR,"AI调用失败，没有返回结果");
        // 关闭服务
        //aiService.shutdownExecutor();
    }
}
