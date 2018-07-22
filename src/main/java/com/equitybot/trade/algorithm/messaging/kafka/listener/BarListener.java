package com.equitybot.trade.algorithm.messaging.kafka.listener;

import com.equitybot.trade.algorithm.model.InstrumentBarDTO;
import com.equitybot.trade.algorithm.processor.Processor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;

import java.io.IOException;

@Component
public class BarListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Processor processor;

    @KafkaListener(topicPartitions = { @TopicPartition(topic = "topic-kite-bar", partitions = {"0"})})
    public void listenPartition3(ConsumerRecord<?, ?> record) throws IOException {
        processRequest(record.value().toString());
    }

    private void processRequest(String barString) throws IOException {
        logger.info("Received bar {}: ", barString);
        ObjectMapper mapper = new ObjectMapper();
        Bar instrumentBarDTO = mapper.readValue(barString, Bar.class);
        processor.process(instrumentBarDTO, 111L);
    }
}
