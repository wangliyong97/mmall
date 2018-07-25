package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;


/**
 * Created by wangliyong on 2018/7/9.
 */
@Slf4j
public class RedisShardedPoolUtil {
    /*
    设置redis有效期
     */

    public static Long expire(String key ,int exTime){ //exTime单位是s
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key,exTime);
        } catch(Exception e){
            log.error("expire key{} exTime{} error",key,exTime,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }

        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String setEx(String key, String value,int exTime){ //exTime单位是s
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key,exTime,value);
        } catch(Exception e){
            log.error("setex key{} exTime{} value{} error",key,exTime,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }

        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String set(String key, String value){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key,value);
        } catch(Exception e){
            log.error("set key{} value{} error",key,value,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }

        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch(Exception e){
            log.error("set key{} error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }

        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long result = null;

        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch(Exception e){
            log.error("set key{} error",key,e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }

        RedisShardedPool.returnResource(jedis);
        return result;
    }


//    public static void main(String[] args) {
//        Jedis jedis = RedisPool.getJedis();
//        RedisPoolUtil.set("key1","value1");
//        String result = RedisPoolUtil.get("key1");
//
//        RedisPoolUtil.setEx("key2","value2",60*10);
//
//        RedisPoolUtil.expire("key2",100);
//
//        RedisPoolUtil.del("key2");
//
//        System.out.println(result);
//
//    }
}
