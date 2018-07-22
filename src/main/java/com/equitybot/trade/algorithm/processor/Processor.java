package com.equitybot.trade.algorithm.processor;

import com.equitybot.trade.algorithm.messaging.kafka.publisher.OrderPublisher;
import com.equitybot.trade.algorithm.model.InstrumentSelectorDTO;
import com.equitybot.trade.algorithm.util.Cache;
import com.equitybot.trade.algorithm.indicator.SuperTrendAnalyzer;
import com.equitybot.trade.algorithm.strategy.SuperTrendStrategy;
import com.equitybot.trade.algorithm.util.InstrumentSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.TimeSeries;

@Service
public class Processor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SuperTrendStrategy superTrendStrategy;

    @Autowired
    private OrderPublisher orderPublisher;

    @Autowired
    private Cache cache;

    @Autowired
    private InstrumentSelector instrumentSelector;


    public void processTimeSeries(String seriesName){

        TimeSeries series = cache.getTimeSeries().get(seriesName);
        processTimeSeries(series);
    }

    public void processTimeSeries(TimeSeries timeSeries) {

        if (timeSeries != null && !timeSeries.getBarData().isEmpty()) {
            Bar bar = timeSeries.getBarData().get(timeSeries.getBarData().size() - 1);
            Long instrument = Long.parseLong(timeSeries.getName());
            process(bar, instrument);

        } else {
            logger.info(" * Super Trend evaluate fail, because timeSeries is empty or null for instrument {}",
                    timeSeries == null ? "" : timeSeries.getName());
        }
    }

    public void process(Bar bar, Long instrument){
        SuperTrendAnalyzer superTrendAnalyzer = this.superTrendStrategy.build(bar, instrument);
        publishOrder(superTrendAnalyzer);
    }

    private void publishOrder(SuperTrendAnalyzer superTrendAnalyzer) {

        if (superTrendAnalyzer.getAction() == -1) {
            sell(superTrendAnalyzer);
        } else if (superTrendAnalyzer.getAction() == 1) {
            buy(superTrendAnalyzer);
        } else {
            logger.info("No action indicated");
        }
    }

    private void buy(SuperTrendAnalyzer superTrendAnalyzer) {
        if (!this.cache.getBoughtInstruments().containsKey(superTrendAnalyzer.getSuperTradeIndicator().getInstrument())) {
            InstrumentSelectorDTO instrumentSelectorDTO = this.instrumentSelector.eligibleInstrument(superTrendAnalyzer
                            .getSuperTradeIndicator().getInstrument(), superTrendAnalyzer.getSuperTradeIndicator()
                    .getBar().getClosePrice().doubleValue());
            logger.info(instrumentSelectorDTO.toString());
            if(instrumentSelectorDTO.getInstrumentProfit()>=0) {
                this.orderPublisher.publishBuyOrder(superTrendAnalyzer.getSuperTradeIndicator().getInstrument(),
                        instrumentSelectorDTO.getExpectedQuantity().intValue(), instrumentSelectorDTO);
            }
        }
    }

    private void sell(SuperTrendAnalyzer superTrendAnalyzer) {
        if (this.cache.getBoughtInstruments().containsKey(superTrendAnalyzer.getSuperTradeIndicator().getInstrument())) {
            this.orderPublisher.publishSellOrder(superTrendAnalyzer.getSuperTradeIndicator().getInstrument());
        }
    }

}
