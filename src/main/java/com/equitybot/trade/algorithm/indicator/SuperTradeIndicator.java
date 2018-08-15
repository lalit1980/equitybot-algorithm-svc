package com.equitybot.trade.algorithm.indicator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import java.util.LinkedList;
import java.util.List;

public class SuperTradeIndicator extends BaseIndicator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int period;
    private long instrument;
    private int multiplier;
    private boolean isInit;
    private List<Decimal> initTrueRangeList;
    private Bar previousBar;
    private Decimal previousFUB;
    private Decimal previousFLB;
    private Decimal previousATR;
    private Decimal previousST;

    private Decimal trueRange;
    private Decimal averageTrueRange;
    private Decimal basicUpperBand;
    private Decimal basicLowerBand;
    private Decimal finalUpperBand;
    private Decimal finalLowerBand;
    private Decimal superTrend;
    private String buySell;
    private Bar bar;


    public SuperTradeIndicator(int multiplier, int period, long instrument) {
        this.multiplier = multiplier;
        this.period = period;
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
         this.initTrueRangeList.remove(0);
         this.initTrueRangeList.add(this.trueRange);
         this.averageTrueRange = calculateEMA(this.previousATR, this.trueRange,
                 smoothingConstant(this.period));
         this.basicUpperBand = calculateBasicUpperBand(this.bar,
                 this.averageTrueRange, this.multiplier);
         this.basicLowerBand = calculateBasicLowerBand(this.bar,
                 this.averageTrueRange, this.multiplier);
         this.finalUpperBand = calculateFinalUpperBand(this.basicUpperBand,
                 this.previousFUB, this.previousBar);
         this.finalLowerBand = calculateFinalLowerBand(this.basicLowerBand,
                 this.previousFLB, this.previousBar);
         this.superTrend = calculateSuperTrend(this.bar, this.finalUpperBand, this.finalLowerBand, this.previousFLB,
                 this.previousST, this.previousFUB);
         this.buySell = calculateBuySell(this.bar, this.superTrend);
         return this.superTrend;
    }

    private Decimal init() {
    	this.trueRange = Decimal.ZERO;
        this.averageTrueRange = Decimal.ZERO;
        this.basicUpperBand = Decimal.ZERO;
        this.basicLowerBand = Decimal.ZERO;
        this.finalUpperBand = Decimal.ZERO;
        this.finalLowerBand = Decimal.ZERO;
        this.superTrend = Decimal.ZERO;

        if (this.previousBar != null) {
            this.trueRange = calculateTrueRange(this.previousBar, this.bar);
            this.initTrueRangeList.add(this.trueRange);
        }
        if (this.initTrueRangeList.size() == this.period) {
            this.averageTrueRange = calculateSMA(this.initTrueRangeList);
            this.basicUpperBand = calculateBasicUpperBand(this.bar,
                    this.averageTrueRange, this.multiplier);
            this.basicLowerBand = calculateBasicLowerBand(this.bar,
                    this.averageTrueRange, this.multiplier);
            this.finalUpperBand = this.basicUpperBand;
            this.finalLowerBand = this.basicLowerBand;
            this.superTrend = calculateSuperTrend(this.bar, this.finalUpperBand,
                    this.finalLowerBand, this.previousFLB, this.previousST, this.previousFUB);
            this.buySell = calculateBuySell(this.bar, this.superTrend);
            this.isInit = true;
        }
        return this.superTrend;
    }

    private void updateValues(Bar workingBar) {
        this.previousFLB = this.finalLowerBand;
        this.previousFUB = this.finalUpperBand;
        this.previousATR = this.averageTrueRange;
        this.previousBar = this.bar;
        this.previousST = this.superTrend;
        this.bar = workingBar;
    }

    public long getInstrument() {
        return this.instrument;
    }

    public Decimal getTrueRange() {
        return trueRange;
    }

    public Decimal getAverageTrueRange() {
        return averageTrueRange;
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
                "period=" + period +
                ", instrument=" + instrument +
                ", multiplier=" + multiplier +
                ", isInit=" + isInit +
                ", initTrueRangeList=" + initTrueRangeList +
                ", previousBar=" + previousBar +
                ", previousFUB=" + previousFUB +
                ", previousFLB=" + previousFLB +
                ", previousATR=" + previousATR +
                ", trueRange=" + trueRange +
                ", averageTrueRange=" + averageTrueRange +
                ", basicUpperBand=" + basicUpperBand +
                ", basicLowerBand=" + basicLowerBand +
                ", finalUpperBand=" + finalUpperBand +
                ", finalLowerBand=" + finalLowerBand +
                ", superTrend=" + superTrend +
                ", buySell='" + buySell + '\'' +
                ", bar=" + bar +
                '}';
    }
    
    private static Decimal smoothingConstant(int period) {
        return Decimal.ONE.dividedBy(period);
    }
}
