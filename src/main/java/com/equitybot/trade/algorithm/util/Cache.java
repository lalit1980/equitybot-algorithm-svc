package com.equitybot.trade.algorithm.util;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Cache {

    @Autowired
    IgniteConfig igniteConfig;

    private IgniteCache<Long, String> boughtInstruments;

    private IgniteCache<String, TimeSeries> timeSeries;

    private IgniteCache<Long, Double> cacheTotalProfit;
    private IgniteCache<Long, Boolean> cacheTrailStopLossFlag;
    private IgniteCache<Long, String> cacheInstrumentTradingSymbol;
    private IgniteCache<Long, Boolean> startTrade;
    private IgniteCache<String, KiteConnect> cacheUserSession;
    private IgniteCache<Long, Integer> cacheQuantity;
	private IgniteCache<Long, Double> cacheLastTradedPrice;
	private IgniteCache<Long, String> cacheOrderPublisher;

    public IgniteCache<Long, Boolean> getStartTrade() {
		return startTrade;
	}

	public void setStartTrade(IgniteCache<Long, Boolean> startTrade) {
		this.startTrade = startTrade;
	}

	public Cache(){
		CacheConfiguration<Long, Double> ccfgLastTradedPrice = new CacheConfiguration<Long, Double>("LastTradedPrice");
		this.cacheLastTradedPrice = igniteConfig.getInstance().getOrCreateCache(ccfgLastTradedPrice);
		
        CacheConfiguration<Long, String> ccfgOrderDetails = new CacheConfiguration<Long, String>("CachedTradeOrder");
        this.boughtInstruments = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);

        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        this.timeSeries = igniteConfig.getInstance().getOrCreateCache(ccfg);

        CacheConfiguration<Long, Double> ccfgcacheTotalProfit = new CacheConfiguration<Long, Double>("CacheTotalProfit");
        this.cacheTotalProfit = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTotalProfit);
        
        CacheConfiguration<Long, Boolean> ccfgcacheTrailStopLossFlag = new CacheConfiguration<Long, Boolean>("CacheTrailStopLossFlag");
        this.cacheTrailStopLossFlag = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTrailStopLossFlag);
        
		CacheConfiguration<Long, String> ccfgcacheInstrument = new CacheConfiguration<Long, String>(
				"CacheInstrumentTradingSymbol");
		this.cacheInstrumentTradingSymbol = igniteConfig.getInstance().getOrCreateCache(ccfgcacheInstrument);
		
		CacheConfiguration<Long, Boolean> ccfgcStartTrade = new CacheConfiguration<Long, Boolean>("CacheStartTrade");
		 this.startTrade = igniteConfig.getInstance().getOrCreateCache(ccfgcStartTrade);
		 
		 CacheConfiguration<String, KiteConnect> ccfgcKiteSession = new CacheConfiguration<String, KiteConnect>("CacheUserSession");
		 this.cacheUserSession = igniteConfig.getInstance().getOrCreateCache(ccfgcKiteSession);
		 
		 CacheConfiguration<Long, Integer> ccfgcQuantity = new CacheConfiguration<Long, Integer>("CacheQuantity");
		 this.cacheQuantity = igniteConfig.getInstance().getOrCreateCache(ccfgcQuantity);
		 
		 CacheConfiguration<Long, String> ccfgCacheOrderPublisher = new CacheConfiguration<Long, String>(
					"CacheCacheOrderPublisher");
			this.cacheOrderPublisher = igniteConfig.getInstance().getOrCreateCache(ccfgCacheOrderPublisher);
    }

    public IgniteCache<Long, String> getBoughtInstruments() {
        return boughtInstruments;
    }

    public IgniteCache<String, TimeSeries> getTimeSeries() {
        return timeSeries;
    }

	public IgniteCache<Long, Double> getCacheTotalProfit() {
		return cacheTotalProfit;
	}

	public IgniteCache<Long, Boolean> getCacheTrailStopLossFlag() {
		return cacheTrailStopLossFlag;
	}

	

	public IgniteCache<String, KiteConnect> getCacheUserSession() {
		return cacheUserSession;
	}

	public void setCacheUserSession(IgniteCache<String, KiteConnect> cacheUserSession) {
		this.cacheUserSession = cacheUserSession;
	}

	public IgniteCache<Long, Integer> getCacheQuantity() {
		return cacheQuantity;
	}

	public void setCacheQuantity(IgniteCache<Long, Integer> cacheQuantity) {
		this.cacheQuantity = cacheQuantity;
	}

	public IgniteCache<Long, String> getCacheInstrumentTradingSymbol() {
		return cacheInstrumentTradingSymbol;
	}

	public void setCacheInstrumentTradingSymbol(IgniteCache<Long, String> cacheInstrumentTradingSymbol) {
		this.cacheInstrumentTradingSymbol = cacheInstrumentTradingSymbol;
	}

	public IgniteCache<Long, Double> getCacheLastTradedPrice() {
		return cacheLastTradedPrice;
	}

	public IgniteCache<Long, String> getCacheOrderPublisher() {
		return cacheOrderPublisher;
	}

}
