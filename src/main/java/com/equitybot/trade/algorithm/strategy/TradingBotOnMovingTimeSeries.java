package com.equitybot.trade.algorithm.strategy;

import java.io.IOException;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CacheRebalanceMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.kubernetes.TcpDiscoveryKubernetesIpFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;


@Service
public class TradingBotOnMovingTimeSeries {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	IgniteCache<String, TimeSeries> timeSeriesCache;
	IgniteCache<Long, Double> totalProfitCache;
	@Value("${supertrend.maxBarCount}")
	private int maxBarCount;
	@Value("${supertrend.smaSize}")
	private int smaSize;
	@Autowired
	ValidateSuperTrend superTrend;
	
	public TradingBotOnMovingTimeSeries() {
		
		IgniteConfiguration cfg = new IgniteConfiguration();
		Ignite ignite = Ignition.start(cfg);
		Ignition.setClientMode(true);
		CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		this.timeSeriesCache = ignite.getOrCreateCache(ccfg);
	}

	
	public void startBot(String seriesName) throws InterruptedException, IOException {

		logger.info("********************** Initialization **********************");
		TimeSeries series = timeSeriesCache.get(seriesName);
		logger.info("&&&&&&&& Series Name: "+seriesName);
		superTrend.evaluate(series);

	}


	public IgniteCache<String, TimeSeries> getTimeSeriesCache() {
		return timeSeriesCache;
	}


	public void setTimeSeriesCache(IgniteCache<String, TimeSeries> timeSeriesCache) {
		this.timeSeriesCache = timeSeriesCache;
	}

	
}
