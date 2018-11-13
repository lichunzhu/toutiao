package com.baine.toutiao.async.handler;

import com.baine.toutiao.async.EventHandler;
import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RegisterHandler implements EventHandler {
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        map.put("ticket", model.getExt("ticket"));
        map.put("domain", model.getExt("domain"));
        mailSender.sendWithHTMLTemplate(model.getExt("username"), "账号激活验证", "regCheck.ftl", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.REGISTER);
    }
}
