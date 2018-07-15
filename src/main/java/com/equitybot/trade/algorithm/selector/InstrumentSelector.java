package com.equitybot.trade.algorithm.selector;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.equitybot.trade.algorithm.strategy.SuperTrendAnalyzer;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.Tick;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ta4j.core.Decimal;

import java.util.ArrayList;

@Component
public class InstrumentSelector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private IgniteCache<Long, Tick> cacheLatestTick;
    @Value("${selector.max-instrument-price-per-stock}")
    private double maxInstrumentPricePerStockLimit;
    @Value("${selector.max-total-price}")
    private double maxTotalPriceLimit;
    @Autowired
	IgniteConfig igniteConfig;
    public InstrumentSelector() {
        CacheConfiguration<Long, Tick> ccfgLatestTickParams = new CacheConfiguration<>("CachedLatestTick");
        this.cacheLatestTick = igniteConfig.getInstance().getOrCreateCache(ccfgLatestTickParams);
    }

    public boolean eligibleInstrument(Long instrument, double expectedPrice){

        return (profitable(instrument) && underPriceRange(instrument) &&  feasibleSellerAvailability(instrument, expectedPrice) ) ;
    }

    private boolean profitable(Long instrument){
        Decimal instrumentProfit = SuperTrendAnalyzer.getProfitPool().get( instrument);
        if(instrumentProfit != null ){
            logger.debug(" instrument ID {} : instrumentProfit {} is profitable {}",instrument,instrumentProfit,instrumentProfit.isGreaterThan(Decimal.ZERO));
            return instrumentProfit.isGreaterThan(Decimal.ZERO);
        }else{
            logger.debug("Not yet traded or analyzed");
        }
        return false;
    }

    private boolean underPriceRange(Long instrument){
        Tick instrumentTick = this.cacheLatestTick.get(instrument);
        if(instrumentTick != null) {
            double instrumentPrice = instrumentTick.getClosePrice();
            logger.debug(" instrument ID {} : Close Price is {} : maxPriceLimit is {}",instrument,instrumentPrice, this.maxInstrumentPricePerStockLimit);
            return instrumentPrice <= maxInstrumentPricePerStockLimit;
        }else{
            logger.debug(" instrument ID {} : not available in cacheLatestTick",instrument);
        }
        return false;
    }

    private boolean feasibleSellerAvailability(Long instrument, double expectedPrice){
        Tick instrumentTick = this.cacheLatestTick.get(instrument);

        if(instrumentTick != null && instrumentTick.getMarketDepth() != null && instrumentTick.getMarketDepth().get("sell") != null  &&
                !instrumentTick.getMarketDepth().get("sell").isEmpty()) {
            double instrumentPrice = instrumentTick.getClosePrice();

            Long quantity = Math.round(maxTotalPriceLimit/instrumentPrice);
            Long requiredQuantity = quantity;

            ArrayList<Depth> instrumentDepth = instrumentTick.getMarketDepth().get("sell");
            double totalStockSize = 0;
            double totalPrice=0;

            for ( Depth depth : instrumentDepth ) {
                totalStockSize += depth.getQuantity();
                if(quantity >= 0) {
                    if (quantity > depth.getQuantity()) {
                        quantity = quantity - depth.getQuantity();
                        totalPrice += depth.getQuantity() * depth.getPrice();
                    } else {
                        quantity = 0L;
                        totalPrice = quantity * depth.getPrice();
                    }
                }
            }
            double averageBuyPrice = totalPrice / requiredQuantity;
            if(quantity < (requiredQuantity*5/100) && averageBuyPrice < expectedPrice+(expectedPrice*5/100)){
                return true;
            }else{
                logger.debug(" instrument ID {} : available quantity {} : requiredQuantity {} : expectedPrice {} : averageBuyPrice {} ",instrument,totalStockSize,requiredQuantity,expectedPrice, averageBuyPrice);
            }
        }else{
            logger.debug(" instrument ID {} : not available in cacheLatestTick or Depth is zero",instrument);
        }
        return false;
    }


    }
