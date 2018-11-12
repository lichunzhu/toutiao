package com.baine.toutiao.async.handler;

import com.baine.toutiao.async.EventHandler;
import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.model.Message;
import com.baine.toutiao.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LoginExceptionHandler implements EventHandler{

    @Autowired
    MessageService messageService;



    @Override
    public void doHandle(EventModel model) {
        // 判断是否有异常登陆
        Message message = new Message();

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
