package com.equitybot.trade.algorithm.indicator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.constants.Constant;

@Service
public class BaseIndicator {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public Decimal calculateTrueRange(Bar previousBar, Bar workingBar) {
		return workingBar.getMaxPrice().minus(workingBar.getMinPrice())
                .max((workingBar.getMaxPrice().minus(previousBar.getClosePrice()).abs()))
                .max((workingBar.getMinPrice().minus(previousBar.getClosePrice()).abs()));
		
		
	}

	public  Decimal calculateSMA(List<Decimal> trueRangeList) {
	        Decimal sma = Decimal.valueOf(0);
	        for (Decimal trueRange : trueRangeList) {
	            sma = sma.plus(trueRange);
	        }
	        sma = sma.dividedBy(trueRangeList.size());
	        return sma;

	}

	public Decimal calculateEMA(Decimal previousEMA, Decimal workingTR, Decimal smoothingConstant) {
        return smoothingConstant.multipliedBy(workingTR.minus(previousEMA)).plus(previousEMA);
	}

	public Decimal calculateBasicUpperBand(Bar workingBar, Decimal workingEMA, int multiplier) {
        return workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
                .plus(workingEMA.multipliedBy(multiplier));
	}

	public Decimal calculateBasicLowerBand(Bar workingBar, Decimal workingEMA, int multiplier) {
        return workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
                .minus(workingEMA.multipliedBy(multiplier));
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

	public Decimal calculateSuperTrend(Bar workingBar, Decimal workingFUB, Decimal workingFLB, Decimal previousFLB, 
			Decimal previousST, Decimal previousFUB) {
        Decimal superTrend;
        if (previousST.
        		equals(previousFUB) && 
        		workingBar.getClosePrice().
        		isLessThanOrEqual
        		(workingFUB)) {
            superTrend = workingFUB;
        } else if (previousST.equals(previousFUB) && workingBar.getClosePrice().isGreaterThan(workingFUB)) {
            superTrend = workingFLB;
        } else if (previousST.equals(previousFLB) && workingBar.getClosePrice().isGreaterThanOrEqual(workingFLB)) {
            superTrend = workingFLB;
        } else if (previousST.equals(previousFLB) && workingBar.getClosePrice().isLessThan(workingFLB)) {
            superTrend = workingFUB;
        } else {
            superTrend = Decimal.ZERO;
        }
        return superTrend;
	}

	public String calculateBuySell(Bar workingBar, Decimal workingSuperTrend) {
		String superTrendBuySell;
		if (workingBar.getClosePrice().isLessThan(workingSuperTrend)) {
			superTrendBuySell = Constant.SELL;
		} else {
			superTrendBuySell = Constant.BUY;
		}
		logger.info(superTrendBuySell);
		return superTrendBuySell;
	}
	

}
