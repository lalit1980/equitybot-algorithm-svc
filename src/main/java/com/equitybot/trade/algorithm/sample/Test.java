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
import org.ta4j.core.*;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

@Service
public class Test {

	/** Close price of the last bar */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	IgniteCache<String, TimeSeries> cache;

	public Test() {
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
		CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		ccfg.setAtomicityMode(CacheAtomicityMode.ATOMIC);
		ccfg.setCacheMode(CacheMode.PARTITIONED);
		ccfg.setRebalanceMode(CacheRebalanceMode.NONE);
		ccfg.setDataRegionName("1GB_Region");
		this.cache = ignite.getOrCreateCache(ccfg);
	}

	/**
	 * Builds a moving time series (i.e. keeping only the maxBarCount last bars)
	 * 
	 * @param maxBarCount
	 *            the number of bars to keep in the time series (at maximum)
	 * @return a moving time series
	 */
	private TimeSeries initMovingTimeSeries(int maxBarCount, String seriesName) {
		TimeSeries series = cache.get(seriesName);
		if (series != null) {
			logger.info(seriesName+ " Initial bar count: " + series.getBarCount());
			// Limitating the number of bars to maxBarCount
			series.setMaximumBarCount(maxBarCount);
			return series;
		} else {
			return null;
		}

	}

	/**
	 * @param series
	 *            a time series
	 * @return a dummy strategy
	 */
	private static Strategy buildStrategy(TimeSeries series) {
		if (series == null) {
			throw new IllegalArgumentException("Series cannot be null");
		}

		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		SMAIndicator sma = new SMAIndicator(closePrice, 12);

		// Signals
		// Buy when SMA goes over close price
		// Sell when close price goes over SMA
		return new BaseStrategy(new OverIndicatorRule(sma, closePrice), new UnderIndicatorRule(sma, closePrice));
	}

	public IgniteCache<String, TimeSeries> getCache() {
		return cache;
	}

	public void runStartegy(String seriesName, int maxBarCount) {
		TimeSeries series = initMovingTimeSeries(5, seriesName);

		if (series != null) {
			// Building the trading strategy
			Strategy strategy = buildStrategy(series);

			// Initializing the trading history
			TradingRecord tradingRecord = new BaseTradingRecord();
			logger.info("************************************************************");
			if (series != null && series.getBarCount() > 0) {

				int endIndex = series.getEndIndex();
				for (int i = 0; i < endIndex; i++) {
					Bar newBar = series.getBar(i);
					if (strategy.shouldEnter(endIndex)) {
						// Our strategy should enter
						logger.info(seriesName+" NameStrategy should ENTER on " + endIndex);
						boolean entered = tradingRecord.enter(endIndex, newBar.getClosePrice(), Decimal.TEN);
						if (entered) {
							Order entry = tradingRecord.getLastEntry();
							logger.info(seriesName+" Entered on " + entry.getIndex() + " (price=" + entry.getPrice().doubleValue()
									+ ", amount=" + entry.getAmount().doubleValue() + ")");
						}
					} else if (strategy.shouldExit(endIndex)) {
						// Our strategy should exit
						logger.info("Strategy should EXIT on " + endIndex);
						boolean exited = tradingRecord.exit(endIndex, newBar.getClosePrice(), Decimal.TEN);
						if (exited) {
							Order exit = tradingRecord.getLastExit();
							logger.info(seriesName+" Exited on " + exit.getIndex() + " (price=" + exit.getPrice().doubleValue()
									+ ", amount=" + exit.getAmount().doubleValue() + ")");
						}
					}
				}
			}
		}
	}
}
