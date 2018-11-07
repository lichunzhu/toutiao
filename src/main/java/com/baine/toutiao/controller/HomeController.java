package com.baine.toutiao.controller;

import com.baine.toutiao.model.EntityType;
import com.baine.toutiao.model.HostHolder;
import com.baine.toutiao.model.News;
import com.baine.toutiao.model.ViewObject;
import com.baine.toutiao.service.LikeService;
import com.baine.toutiao.service.NewsService;
import com.baine.toutiao.service.UserService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId(): 0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if(localUserId != 0) {
                vo.set("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            }
            else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,@RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("pop", pop);
        model.addAttribute("vos", getNews(0, 0, 10));
        return "home";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        model.addAttribute("pop", pop);
        return "home";
    }
}
