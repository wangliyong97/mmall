package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliyong on 2018/7/12.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;  //jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.tolal","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10")); //在redispool中最大的处于idle状态（空闲的）的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2")); //在redispool中最小的idle状态（空闲的）jedis实例的个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true")); //在borrow一个jedis实例时，是否要进行验证操作，如果赋值为true则得到的jedis实例一定是可以用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true")); //在return一个jedis实例时，是否要进行验证操作，如果赋值为true则放回jedispool的edis实例是可以用的

    private static String ip1 = PropertiesUtil.getProperty("redis.ip1");
    private static Integer port1 = Integer.parseInt(PropertiesUtil.getProperty("redis.port1","6379"));

    private static String ip2 = PropertiesUtil.getProperty("redis.ip2");
    private static Integer port2 = Integer.parseInt(PropertiesUtil.getProperty("redis.port2","6380"));

    private static void initPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);

        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        jedisPoolConfig.setBlockWhenExhausted(true);  //当连接耗尽时，是否阻塞，false则会抛出异常，true则阻塞一直到超出

        JedisShardInfo jedisShardInfo1 = new JedisShardInfo(ip1,port1);
        JedisShardInfo jedisShardInfo2 = new JedisShardInfo(ip2,port2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>();
        jedisShardInfoList.add(jedisShardInfo1);
        jedisShardInfoList.add(jedisShardInfo2);

        pool = new ShardedJedisPool(jedisPoolConfig,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for(int i=0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
        //销毁连接池中所有连接
//        pool.destroy();
        System.out.println("program is end!");
    }
}
