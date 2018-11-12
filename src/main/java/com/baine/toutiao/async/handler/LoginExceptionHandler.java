package com.baine.toutiao.async.handler;

import com.baine.toutiao.async.EventHandler;
import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.model.Message;
import com.baine.toutiao.service.MessageService;
import com.baine.toutiao.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LoginExceptionHandler implements EventHandler{

    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        // 判断是否有异常登陆
        Message message = new Message();
        // 同LikeHandler
        int fromId = 3, toId = model.getActorID();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setConversationId(messageService.getConversationId(fromId, toId));
        message.setContent("上次登录的IP异常");
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
        // 发现登录异常时给相应账户发邮件, 发邮件功能已经实现
//        Map<String, Object> map = new HashMap<>();
//        map.put("username", model.getExt("username"));
//        map.put("errorDate", new Date());
//        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登录异常", "ipError.ftl", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
