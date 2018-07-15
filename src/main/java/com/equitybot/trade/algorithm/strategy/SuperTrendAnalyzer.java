package com.equitybot.trade.algorithm.strategy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.constants.Constant;
import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.equitybot.trade.algorithm.selector.InstrumentSelector;
import com.equitybot.trade.bo.OrderRequestDTO;
import com.google.gson.Gson;
import com.zerodhatech.kiteconnect.utils.Constants;

public class SuperTrendAnalyzer extends BaseIndicator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${spring.kafka.producer.topic-kite-tradeorder}")
	private String orderProcessProducerTopic;
    
    @Value("${selector.order-quantity}")
	private int orderQuantity;
    
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
    private long instrument;
    private int bandSize;
    private final int smaSize;
    private boolean isInit;
    private List<Decimal> initTrueRangeList;
    private Bar previousBar;
    private Decimal previousFUB;
    private Decimal previousFLB;
    private Decimal previousEMA;

    private boolean havebuy;
    private Bar lastBuyBar;
    private Decimal totalProfitLoss;
    private AlgorithmDataLoger algorithmDataLoger;
    private static Map<Long,Decimal> profitPool = new HashMap<>();
    InstrumentSelector instrumentSelector;
    private IgniteCache<Long, String> cacheTradeOrder;

    @Autowired
    IgniteConfig igniteConfig;
    public SuperTrendAnalyzer(int bandSize, int smaSize, long instrument, AlgorithmDataLoger algorithmDataLoger,
                              InstrumentSelector instrumentSelector) {
        this.bandSize = bandSize;
        this.smaSize = smaSize;
        this.instrument = instrument;
        this.isInit = false;
        this.initTrueRangeList = new LinkedList<>();
        this.previousFUB = Decimal.ZERO;
        this.previousFLB = Decimal.ZERO;
        this.previousEMA = Decimal.ZERO;
        this.havebuy = false;
        this.totalProfitLoss = Decimal.ZERO;
        this.algorithmDataLoger = algorithmDataLoger;
        this.instrumentSelector = instrumentSelector;
        CacheConfiguration<Long, String> ccfgOrderDetails = new CacheConfiguration<Long, String>("CachedTradeOrder");
		this.cacheTradeOrder = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);
    }

    public Decimal analysis(Bar workingBar) {
        if (isInit) {
            calculate(workingBar);
        } else {
            init(workingBar);
        }
        this.previousBar = workingBar;
        return null;
    }

    private Decimal calculate(Bar workingBar) {
        Decimal workingTrueRange = calculateTrueRange(this.previousBar, workingBar);
        Decimal workingEMA = calculateEMA(this.previousEMA, workingTrueRange);
        Decimal workingBUB = calculateBasicUpperBand(workingBar, workingEMA, this.bandSize);
        Decimal workingBLB = calculateBasicLowerBand(workingBar, workingEMA, this.bandSize);
        Decimal workingFUB = calculateFinalUpperBand(workingBUB, this.previousFUB, this.previousBar);
        Decimal workingFLB = calculateFinalLowerBand(workingBLB, this.previousFLB, this.previousBar);
        Decimal workingSuperTrend = calculateSuperTrend(workingBar, workingFUB, workingFLB);
        String buySell = calculateBuySell(workingBar, workingSuperTrend);
        updatePreviousValue(workingFUB, workingFLB, workingEMA);
        analyze(workingBar, workingTrueRange, workingEMA, workingBUB, workingBLB, workingFUB, workingFLB,
                workingSuperTrend, buySell);
        return workingSuperTrend;
    }

    private Decimal init(Bar workingBar) {
        Decimal workingTrueRange;
        Decimal workingSMA;
        Decimal workingBUB;
        Decimal workingBLB;
        Decimal workingFUB;
        Decimal workingFLB;
        Decimal workingSuperTrend = Decimal.ZERO;
        if (this.previousBar != null) {
            workingTrueRange = calculateTrueRange(this.previousBar, workingBar);
            this.initTrueRangeList.add(workingTrueRange);
        }
        if (this.initTrueRangeList.size() == this.smaSize) {
            workingSMA = calculateSMA(this.initTrueRangeList);
            workingBUB = calculateBasicUpperBand(workingBar, workingSMA, this.bandSize);
            workingBLB = calculateBasicLowerBand(workingBar, workingSMA, this.bandSize);
            workingFUB = calculateFinalUpperBand(workingBUB, this.previousFUB, this.previousBar);
            workingFLB = calculateFinalLowerBand(workingBLB, this.previousFLB, this.previousBar);
            workingSuperTrend = calculateSuperTrend(workingBar, workingFUB, workingFLB);
            this.isInit = true;
            updatePreviousValue(workingFUB, workingFLB, workingSMA);
        }
        return workingSuperTrend;
    }

    private void updatePreviousValue(Decimal workingFUB, Decimal workingFLB, Decimal workingEMA) {
        this.previousFLB = workingFLB;
        this.previousFUB = workingFUB;
        this.previousEMA = workingEMA;
    }

    public void analyze(Bar workingBar, Decimal workingTrueRange, Decimal workingEMA, Decimal workingBUB,
                        Decimal workingBLB, Decimal workingFUB, Decimal workingFLB, Decimal workingSuperTrend, String buySell) {

        algorithmDataLoger.logData(workingBar, this.getInstrument(), workingTrueRange, workingEMA, workingBUB,workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell);
        if (buySell != null && this.havebuy && Constant.SELL.equals(buySell)) {
            Decimal currentProfitLoss = workingBar.getClosePrice().minus(this.lastBuyBar.getClosePrice());
            this.totalProfitLoss = this.totalProfitLoss.plus(currentProfitLoss);
            this.algorithmDataLoger.logActionLogData(workingBar.getOpenPrice(),workingBar.getMaxPrice(), workingBar.getMinPrice(),workingBar.getClosePrice(), this.getInstrument(), workingTrueRange, workingEMA,
                    workingBUB, workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell, currentProfitLoss,
                    this.totalProfitLoss,"SuperTrend");
            this.algorithmDataLoger.logProfitLossRepository(this.getInstrument(), this.totalProfitLoss.doubleValue());
            profitPool.put(this.getInstrument(), this.totalProfitLoss);
            instrumentSelector.eligibleInstrument(this.getInstrument(),workingBar.getClosePrice().doubleValue());
            if(this.cacheTradeOrder.containsKey(this.getInstrument())) {
            	OrderRequestDTO orderBo=new OrderRequestDTO();
                orderBo.setInstrumentToken(this.getInstrument());
                orderBo.setTransactionType(Constants.TRANSACTION_TYPE_SELL);
                orderBo.setQuantity(orderQuantity);
                orderBo.setTag("Lalit");
                String newJson = new Gson().toJson(orderBo);
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(orderProcessProducerTopic,newJson);
        		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
        			@Override
        			public void onSuccess(SendResult<String, String> result) {
        				 logger.info("Sent Sell Order: " + result);
        			}

        			@Override
        			public void onFailure(Throwable ex) {
        				logger.info("Failed to send message");
        			}
        		});
            }else {
            	logger.info("Cache Doesn't have any order ID");
            }
            
            this.havebuy = false;

        } else if (buySell != null && !this.havebuy && Constant.BUY.equals(buySell)) {
            this.havebuy = true;
            this.lastBuyBar = workingBar;
            this.algorithmDataLoger.logActionLogData(workingBar.getOpenPrice(),workingBar.getMaxPrice(), workingBar.getMinPrice(),workingBar.getClosePrice(), this.getInstrument(), workingTrueRange, workingEMA,
                    workingBUB, workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell, Decimal.ZERO,
                    Decimal.ZERO,"SuperTrend");
            if(!this.cacheTradeOrder.containsKey(this.getInstrument())) {
            	OrderRequestDTO orderBo=new OrderRequestDTO();
                orderBo.setInstrumentToken(this.getInstrument());
                orderBo.setTransactionType(Constants.TRANSACTION_TYPE_SELL);
                orderBo.setQuantity(orderQuantity);
                orderBo.setTag("Lalit");
                String newJson = new Gson().toJson(orderBo);
                String topicName="topic-kite-tradeorder";
                logger.info( "Buy Order inside Topic Name: "+orderProcessProducerTopic);
                ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName,newJson);
        		future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
        			@Override
        			public void onSuccess(SendResult<String, String> result) {
        				 logger.info("Sent Buy Order: " + result);
        			}

        			@Override
        			public void onFailure(Throwable ex) {
        				logger.info("Failed to send message");
        			}
        		});
            }
        }

    }

    public long getInstrument() {
        return this.instrument;
    }
    
    /*public void stopLoss(Decimal closePrice) {
    	
        if (this.havebuy && this.lastBuyBar.getClosePrice().minus(closePrice)
        		.isGreaterThanOrEqual(this.lastBuyBar.getClosePrice().multipliedBy(5).dividedBy(100))) {
        	 Decimal currentProfitLoss = closePrice.minus(this.lastBuyBar.getClosePrice());
             this.totalProfitLoss = this.totalProfitLoss.plus(currentProfitLoss);
             this.algorithmDataLoger.logActionLogData(Decimal.ZERO,Decimal.ZERO, Decimal.ZERO,closePrice, this.getInstrument(), Decimal.ZERO, Decimal.ZERO,
            		 Decimal.ZERO, Decimal.ZERO, Decimal.ZERO, Decimal.ZERO, Decimal.ZERO, "", Decimal.ZERO,
                     Decimal.ZERO,"stopLoss");
             this.algorithmDataLoger.logProfitLossRepository(this.getInstrument(), this.totalProfitLoss.doubleValue());
             this.havebuy = false;
        }
    	
    }*/

    public static Map<Long, Decimal> getProfitPool() {
        return profitPool;
    }

	public IgniteCache<Long, String> getCacheTradeOrder() {
		return cacheTradeOrder;
	}

	public void setCacheTradeOrder(IgniteCache<Long, String> cacheTradeOrder) {
		this.cacheTradeOrder = cacheTradeOrder;
	}
}
