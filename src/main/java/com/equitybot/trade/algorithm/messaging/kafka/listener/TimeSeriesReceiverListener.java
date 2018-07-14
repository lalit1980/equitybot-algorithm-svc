package com.equitybot.trade.algorithm.messaging.kafka.listener;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import com.equitybot.trade.algorithm.strategy.TradingBotOnMovingTimeSeries;

@Service
public class TimeSeriesReceiverListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TradingBotOnMovingTimeSeries tradeBot;

    @KafkaListener(topicPartitions = {
            @TopicPartition(topic = "${spring.kafka.consumer.topic-data-seriesupdate}", partitions = { "0" }) })
    public void listenPartition0(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("1: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    @KafkaListener( topicPartitions = {
            @TopicPartition(topic = "${spring.kafka.consumer.topic-data-seriesupdate}", partitions = { "0" }) })
    public void listenPartition1(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("2: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    @KafkaListener( topicPartitions = {
            @TopicPartition(topic = "${spring.kafka.consumer.topic-data-seriesupdate}", partitions = { "0" }) })
    public void listenPartition2(ConsumerRecord<?, ?> record) throws IOException {
    	logger.info("3: Received Times Series update: "+record.value().toString());
    	processRequest(record.value().toString());
    }

    private void processRequest(String seriesName) throws IOException {
    	try {
			tradeBot.startBot(seriesName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }

}
