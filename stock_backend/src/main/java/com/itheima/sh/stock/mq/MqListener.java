package com.itheima.sh.stock.mq;

import com.itheima.sh.stock.pojo.domain.InnerMarketDomain;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MqListener {
    @Autowired
    private RedisTemplate redisTemplate;
    /*
        监听mq的消息，然后将消息同步到redis中
        1.@RabbitListener(queues = "innerMarketQueue")表示监听mq中队列名是innerMarketQueue的消息，然后将监听到的消息赋值给
        修饰方法的参数
        2.发送消息方数据是什么类型，那么接收方法就是什么类型List<InnerMarketDomain> mqAndRedisInnerMarketDomainList
     */
    @RabbitListener(queues = "innerMarketQueue")
    public void listenMarketInfoFromMq(List<InnerMarketDomain> mqAndRedisInnerMarketDomainList){
        //1.删除之前的消息
        //innerMarketInfos ===>上证:当前价格 3238    时间 14:30
        //innerMarketInfos ===>上证:当前价格 4000    时间 15:00
        redisTemplate.delete("innerMarketInfos");
        //2.将新的消息同步到redis中
        //leftPushAll表示从list类型的左侧添加数据
        redisTemplate.opsForList().leftPushAll("innerMarketInfos", mqAndRedisInnerMarketDomainList);
    }
}
