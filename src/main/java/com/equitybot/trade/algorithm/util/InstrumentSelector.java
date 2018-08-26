package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.model.InstrumentSelectorDTO;
import com.zerodhatech.models.Depth;
import com.zerodhatech.models.Tick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ta4j.core.Decimal;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

@Component
public class InstrumentSelector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Cache cache;

    @Autowired
    private Pool pool;

    @Value("${selector.max-instrument-price-per-stock}")
    private double maxInstrumentPricePerStockLimit;

    @Value("${selector.max-total-price}")
    private double maxTotalPriceLimit;


    public InstrumentSelectorDTO eligibleInstrument(Long instrument, double expectedPrice) {

        InstrumentSelectorDTO instrumentSelectorDTO = new InstrumentSelectorDTO();
        boolean profitableB = profitable(instrument,instrumentSelectorDTO);
        boolean underPriceRangeB = underPriceRange(instrument, instrumentSelectorDTO);
      //  boolean feasibleSellerAvailabilitB = feasibleSellerAvailability(instrument, expectedPrice, instrumentSelectorDTO);
        //instrumentSelectorDTO.setEligibleInstrument(profitableB && underPriceRangeB && feasibleSellerAvailabilitB);
        return instrumentSelectorDTO;
    }

    private boolean profitable(Long instrument, InstrumentSelectorDTO instrumentSelectorDTO) {
        Decimal instrumentProfit = this.pool.getSuperTrendProfitPool().get(instrument);
        if (instrumentProfit != null) {
            logger.debug(" instrument ID {} : instrumentProfit {} is profitable {}", instrument, instrumentProfit, instrumentProfit.isGreaterThan(Decimal.ZERO));
            instrumentSelectorDTO.setInstrumentProfit(instrumentProfit.doubleValue());
            return instrumentProfit.isGreaterThan(Decimal.ZERO);
        } else {
            logger.debug("Not yet traded or analyzed");
        }
        return false;
    }

    private boolean underPriceRange(Long instrument,InstrumentSelectorDTO instrumentSelectorDTO) {
        Double instrumentTick = this.cache.getCacheLastTradedPrice().get(instrument);
        if (instrumentTick != null) {
            double instrumentPrice = instrumentTick;
            instrumentSelectorDTO.setInstrumentClosePrice(instrumentPrice);
            logger.debug(" instrument ID {} : Close Price is {} : maxPriceLimit is {}", instrument, instrumentPrice, this.maxInstrumentPricePerStockLimit);
            return instrumentPrice <= maxInstrumentPricePerStockLimit;
        } else {
            logger.debug(" instrument ID {} : not available in cacheLatestTick", instrument);
        }
        return false;
    }
/*
    private boolean feasibleSellerAvailability(Long instrument, double expectedPrice,InstrumentSelectorDTO instrumentSelectorDTO) {
        Tick instrumentTick = this.cache.getCacheLatestTick().get(instrument);

        if (instrumentTick != null && instrumentTick.getMarketDepth() != null) {
            ArrayList<Depth> instrumentDepth=null;
            for ( Map.Entry< String, ArrayList<Depth>>  depths : instrumentTick.getMarketDepth().entrySet()) {
                if("sell".equalsIgnoreCase(depths.getKey())){
                   instrumentDepth = depths.getValue();
                }
            }
            if(instrumentDepth != null && !instrumentDepth.isEmpty()){
            double instrumentPrice = instrumentTick.getClosePrice();
            Long quantity = Math.round(maxTotalPriceLimit / instrumentPrice);
            Long expectedQuantity = quantity;
            double totalStockSize = 0;
            double totalPrice = 0;

            for (Depth depth : instrumentDepth) {
                totalStockSize += depth.getQuantity();
                if (quantity > 0) {
                    if (quantity > depth.getQuantity()) {
                        quantity = quantity - depth.getQuantity();
                        totalPrice += depth.getQuantity() * depth.getPrice();
                    } else {
                        quantity = 0L;
                        totalPrice += quantity * depth.getPrice();
                    }
                }
            }
            double averageBuyPrice = totalPrice / expectedQuantity;
            instrumentSelectorDTO.setAverageBuyPrice(averageBuyPrice);
            instrumentSelectorDTO.setExpectedQuantity(expectedQuantity);
            instrumentSelectorDTO.setTotalStockSize(totalStockSize);

            if (quantity < (expectedQuantity * 5 / 100) && averageBuyPrice < expectedPrice + (expectedPrice * 5 / 100)) {
                return true;
            } else {
                logger.debug(" instrument ID {} : available quantity {} : requiredQuantity {} : expectedPrice {} : averageBuyPrice {} ", instrument, totalStockSize, expectedQuantity, expectedPrice, averageBuyPrice);
            }
        }
        } else {
            logger.debug(" instrument ID {} : not available in cacheLatestTick or Depth is zero", instrument);
        }
        return false;
    }

*/
}
