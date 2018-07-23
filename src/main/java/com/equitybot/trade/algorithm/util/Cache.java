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
}
