package com.equitybot.trade.algorithm.sample;

import java.util.LinkedList;
import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import com.equitybot.trade.algorithm.bo.SuperTrendBO;

@Service
public class Test {

	/** Close price of the last bar */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	IgniteCache<String, TimeSeries> cache;

	private List<SuperTrendBO> superTrendList = null;

	public Test() {
		IgniteConfiguration cfg = new IgniteConfiguration();
		Ignite ignite = Ignition.start(cfg);
		Ignition.setClientMode(true);
		CacheConfiguration<String, TimeSeries> ccfg = new CacheConfiguration<String, TimeSeries>("TimeSeriesCache");
		this.cache = ignite.getOrCreateCache(ccfg);
		superTrendList = new LinkedList<SuperTrendBO>();
	}

	public IgniteCache<String, TimeSeries> getCache() {
		return cache;
	}

	public void runStartegy(String seriesName) {
		TimeSeries series = cache.get(seriesName);

		/*
		
		 BASIC UPPERBAND = (HIGH + LOW) / 2 + Multiplier * ATR 
		 BASIC LOWERBAND = (HIGH+ LOW) / 2 - Multiplier * ATR
		 
		FINAL UPPERBAND = IF( (Current BASICUPPERBAND < Previous FINAL UPPERBAND) and
		 (Previous Close > Previous FINAL UPPERBAND)) THEN (Current BASIC UPPERBAND)
		 ELSE Previous FINALUPPERBAND)
		 
		 FINAL LOWERBAND = IF( (Current BASIC LOWERBAND > Previous FINAL LOWERBAND)
		 and (Previous Close < Previous FINAL LOWERBAND)) THEN (Current BASIC
		  LOWERBAND) ELSE Previous FINAL LOWERBAND)
		 
		 SUPERTREND = IF(Current Close <= Current FINAL UPPERBAND ) THEN Current FINAL
		  UPPERBAND ELSE Current FINAL LOWERBAND
		 */

		if (series != null) {
			ATRIndicator atr = new ATRIndicator(series, 10);
			final int nbBars = series.getBarCount();
			Decimal previousUpperBand = Decimal.valueOf(0.0);
			Decimal previousLowerBand = Decimal.valueOf(0.0);
			SuperTrendBO trend = new SuperTrendBO();
			for (int i = 0; i < nbBars; i++) {
				Decimal high = atr.getTimeSeries().getBar(i).getMaxPrice();
				Decimal low = atr.getTimeSeries().getBar(i).getMinPrice();
			
				Decimal currentBasicUpperBand = ((high.plus(low)).dividedBy(2)).plus(atr.getValue(i).multipliedBy(3));
				Decimal currentBasicLowerBand = ((high.plus(low)).dividedBy(2)).minus(atr.getValue(i).multipliedBy(3));
				
				
				
				logger.info("Sl No: " + i + " ATR %2f" + atr.getValue(i) + " High: %2f" + high + " Low: " + low
						+ " Basic Lower Band: " + currentBasicLowerBand + " Basic Upper Band: " + currentBasicUpperBand);

			}
		}

	}

}
