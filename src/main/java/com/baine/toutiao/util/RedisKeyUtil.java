package com.baine.toutiao.util;

public class RedisKeyUtil {
    private static String SPLIT = ":";              // 分隔符
    private static String BIZ_LIKE = "LIKE";        // 业务: 喜欢
    private static String BIZ_DISLIKE = "DISLIKE";

    public static String getLikeKey(int entityId, int entityType) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDisLikeKey(int entityId, int entityType) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}
