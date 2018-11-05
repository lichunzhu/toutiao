package com.baine.toutiao.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

@Component
public class JedisAdapter implements InitializingBean {

    private JedisPool pool = null;
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");      // 更改key的名字
        print(1, "newhello");
        jedis.setex("hello2", 15, "world"); // 设置带过期时间的key-value

        // 更改数值
        jedis.set("pv", "100");
        jedis.incr("pv");                                // 自增1
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);                    // 自增integer
        print(2, jedis.get("pv"));

        // list
        String listName = "list";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));    // 找到list一定范围内的值
        print(4, jedis.llen(listName));                       // 得到list长度
        print(5, jedis.lpop(listName));                       // 退list队列
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 3));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(10, jedis.lrange(listName, 0, 12));    // 找到list一定范围内的值

        // hash
        String userKey = "user12";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "18618181818");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hkeys(userKey));
        print(16, jedis.hvals(userKey));
        print(17, jedis.hexists(userKey, "phone"));
        print(18, jedis.hexists(userKey, "age"));
        jedis.hsetnx(userKey, "school", "pku");
        jedis.hsetnx(userKey, "name", "yxy");
        print(19, jedis.hgetAll(userKey));

        // set
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
        print(20, jedis.smembers(likeKeys1));
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys2)); // 求交集
        print(23, jedis.sunion(likeKeys1, likeKeys2)); // 求并集
        print(24, jedis.sdiff(likeKeys1, likeKeys2));  // 求不同
        print(25, jedis.sismember(likeKeys1, "5"));
        jedis.srem(likeKeys1, "5");
        print(26, jedis.smembers(likeKeys1));
        print(27, jedis.scard(likeKeys1));             // 集合有多少值
        jedis.smove(likeKeys2, likeKeys1, "14");
        print(28, jedis.scard(likeKeys1));
        print(29, jedis.smembers(likeKeys1));

        // sorted set 优先队列 z开头 主要用于排名
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 80, "Mei");
        jedis.zadd(rankKey, 75, "Lucy");
        print(30, jedis.zcard(rankKey));
        print(31, jedis.zcount(rankKey, 61, 100));  // 分值范围内的count查询
        print(32, jedis.zscore(rankKey, "Lucy"));    // 查Lucy分
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luc");            // zincrby时如果没有会新创建一个
        print(34, jedis.zcount(rankKey, 0, 100));
        print(35, jedis.zrange(rankKey, 1, 3));    // 默认从小到大排序, 下标从0开始
        print(36, jedis.zrevrange(rankKey, 1, 3)); // 反序

        for (Tuple tuple: jedis.zrangeByScoreWithScores(rankKey, "0", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }

        print(38, jedis.zrank(rankKey, "Ben"));      // 从小到大排名, 下标从0开始
        print(39, jedis.zrevrank(rankKey, "Ben"));   // 从大到小排名

        // 多线程 阻塞非阻塞的Redis操作
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            // 默认最大8条, 超出后会阻塞
            // j.close();
        }
    }

    // 初始化完成后初始化pool
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis() {
        return pool.getResource();
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
