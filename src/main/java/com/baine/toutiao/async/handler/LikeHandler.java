package com.baine.toutiao.async.handler;

import com.baine.toutiao.async.EventHandler;
import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class LikeHandler implements EventHandler {

    @Override
    public void doHandle(EventModel model) {
        System.out.println("Liked");
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
