package com.baine.toutiao.service;

import com.baine.toutiao.dao.NewsDAO;
import com.baine.toutiao.model.News;
import com.baine.toutiao.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService {
    private final NewsDAO newsDAO;

    @Autowired
    public NewsService(NewsDAO newsDAO) {
        this.newsDAO = newsDAO;
    }

    @Autowired
    private QiniuService qiniuService;

    public int addNews(News news) {
        return newsDAO.addNews(news);
    }

    public News getById(int newsId) {
        return newsDAO.selectByNewsId(newsId);
    }

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public String saveImageLocal(MultipartFile file) throws IOException {
        // xxx._=someName.jpg
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                   StandardCopyOption.REPLACE_EXISTING);
        // xxx.???
        return ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public String saveImageOnline(MultipartFile file) throws IOException {
        // xxx._=someName.jpg
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }

        return qiniuService.uploadImage(file);
    }

    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }
}
