package com.baine.toutiao.service;

import com.baine.toutiao.dao.NewsDAO;
import com.baine.toutiao.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsService {
    private final NewsDAO newsDAO;

    @Autowired
    public NewsService(NewsDAO newsDAO) {
        this.newsDAO = newsDAO;
    }

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
}
