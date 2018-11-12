package com.baine.toutiao.service;

import com.baine.toutiao.dao.KeyDataDAO;
import com.baine.toutiao.util.ToutiaoUtil;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class QiniuService {

    final
    KeyDataDAO keyDataDAO;

    private static final Logger logger = LoggerFactory.getLogger(QiniuService.class);
    // 构造一个带指定Zone对象的配置类
    private Configuration cfg = new Configuration(Zone.zone0());
    // ...其他参数参考类注释
    private UploadManager uploadManager = new UploadManager(cfg);
    // ...生成上传凭证，然后准备上传
    private String accessKey;
    private String secretKey;
    private String QINIU_DOMAIN = "http://phaj7v477.bkt.clouddn.com/";
    private String bucket = "baine";
    private Auth auth;
    private String upToken;

    @Autowired
    public QiniuService(KeyDataDAO keyDataDAO) {
        this.keyDataDAO = keyDataDAO;
        accessKey = keyDataDAO.getKeyData().getQiniuAccessKey();
        secretKey = keyDataDAO.getKeyData().getQiniuSecretKey();
        auth = Auth.create(accessKey, secretKey);
        upToken = auth.uploadToken(bucket);
    }

    public String uploadImage(MultipartFile file) throws IOException{
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!ToutiaoUtil.isFileAllowed(fileExt)) {
                return null;
            }
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            Response response = uploadManager.put(file.getBytes(), fileName, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            logger.info("七牛上传结果key: ", putRet.key);
            System.out.println(putRet.hash);
            logger.info("七牛上传结果hash: ", putRet.hash);
            return QINIU_DOMAIN + putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            logger.error("七牛异常", r.toString());
            try {
                System.err.println(r.bodyString());
                logger.error("七牛异常", r.bodyString());
            } catch (QiniuException ex2) {
                logger.error("七牛异常" + ex2.getMessage());
                return null;
            }
            return null;
        }
    }
}
