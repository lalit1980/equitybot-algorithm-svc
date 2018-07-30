package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.models.Instrument;
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
    
    private IgniteCache<Long, Instrument> cacheInstrument;



    public Cache(){
        CacheConfiguration<Long, String> ccfgOrderDetails = new CacheConfiguration<Long, String>("CachedTradeOrder");
        ccfgOrderDetails.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfgOrderDetails.setCacheMode(CacheMode.PARTITIONED);
        ccfgOrderDetails.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfgOrderDetails.setDataRegionName("1GB_Region");
        this.boughtInstruments = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);

        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfg.setCacheMode(CacheMode.PARTITIONED);
        ccfg.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfg.setDataRegionName("1GB_Region");
        this.timeSeries = igniteConfig.getInstance().getOrCreateCache(ccfg);


        CacheConfiguration<Long, Tick> ccfgLatestTickParams = new CacheConfiguration<>("CachedLatestTick");
        ccfgLatestTickParams.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfgLatestTickParams.setCacheMode(CacheMode.PARTITIONED);
        ccfgLatestTickParams.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfgLatestTickParams.setDataRegionName("1GB_Region");

        this.cacheLatestTick = igniteConfig.getInstance().getOrCreateCache(ccfgLatestTickParams);
        
        CacheConfiguration<Long, Double> ccfgcacheTotalProfit = new CacheConfiguration<Long, Double>("CacheTotalProfit");
        ccfgcacheTotalProfit.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfgcacheTotalProfit.setCacheMode(CacheMode.PARTITIONED);
        ccfgcacheTotalProfit.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfgcacheTotalProfit.setDataRegionName("1GB_Region");
        this.cacheTotalProfit = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTotalProfit);
        
        CacheConfiguration<Long, Boolean> ccfgcacheTrailStopLossFlag = new CacheConfiguration<Long, Boolean>("CacheTrailStopLossFlag");
        ccfgcacheTrailStopLossFlag.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfgcacheTrailStopLossFlag.setCacheMode(CacheMode.PARTITIONED);
        ccfgcacheTrailStopLossFlag.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfgcacheTrailStopLossFlag.setDataRegionName("1GB_Region");
        this.cacheTrailStopLossFlag = igniteConfig.getInstance().getOrCreateCache(ccfgcacheTrailStopLossFlag);
        
        CacheConfiguration<Long, Instrument> ccfgcacheInstrument = new CacheConfiguration<Long, Instrument>("CacheInstrument");
        ccfgcacheInstrument.setAtomicityMode(CacheAtomicityMode.ATOMIC);
        ccfgcacheInstrument.setCacheMode(CacheMode.PARTITIONED);
        ccfgcacheInstrument.setRebalanceMode(CacheRebalanceMode.NONE);
        ccfgcacheInstrument.setDataRegionName("1GB_Region");
		this.cacheInstrument = igniteConfig.getInstance().getOrCreateCache(ccfgcacheInstrument);
        
       
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
