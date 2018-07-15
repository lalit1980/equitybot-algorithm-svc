package com.equitybot.trade.algorithm.strategy;

import java.io.IOException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

import com.equitybot.trade.algorithm.ignite.configs.IgniteConfig;
import com.zerodhatech.models.Tick;


@Service
public class TradingBotOnMovingTimeSeries {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	IgniteCache<String, TimeSeries> timeSeriesCache;

	@Value("${supertrend.maxBarCount}")
	private int maxBarCount;
	@Value("${supertrend.smaSize}")
	private int smaSize;
	@Autowired
	ValidateSuperTrend superTrend;
	
	@Autowired
	IgniteConfig igniteConfig;
	
	
	public void startBot(String seriesName) throws InterruptedException, IOException {
		CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		this.timeSeriesCache = igniteConfig.getInstance().getOrCreateCache(ccfg);
		TimeSeries series = timeSeriesCache.get(seriesName);
		superTrend.evaluate(series);

	}


	public IgniteCache<String, TimeSeries> getTimeSeriesCache() {
		return timeSeriesCache;
	}


	public void setTimeSeriesCache(IgniteCache<String, TimeSeries> timeSeriesCache) {
		this.timeSeriesCache = timeSeriesCache;
	}
}
