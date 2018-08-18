package com.equitybot.trade.algorithm.messaging.kafka.publisher;

import com.equitybot.trade.algorithm.model.InstrumentSelectorDTO;
import com.equitybot.trade.algorithm.model.OrderRequestDTO;
import com.equitybot.trade.algorithm.util.Cache;
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

	@Autowired
	private Cache cache;

	public void publishSellOrder(double closePrice, long instrument) {
		boolean startTradeFlag=cache.getStartTrade().get(instrument);
		logger.info("Start Trade Flag in Algo Sell Order: "+startTradeFlag);
		
		if (startTradeFlag) {
			publish(closePrice, instrument, 0, Constants.TRANSACTION_TYPE_SELL, null);
		}
	}

	public void publishBuyOrder(double closePrice, long instrument, int orderQuantity,
			InstrumentSelectorDTO instrumentSelectorDTO) {
		boolean startTradeFlag=cache.getStartTrade().get(instrument);
		logger.info("Start Trade Flag in Algo Buy order: "+startTradeFlag);
		if (startTradeFlag) {
			publish(closePrice, instrument, orderQuantity, Constants.TRANSACTION_TYPE_BUY, instrumentSelectorDTO);
		}
	}

	private void publish(double closePrice, long instrument, int orderQuantity, String orderType,
			InstrumentSelectorDTO instrumentSelectorDTO) {
		OrderRequestDTO orderBo = new OrderRequestDTO();
		orderBo.setPrice(closePrice);
		orderBo.setInstrumentSelectorDTO(instrumentSelectorDTO);
		orderBo.setInstrumentToken(instrument);
		orderBo.setTransactionType(orderType);
		orderBo.setTradingsymbol(cache.getCacheInstrument().get(instrument).getTradingsymbol());
		orderBo.setQuantity(80);
		orderBo.setTag("STR");
		orderBo.setUserId("WU6870");
		String newJson = new Gson().toJson(orderBo);
		ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(orderProcessProducerTopic,
				newJson);
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
