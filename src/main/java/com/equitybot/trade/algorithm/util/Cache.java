package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.models.Tick;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

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



    public Cache(){
        CacheConfiguration<Long, String> ccfgOrderDetails = new CacheConfiguration<Long, String>("CachedTradeOrder");
        this.boughtInstruments = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);

        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        this.timeSeries = igniteConfig.getInstance().getOrCreateCache(ccfg);

        CacheConfiguration<Long, Tick> ccfgLatestTickParams = new CacheConfiguration<Long, Tick>("CachedLatestTick");
        this.cacheLatestTick = igniteConfig.getInstance().getOrCreateCache(ccfgLatestTickParams);
        
        CacheConfiguration<Long, Double> ccfgcacheTotalProfit = new CacheConfiguration<Long, Double>("CacheTotalProfit");
        this.cacheTotalProfit = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTotalProfit);
        
        CacheConfiguration<Long, Boolean> ccfgcacheTrailStopLossFlag = new CacheConfiguration<Long, Boolean>("CacheTrailStopLossFlag");
        this.cacheTrailStopLossFlag = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTrailStopLossFlag);
        
       
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

}
