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
        logger.info(" * added new True Range for instrument");
        Decimal trueRange = workingBar.getMaxPrice().minus(workingBar.getMinPrice())
                .max((workingBar.getMaxPrice().minus(previousBar.getClosePrice()).abs()))
                .max((workingBar.getMinPrice().minus(previousBar.getClosePrice()).abs()));
        logger.info(trueRange.toString());
        return trueRange;
    }

    public Decimal calculateSMA(List<Decimal> trueRangeList) {
        logger.info(" * calculate SMA");
        Decimal sma = Decimal.valueOf(0);
        for (Decimal trueRange : trueRangeList) {
            sma = sma.plus(trueRange);
        }
        sma = sma.dividedBy(trueRangeList.size());
        logger.info(sma.toString());
        this.smoothingConstant = Decimal.TWO.dividedBy(trueRangeList.size() + 1);
        return sma;
    }

    public Decimal calculateEMA(Decimal previousEMA, Decimal workingTR) {
        logger.info(" * calculate EMA");
        Decimal ema = smoothingConstant.multipliedBy(workingTR.minus(previousEMA)).plus(previousEMA);
        logger.info(ema.toString());
        return ema;
    }

    public Decimal calculateBasicUpperBand(Bar workingBar, Decimal workingEMA, int bandSize) {
        logger.info(" * calculate Basic Upper Band");
        Decimal basicUpperBand = workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
                .plus(workingEMA.multipliedBy(bandSize));
        logger.info(basicUpperBand.toString());
        return basicUpperBand;
    }

    public Decimal calculateBasicLowerBand(Bar workingBar, Decimal workingEMA, int bandSize) {
        logger.info(" * calculate Basic Lower Band");
        Decimal basicLowerBand = workingBar.getMaxPrice().plus(workingBar.getMinPrice()).dividedBy(Decimal.TWO)
                .minus(workingEMA.multipliedBy(bandSize));
        logger.info(basicLowerBand.toString());
        return basicLowerBand;
    }

    public Decimal calculateFinalUpperBand(Decimal workingBUB, Decimal previousFUB, Bar previousBar) {
        logger.info(" * calculate Final Upper Band");
        Decimal finalUpperBand;
        if (workingBUB.isLessThan(previousFUB) || previousBar.getClosePrice().isGreaterThan(previousFUB)) {
            finalUpperBand = workingBUB;
        } else {
            finalUpperBand = previousFUB;
        }
        logger.info(finalUpperBand.toString());
        return finalUpperBand;
    }

    public Decimal calculateFinalLowerBand(Decimal workingBLB, Decimal previousFLB, Bar previousBar) {
        logger.info(" * calculate Final Lower Band");
        Decimal finalLowerBand;
        if (workingBLB.isGreaterThan(previousFLB) || previousBar.getClosePrice().isLessThan(previousFLB)) {
            finalLowerBand = workingBLB;
        } else {
            finalLowerBand = previousFLB;
        }
        logger.info(finalLowerBand.toString());
        return finalLowerBand;
    }

    public Decimal calculateSuperTrend(Bar workingBar, Decimal workingFUB, Decimal workingFLB) {
        logger.info(" * adding new Super Trend");
        Decimal superTrend;
        if (workingBar.getClosePrice().isLessThanOrEqual(workingFUB)) {
            superTrend = workingFUB;
        } else {
            superTrend = workingFLB;
        }
        logger.info(superTrend.toString());
        return superTrend;
    }
    
    public String calculateBuySell(Bar workingBar, Decimal workingSuperTrend) {
        logger.info(" * Adding new Super Trend BuyS Sell");
        String superTrendBuySell;
        if (workingBar.getClosePrice().isLessThan(workingSuperTrend)) {
            superTrendBuySell = Constant.BUY;
        } else {
            superTrendBuySell = Constant.SELL;
        }
        logger.info(superTrendBuySell);
        return superTrendBuySell;
    }



}
