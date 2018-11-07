package com.baine.toutiao.service;

import com.baine.toutiao.util.JedisAdapter;
import com.baine.toutiao.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;

    /**
     * 如果喜欢返回1, 如果不喜欢返回-1, 否则返回0
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int getLikeStatus(int userId, int entityType, int entityId){
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if (jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1: 0;
    }

    public long like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));

        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(dislikeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }

    // like 和 dislike 返回的都是likeCount因为只关心likeCount
    public long dislike(int userId, int entityType, int entityId) {
        String dislikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(dislikeKey, String.valueOf(userId));

        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));

        return jedisAdapter.scard(likeKey);
    }
}
