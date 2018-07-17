package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.models.Tick;
import org.apache.ignite.IgniteCache;
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
        this.boughtInstruments = igniteConfig.getInstance().getOrCreateCache(ccfgOrderDetails);

        CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
        this.timeSeries = igniteConfig.getInstance().getOrCreateCache(ccfg);

        CacheConfiguration<Long, Tick> ccfgLatestTickParams = new CacheConfiguration<>("CachedLatestTick");
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
