package com.equitybot.trade.algorithm.indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import java.util.LinkedList;
import java.util.List;

public class SuperTradeIndicator extends BaseIndicator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int smaSize;
    private long instrument;
    private int bandSize;
    private boolean isInit;
    private List<Decimal> initTrueRangeList;
    private Bar previousBar;
    private Decimal previousFUB;
    private Decimal previousFLB;
    private Decimal previousEMA;

    private Decimal trueRange;
    private Decimal exponentialMovingAverage;
    private Decimal basicUpperBand;
    private Decimal basicLowerBand;
    private Decimal finalUpperBand;
    private Decimal finalLowerBand;
    private Decimal superTrend;
    private String buySell;
    private Bar bar;


    public SuperTradeIndicator(int bandSize, int smaSize, long instrument) {
        this.bandSize = bandSize;
        this.smaSize = smaSize;
        this.instrument = instrument;
        this.isInit = false;
        this.initTrueRangeList = new LinkedList<>();
    }

    public Decimal calculate(Bar workingBar) {
        updateValues(workingBar);
        if (isInit) {
            calculate();
            logger.info(" SuperTradeIndicator calculated for instrument {} ", instrument);
        } else {
            logger.info(" SuperTradeIndicator :  instrument {} is in init stage", instrument);
            init();
        }
        return null;
    }

    private Decimal calculate() {
        this.trueRange = calculateTrueRange(this.previousBar, this.bar);
        this.exponentialMovingAverage = calculateEMA(this.previousEMA, this.trueRange);
        this.basicUpperBand = calculateBasicUpperBand(this.bar, this.exponentialMovingAverage, this.bandSize);
        this.basicLowerBand = calculateBasicLowerBand(this.bar, this.exponentialMovingAverage, this.bandSize);
        this.finalUpperBand = calculateFinalUpperBand(this.basicUpperBand, this.previousFUB, this.previousBar);
        this.finalLowerBand = calculateFinalLowerBand(this.basicLowerBand, this.previousFLB, this.previousBar);
        this.superTrend = calculateSuperTrend(this.bar, this.finalUpperBand, this.finalLowerBand);
        this.buySell = calculateBuySell(this.bar, this.superTrend);
        return this.superTrend;
    }

    private Decimal init() {
        this.trueRange = Decimal.ZERO;
        this.exponentialMovingAverage = Decimal.ZERO;
        this.basicUpperBand = Decimal.ZERO;
        this.basicLowerBand = Decimal.ZERO;
        this.finalUpperBand = Decimal.ZERO;
        this.finalLowerBand = Decimal.ZERO;
        this.superTrend = calculateSuperTrend(this.bar, this.finalUpperBand, this.finalLowerBand);
        if (this.previousBar != null) {
            this.trueRange = calculateTrueRange(this.previousBar, this.bar);
            this.initTrueRangeList.add(this.trueRange);
        }
        if (this.initTrueRangeList.size() == this.smaSize) {
            this.exponentialMovingAverage = calculateSMA(this.initTrueRangeList);
            this.basicUpperBand = calculateBasicUpperBand(this.bar, this.exponentialMovingAverage, this.bandSize);
            this.basicLowerBand = calculateBasicLowerBand(this.bar, this.exponentialMovingAverage, this.bandSize);
            this.finalUpperBand = calculateFinalUpperBand(this.basicUpperBand, Decimal.ZERO, this.previousBar);
            this.finalLowerBand = calculateFinalLowerBand(this.basicLowerBand, Decimal.ZERO, this.previousBar);
            this.superTrend = calculateSuperTrend(this.bar, this.finalUpperBand, this.finalLowerBand);
            this.isInit = true;
        }
        return this.superTrend;
    }

    private void updateValues(Bar workingBar) {
        this.previousFLB = this.finalLowerBand;
        this.previousFUB = this.finalUpperBand;
        this.previousEMA = this.exponentialMovingAverage;
        this.previousBar = this.bar;
        this.bar = workingBar;
    }

    public long getInstrument() {
        return this.instrument;
    }

    public Decimal getTrueRange() {
        return trueRange;
    }

    public Decimal getExponentialMovingAverage() {
        return exponentialMovingAverage;
    }

    public Decimal getBasicUpperBand() {
        return basicUpperBand;
    }

    public Decimal getBasicLowerBand() {
        return basicLowerBand;
    }

    public Decimal getFinalUpperBand() {
        return finalUpperBand;
    }

    public Decimal getFinalLowerBand() {
        return finalLowerBand;
    }

    public Decimal getSuperTrend() {
        return superTrend;
    }

    public String getBuySell() {
        return buySell;
    }

    public Bar getBar() {
        return bar;
    }

    @Override
    public String toString() {
        return "SuperTradeIndicator{" +
                "smaSize=" + smaSize +
                ", instrument=" + instrument +
                ", bandSize=" + bandSize +
                ", isInit=" + isInit +
                ", initTrueRangeList=" + initTrueRangeList +
                ", previousBar=" + previousBar +
                ", previousFUB=" + previousFUB +
                ", previousFLB=" + previousFLB +
                ", previousEMA=" + previousEMA +
                ", trueRange=" + trueRange +
                ", exponentialMovingAverage=" + exponentialMovingAverage +
                ", basicUpperBand=" + basicUpperBand +
                ", basicLowerBand=" + basicLowerBand +
                ", finalUpperBand=" + finalUpperBand +
                ", finalLowerBand=" + finalLowerBand +
                ", superTrend=" + superTrend +
                ", buySell='" + buySell + '\'' +
                ", bar=" + bar +
                '}';
    }
}
