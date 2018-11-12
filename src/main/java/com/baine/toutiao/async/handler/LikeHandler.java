package com.baine.toutiao.async.handler;

import com.baine.toutiao.async.EventHandler;
import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.model.Message;
import com.baine.toutiao.model.User;
import com.baine.toutiao.service.MessageService;
import com.baine.toutiao.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
        // 点赞后给被点赞的人消息提醒
        Message message = new Message();
        // fromId 系统, toId 新闻所有的用户
        int fromId = 3, toId = model.getEntityOwnerId();
        message.setFromId(fromId);
        message.setToId(toId);
        message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId): String.format("%d_%d", toId, fromId));
        User user = userService.getUser(model.getActorID());
        message.setContent("用户" + user.getName() + "赞了你的资讯, http://127.0.0.1:8080/news/" + model.getEntityId());
        message.setCreatedDate(new Date());
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
