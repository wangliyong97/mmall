package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by wangliyong on 2018/7/26.
 */
@Slf4j
@Component
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    //@Scheduled(cron = "0*/1***?")//每一分钟执行一次
    public void closeOrdertaskV1(){
        log.info("关闭订单定时任务开始");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

    @Scheduled(cron = "0*/1***?")//每一分钟执行一次
    public void closeOrdertaskV2(){
        log.info("关闭订单定时任务开始");
        long lockTimeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeOut));
        if(setnxResult != null && setnxResult.intValue()==1){
            //如果返回值为1则代表分布式锁获取成功
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获得分布式锁{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName){
        //将锁设置有效期50秒  防止死锁
        RedisShardedPoolUtil.expire(lockName,50);
        log.info("获取{},ThredName{}",lockName,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(lockName);
        log.info("释放{},ThredName{}",lockName,Thread.currentThread().getName());
    }

}
