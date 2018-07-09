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

import com.equitybot.trade.algorithm.bo.LogData;

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
		TcpDiscoverySpi tcpDiscoverySpi= new TcpDiscoverySpi();
		TcpDiscoveryKubernetesIpFinder ipFinder  = new TcpDiscoveryKubernetesIpFinder();
		ipFinder.setServiceName("ignite");
		tcpDiscoverySpi.setIpFinder(ipFinder);
		cfg.setDiscoverySpi(tcpDiscoverySpi);
		cfg.setPeerClassLoadingEnabled(true);
		cfg.setClientMode(true);
		Ignite ignite = Ignition.start(cfg);
		Ignition.setClientMode(true);
		ignite.active(true);
		CacheConfiguration<String, TimeSeries> timeSeriesCCFG = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		timeSeriesCCFG.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		timeSeriesCCFG.setCacheMode(CacheMode.PARTITIONED);
		timeSeriesCCFG.setRebalanceMode(CacheRebalanceMode.NONE);
		timeSeriesCCFG.setDataRegionName("1GB_Region");
		 timeSeriesCCFG = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		CacheConfiguration<Long, Double> totalProfitCCFG = new CacheConfiguration<Long, Double>("TotalProfitCache");
		totalProfitCCFG.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		totalProfitCCFG.setCacheMode(CacheMode.PARTITIONED);
		totalProfitCCFG.setRebalanceMode(CacheRebalanceMode.NONE);
		totalProfitCCFG.setDataRegionName("1GB_Region");
		totalProfitCCFG = new CacheConfiguration<Long, Double>("TotalProfitCache");
		this.timeSeriesCache = ignite.getOrCreateCache(timeSeriesCCFG);
		this.totalProfitCache = ignite.getOrCreateCache(totalProfitCCFG);
	}

	
	public void startBot(String seriesName) throws InterruptedException, IOException {

		logger.info("********************** Initialization **********************");
		TimeSeries series = timeSeriesCache.get(seriesName);
		logger.info("&&&&&&&& Series Name: "+seriesName);
		superTrend.evaluate(series,totalProfitCache);

	}

	public IgniteCache<String, TimeSeries> getCache() {
		return timeSeriesCache;
	}

	public void setCache(IgniteCache<String, TimeSeries> cache) {
		this.timeSeriesCache = cache;
	}
}
