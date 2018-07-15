package com.equitybot.trade.algorithm.strategy;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.constants.Constant;

public class BaseIndicator {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private Decimal smoothingConstant;

	public Decimal calculateTrueRange(Bar previousBar, Bar workingBar) {
		Decimal trueRange = workingBar.getMaxPrice().minus(workingBar.getMinPrice())
				.max((workingBar.getMaxPrice().minus(previousBar.getClosePrice()).abs()))
				.max((workingBar.getMinPrice().minus(previousBar.getClosePrice()).abs()));
		return trueRange;
	}

	public  Decimal calculateSMA(List<Decimal> trueRangeList) {
		Decimal sma=null;
			sma = Decimal.valueOf(0);

			for (Decimal trueRange : trueRangeList) {
				sma = sma.plus(trueRange);
			}
			sma = sma.dividedBy(trueRangeList.size());
			this.smoothingConstant = Decimal.TWO.dividedBy(trueRangeList.size() + 1);
		
		return sma;

	}

	public Decimal calculateEMA(Decimal previousEMA, Decimal workingTR) {
		Decimal ema = smoothingConstant.multipliedBy(workingTR.minus(previousEMA)).plus(previousEMA);
		return ema;
	}

	public Decimal calculateBasicUpperBand(Bar workingBar, Decimal workingEMA, int bandSize) {
		Decimal basicUpperBand = workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
				.plus(workingEMA.multipliedBy(bandSize));
		return basicUpperBand;
	}

	public Decimal calculateBasicLowerBand(Bar workingBar, Decimal workingEMA, int bandSize) {
		Decimal basicLowerBand = workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
				.minus(workingEMA.multipliedBy(bandSize));
		return basicLowerBand;
	}

	public Decimal calculateFinalUpperBand(Decimal workingBUB, Decimal previousFUB, Bar previousBar) {
		Decimal finalUpperBand;
		if (workingBUB.isLessThan(previousFUB) || previousBar.getClosePrice().isGreaterThan(previousFUB)) {
			finalUpperBand = workingBUB;
		} else {
			finalUpperBand = previousFUB;
		}
		return finalUpperBand;
	}

	public Decimal calculateFinalLowerBand(Decimal workingBLB, Decimal previousFLB, Bar previousBar) {
		Decimal finalLowerBand;
		if (workingBLB.isGreaterThan(previousFLB) || previousBar.getClosePrice().isLessThan(previousFLB)) {
			finalLowerBand = workingBLB;
		} else {
			finalLowerBand = previousFLB;
		}
		return finalLowerBand;
	}

	public Decimal calculateSuperTrend(Bar workingBar, Decimal workingFUB, Decimal workingFLB) {
		Decimal superTrend;
		if (workingBar.getClosePrice().isLessThanOrEqual(workingFUB)) {
			superTrend = workingFUB;
		} else {
			superTrend = workingFLB;
		}
		return superTrend;
	}

	public String calculateBuySell(Bar workingBar, Decimal workingSuperTrend) {
		String superTrendBuySell;
		if (workingBar.getClosePrice().isLessThan(workingSuperTrend)) {
			superTrendBuySell = Constant.BUY;
		} else {
			superTrendBuySell = Constant.SELL;
		}
		return superTrendBuySell;
	}

}
