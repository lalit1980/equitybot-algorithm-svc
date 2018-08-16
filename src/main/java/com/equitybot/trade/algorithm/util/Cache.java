package com.equitybot.trade.algorithm.util;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.models.Instrument;
import com.zerodhatech.models.Tick;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Cache {

    @Autowired
    IgniteConfig igniteConfig;

    private IgniteCache<Long, String> boughtInstruments;

    private IgniteCache<String, TimeSeries> timeSeries;

    private IgniteCache<Long, Tick> cacheLatestTick;
    private IgniteCache<Long, Double> cacheTotalProfit;
    private IgniteCache<Long, Boolean> cacheTrailStopLossFlag;
    private IgniteCache<Long, Instrument> cacheInstrument;
    private IgniteCache<Long, Boolean> startTrade;

    public IgniteCache<Long, Boolean> getStartTrade() {
		return startTrade;
	}

	public void setStartTrade(IgniteCache<Long, Boolean> startTrade) {
		this.startTrade = startTrade;
	}

	public Cache(){
        CacheConfiguration<Long, String> ccfgOrderDetails = new CacheConfiguration<Long, String>("CachedTradeOrder");
        this.boughtInstruments = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);

        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        this.timeSeries = igniteConfig.getInstance().getOrCreateCache(ccfg);


        CacheConfiguration<Long, Tick> ccfgLatestTickParams = new CacheConfiguration<>("CachedLatestTick");
        this.cacheLatestTick = igniteConfig.getInstance().getOrCreateCache(ccfgLatestTickParams);
        
        CacheConfiguration<Long, Double> ccfgcacheTotalProfit = new CacheConfiguration<Long, Double>("CacheTotalProfit");
        this.cacheTotalProfit = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTotalProfit);
        
        CacheConfiguration<Long, Boolean> ccfgcacheTrailStopLossFlag = new CacheConfiguration<Long, Boolean>("CacheTrailStopLossFlag");
        this.cacheTrailStopLossFlag = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTrailStopLossFlag);
        
        CacheConfiguration<Long, Instrument> ccfgcacheInstrument = new CacheConfiguration<Long, Instrument>("CacheInstrument");
		this.cacheInstrument = igniteConfig.getInstance().getOrCreateCache(ccfgcacheInstrument);
		
		CacheConfiguration<Long, Boolean> ccfgcStartTrade = new CacheConfiguration<Long, Boolean>("CacheStartTrade");
		 this.startTrade = igniteConfig.getInstance().getOrCreateCache(ccfgcStartTrade);
    }

    public IgniteCache<Long, String> getBoughtInstruments() {
        return boughtInstruments;
    }

    public IgniteCache<String, TimeSeries> getTimeSeries() {
        return timeSeries;
    }

    public IgniteCache<Long, Tick> getCacheLatestTick() {
        return cacheLatestTick;
    }

	public IgniteCache<Long, Double> getCacheTotalProfit() {
		return cacheTotalProfit;
	}

	public IgniteCache<Long, Boolean> getCacheTrailStopLossFlag() {
		return cacheTrailStopLossFlag;
	}

	public IgniteCache<Long, Instrument> getCacheInstrument() {
		return cacheInstrument;
	}

	public void setCacheInstrument(IgniteCache<Long, Instrument> cacheInstrument) {
		this.cacheInstrument = cacheInstrument;
	}

}
