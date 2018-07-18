package com.equitybot.trade.algorithm.messaging.kafka.publisher;

import com.equitybot.trade.algorithm.model.InstrumentSelectorDTO;
import com.equitybot.trade.algorithm.model.OrderRequestDTO;
import com.google.gson.Gson;
import com.zerodhatech.kiteconnect.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrderPublisher {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topic-kite-tradeorder}")
    private String orderProcessProducerTopic;
    
    @Value("${supertrend.userid}")
    private String userid;

    public void publishSellOrder(long instrument) {
        publish(instrument, 0, Constants.TRANSACTION_TYPE_SELL, null);
    }

    public void publishBuyOrder(long instrument, int orderQuantity, InstrumentSelectorDTO instrumentSelectorDTO) {
        publish(instrument, orderQuantity, Constants.TRANSACTION_TYPE_BUY, instrumentSelectorDTO);
    }

    private void publish(long instrument, int orderQuantity, String orderType, InstrumentSelectorDTO instrumentSelectorDTO) {
        OrderRequestDTO orderBo = new OrderRequestDTO();
        orderBo.setInstrumentSelectorDTO(instrumentSelectorDTO);
        orderBo.setInstrumentToken(instrument);
        orderBo.setTransactionType(orderType);
        orderBo.setQuantity(80);
        orderBo.setTag("Lalit");
        orderBo.setUserId(userid);
        String newJson = new Gson().toJson(orderBo);
        ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(orderProcessProducerTopic, newJson);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onSuccess(SendResult<String, String> result) {
                logger.info("Sent Sell Order: {}", result);
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.info("Failed to send message");
            }
        });
    }

}
