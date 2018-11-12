package com.baine.toutiao.controller;

import com.baine.toutiao.async.EventModel;
import com.baine.toutiao.async.EventProducer;
import com.baine.toutiao.async.EventType;
import com.baine.toutiao.model.EntityType;
import com.baine.toutiao.model.HostHolder;
import com.baine.toutiao.model.News;
import com.baine.toutiao.service.LikeService;
import com.baine.toutiao.service.NewsService;
import com.baine.toutiao.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LikeController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    LikeService likeService;

    @Autowired
    NewsService newsService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/like"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String like(Model model, @RequestParam("newsId") int newsId) {
        if (hostHolder.getUser() == null) {
            return ToutiaoUtil.getJSONString(1, "用户尚未登录");
        }
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.like(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);

        News news = newsService.getById(newsId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE)
                                .setActorID(hostHolder.getUser().getId()).setEntityId(newsId)
                                .setEntityType(EntityType.ENTITY_NEWS).setEntityOwnerId(news.getUserId()));

        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String dislike(Model model, @RequestParam("newsId") int newsId) {
        if (hostHolder.getUser() == null) {
            return ToutiaoUtil.getJSONString(1, "用户尚未登录");
        }
        int userId = hostHolder.getUser().getId();
        long likeCount = likeService.dislike(userId, EntityType.ENTITY_NEWS, newsId);
        newsService.updateLikeCount(newsId, (int) likeCount);
        return ToutiaoUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
