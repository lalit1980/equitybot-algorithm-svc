package com.equitybot.trade.algorithm.sample;

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
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

@Service
public class Test {

	/** Close price of the last bar */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	IgniteCache<String, TimeSeries> cache;

	public Test() {
		IgniteConfiguration cfg = new IgniteConfiguration();
		TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
		TcpDiscoveryKubernetesIpFinder ipFinder = new TcpDiscoveryKubernetesIpFinder();
		ipFinder.setServiceName("ignite");
		tcpDiscoverySpi.setIpFinder(ipFinder);
		cfg.setDiscoverySpi(tcpDiscoverySpi);
		cfg.setPeerClassLoadingEnabled(true);
		cfg.setClientMode(true);
		Ignite ignite = Ignition.start(cfg);
		Ignition.setClientMode(true);
		ignite.active(true);
		CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		ccfg.setCacheMode(CacheMode.PARTITIONED);
		ccfg.setRebalanceMode(CacheRebalanceMode.NONE);
		ccfg.setDataRegionName("1GB_Region");
		this.cache = ignite.getOrCreateCache(ccfg);
	}

	public IgniteCache<String, TimeSeries> getCache() {
		return cache;
	}

	public void runStartegy(String seriesName) {
		TimeSeries timeSeries = cache.get(seriesName);

		if (timeSeries != null) {
			TrueRange trueRange = new TrueRange();
			trueRange.buildTrueRange(timeSeries);
			AverageTrueRange averageTrueRange = new AverageTrueRange();
			averageTrueRange.buildAverageTrueRange(trueRange.getTrueRangeList(), 10);
			BasicLowerBand basicLowerBand = new BasicLowerBand();
			basicLowerBand.buildBasicLowerBand(timeSeries, averageTrueRange, 2);
			BasicUpperBand basicUpperBand = new BasicUpperBand();
			basicUpperBand.buildBasicUpperBand(timeSeries, averageTrueRange, 2);
			FinalLowerBand finalLowerBand = new FinalLowerBand();
			finalLowerBand.buildFinalLowerBand(timeSeries, basicLowerBand);
			FinalUpperBand finalUpperBand = new FinalUpperBand();
			finalUpperBand.buildFinalUpperBand(timeSeries, basicUpperBand);
			SuperTrend superTrend = new SuperTrend();
			superTrend.buildSuperTrend(timeSeries, finalLowerBand, finalUpperBand);
			SuperTrendBuySell superTrendBuySell = new SuperTrendBuySell();
			superTrendBuySell.buildSuperTrendBuySell(timeSeries, superTrend);
			superTrendBuySell.getSuperTrendBuySellList();
		}
	}
}
