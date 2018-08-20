package com.equitybot.trade.algorithm.indicator;


import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.constants.Constant;
import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;

public class SuperTrendAnalyzer extends BaseIndicator {

    private boolean haveBuy;
    private Bar lastBuyBar;
    private Decimal totalProfitLoss;
    private SuperTradeIndicator superTradeIndicator;
    private Decimal currentProfitLoss;
    private byte action;
    private long barCount;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private IgniteCache<Long, Boolean> startTrade;

    @Autowired
    IgniteConfig igniteConfig;

    public SuperTrendAnalyzer(int multiplier, int period, long instrument) {
        this.superTradeIndicator = new SuperTradeIndicator(multiplier, period, instrument);
        this.haveBuy = false;
        this.totalProfitLoss = Decimal.ZERO;
        action = 0;
        CacheConfiguration<Long, Boolean> ccfgcStartTrade = new CacheConfiguration<Long, Boolean>("CacheStartTrade");
		 this.startTrade = igniteConfig.getInstance().getOrCreateCache(ccfgcStartTrade);
    }

    public void analyze(Bar bar) {
        this.superTradeIndicator.calculate(bar);
        calculateProfitLoss();
        barCount++;
    }

    private void calculateProfitLoss() {
    	
    			boolean startTradeFlag=this.startTrade.get(superTradeIndicator.getInstrument());
        		logger.info("Start Trade Flag in Algo Sell Order: "+startTradeFlag);
            	if(startTradeFlag) {
            		if (this.superTradeIndicator.getBuySell() != null && this.haveBuy && Constant.SELL.equals(this.superTradeIndicator.getBuySell())) {
                        sell();
                        this.action = -1;
                    } else if (this.superTradeIndicator.getBuySell() != null && !this.haveBuy && Constant.BUY.equals(this.superTradeIndicator.getBuySell())) {
                        buy();
                        this.action = 1;
                    } else {
                        this.action = 0;
                    }
            	}
    		
    }

    private void buy() {
        this.haveBuy = true;
        this.lastBuyBar = this.superTradeIndicator.getBar();
        this.currentProfitLoss = Decimal.ZERO;
    }

    private void sell() {
        this.currentProfitLoss = this.superTradeIndicator.getBar().getClosePrice().minus(this.lastBuyBar.getClosePrice());
        this.totalProfitLoss = this.totalProfitLoss.plus(currentProfitLoss);
        this.haveBuy = false;
    }

    public Decimal getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public SuperTradeIndicator getSuperTradeIndicator() {
        return superTradeIndicator;
    }

    public Decimal getCurrentProfitLoss() {
        return currentProfitLoss;
    }

    public byte getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "SuperTrendAnalyzer{" +
                "haveBuy=" + haveBuy +
                ", lastBuyBar=" + lastBuyBar +
                ", totalProfitLoss=" + totalProfitLoss +
                ", superTradeIndicator=" + superTradeIndicator +
                ", currentProfitLoss=" + currentProfitLoss +
                ", action=" + action +
                ", barCount=" + barCount +
                '}';
    }
}
