package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 消费者
 */
public class ConsumerQuickStart {
    public static void main(String[] args) {
        //1.kafka的配置信息
        Properties prop = new Properties();
        //连接地址
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.200.130:9092");
        //key和value的反序列化器
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //设置消费者组
        prop.put(ConsumerConfig.GROUP_ID_CONFIG, "group1");
        //2.创建消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(prop);
        //3.订阅主题
        consumer.subscribe(Arrays.asList("topic-first"));
        //4.拉取消息
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record.key());
                System.out.println(record.value());
            }
        }
    }
}
