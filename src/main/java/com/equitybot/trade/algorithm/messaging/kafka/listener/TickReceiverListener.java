package com.equitybot.trade.algorithm.messaging.kafka.listener;

import java.io.IOException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import com.equitybot.trade.algorithm.strategy.ValidateSuperTrend;
import com.equitybot.trade.bo.Tick;
import com.google.gson.Gson;

public class TickReceiverListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ValidateSuperTrend validateSuperTrend;

	
	@KafkaListener(id = "id0", topicPartitions = {
	@TopicPartition(topic = "topic-kite-tick", partitions = { "0" }) })
	public void listenPartition0(ConsumerRecord<?, ?> record) throws IOException {
		Gson gson = new Gson();
		Tick unitData = gson.fromJson(record.value().toString(), Tick.class);
		//logger.info("ID9 received: "+unitData.toString());
	//	processRequest(unitData);
	}

	@KafkaListener(id = "id1", topicPartitions = {
			@TopicPartition(topic = "topic-kite-tick", partitions = { "0" }) })
	public void listenPartition1(ConsumerRecord<?, ?> record) throws IOException {
		Gson gson = new Gson();
		Tick unitData = gson.fromJson(record.value().toString(), Tick.class);
		//logger.info("ID0 received: "+unitData.toString());
		//processRequest(unitData);
	}

	@KafkaListener(id = "id2", topicPartitions = {
			@TopicPartition(topic = "topic-kite-tick", partitions = { "0" }) })
	public void listenPartition2(ConsumerRecord<?, ?> record) throws IOException {
		Gson gson = new Gson();
		Tick unitData = gson.fromJson(record.value().toString(), Tick.class);
		//logger.info("ID0 received: "+unitData.toString());
		//processRequest(unitData);
	}

	/*private void processRequest(Tick unitData) {
		validateSuperTrend.stopLoss(Decimal.valueOf(unitData.getLastTradedPrice()), unitData.getInstrumentToken());

	}*/

}
