package com.equitybot.trade.algorithm.messaging.kafka.listener;

import com.equitybot.trade.algorithm.processor.Processor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import java.io.IOException;
import java.util.UUID;

public class TimeSeriesReceiverListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Processor processor;
/*
    @KafkaListener(topicPartitions = { @TopicPartition(topic = "topic-data-seriesupdate", partitions = {"0"})})
    public void listenPartition3(ConsumerRecord<?, ?> record) throws IOException {
        processRequest(record.value().toString());
    }
*/
    
    @KafkaListener(topicPartitions = {@TopicPartition(topic = "topic-data-seriesupdate", partitions = {"0"})})
    public void listenPartition0(ConsumerRecord<?, ?> record) throws IOException {
    	processRequest(record.value().toString());
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "topic-data-seriesupdate", partitions = {"1"})})
    public void listenPartition1(ConsumerRecord<?, ?> record) throws IOException {
    	processRequest(record.value().toString());
    }

    @KafkaListener(topicPartitions = {@TopicPartition(topic = "topic-data-seriesupdate", partitions = {"2"})})
    public void listenPartition2(ConsumerRecord<?, ?> record) throws IOException {
    	processRequest(record.value().toString());
    }
    
    
    private void processRequest(String seriesName) throws IOException {
        logger.info("Received Series Update for: " + seriesName);
        processor.processTimeSeries(seriesName);
    }

}
