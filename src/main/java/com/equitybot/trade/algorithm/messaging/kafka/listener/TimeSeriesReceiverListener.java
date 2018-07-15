package com.equitybot.trade.algorithm.messaging.kafka.listener;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

import com.equitybot.trade.algorithm.strategy.TradingBotOnMovingTimeSeries;

public class TimeSeriesReceiverListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TradingBotOnMovingTimeSeries tradeBot;

    @KafkaListener(id = "id3", topicPartitions = {
    		@TopicPartition(topic = "topic-data-seriesupdate", partitions = { "0" }) })
    public void listenPartition3(ConsumerRecord<?, ?> record) throws IOException {
    	processRequest(record.value().toString());
    }

    @KafkaListener(id = "id4", topicPartitions = {
    		@TopicPartition(topic = "topic-data-seriesupdate", partitions = { "0" }) })
    public void listenPartition4(ConsumerRecord<?, ?> record) throws IOException {
    	processRequest(record.value().toString());
    }

    @KafkaListener(id = "id5", topicPartitions = {
    		@TopicPartition(topic = "topic-data-seriesupdate", partitions = { "0" }) })
    public void listenPartition5(ConsumerRecord<?, ?> record) throws IOException {
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
