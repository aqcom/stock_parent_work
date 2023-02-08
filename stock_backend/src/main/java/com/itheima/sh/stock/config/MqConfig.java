package com.itheima.sh.stock.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {
    //1.重新定义消息序列化的方式，改为基于json格式序列化和反序列化。默认方式是jdk.传输数据性能较低
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
    //2.定义国内大盘信息队列，队列名innerMarketQueue
    @Bean
    public Queue queue(){
        /*
            1.innerMarketQueue表示大盘消息的队列名
            2.true表示队列一直存在
         */
        return new Queue("innerMarketQueue",true);
    }
    //3.定义路由股票信息的主题交换机TopicExchange，交换机名字是stockExchange
    @Bean
    public TopicExchange topicExchange(){
        /*
            1.stockExchange表示交换机名字
            2.true表示持久存在
            3.false 不自动删除
         */
        return new TopicExchange("stockExchange", true, false);
    }
    //4.绑定队列到指定交换机并设置routingKey是inner.market
    @Bean
    public Binding bindingQueueToExchange(Queue queue,TopicExchange topicExchange){
        //开始绑定
        return BindingBuilder.bind(queue).to(topicExchange).with("inner.market");
    }
}
