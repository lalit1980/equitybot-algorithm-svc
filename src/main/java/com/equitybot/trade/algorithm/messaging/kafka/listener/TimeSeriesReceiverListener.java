package com.equitybot.trade.algorithm.messaging.kafka.listener;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import com.equitybot.trade.algorithm.sample.Test;


public class TimeSeriesReceiverListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private Test test;
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @KafkaListener(id = "id0", topicPartitions = {@TopicPartition(topic = "notify-algo-seriesupdate-topic", partitions = {"0"})})
    public void listenPartition0(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("1: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    @KafkaListener(id = "id1", topicPartitions = {@TopicPartition(topic = "notify-algo-seriesupdate-topic", partitions = {"1"})})
    public void listenPartition1(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("2: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    @KafkaListener(id = "id2", topicPartitions = {@TopicPartition(topic = "notify-algo-seriesupdate-topic", partitions = {"2"})})
    public void listenPartition2(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("3: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    private void processRequest(String seriesName) throws IOException {
    	test.runStartegy(seriesName, 5);
    }

}
