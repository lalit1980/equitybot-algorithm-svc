package com.equitybot.trade.algorithm.strategy;

import com.equitybot.trade.algorithm.constants.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import java.util.LinkedList;
import java.util.List;

public class SuperTrendAnalyzer extends BaseIndicator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int bandSize;
    private final int smaSize;
    private long instrument;
    private boolean isInit;
    private List<Decimal> initTrueRangeList;
    private Bar previousBar;
    private Decimal previousFUB;
    private Decimal previousFLB;
    private Decimal previousEMA;
    private boolean havebuy;
    private Bar lastBuyBar;
    private Decimal totalProfitLoss;
    private AlgorithmDataLoger algorithmDataLoger;

    public SuperTrendAnalyzer(int bandSize, int smaSize, long instrument, AlgorithmDataLoger algorithmDataLoger) {
        this.bandSize = bandSize;
        this.smaSize = smaSize;
        this.instrument = instrument;
        this.isInit = false;
        this.initTrueRangeList = new LinkedList<>();
        this.previousFUB = Decimal.ZERO;
        this.previousFLB = Decimal.ZERO;
        this.previousEMA = Decimal.ZERO;
        this.havebuy = false;
        this.totalProfitLoss = Decimal.ZERO;
        this.algorithmDataLoger = algorithmDataLoger;
    }

    public Decimal analysis(Bar workingBar) {
        if (isInit) {
            calculate(workingBar);
        } else {
            init(workingBar);
        }
        this.previousBar = workingBar;
        return null;
    }

    private Decimal calculate(Bar workingBar) {
        Decimal workingTrueRange = calculateTrueRange(this.previousBar, workingBar);
        Decimal workingEMA = calculateEMA(this.previousEMA, workingTrueRange);
        Decimal workingBUB = calculateBasicUpperBand(workingBar, workingEMA, this.bandSize);
        Decimal workingBLB = calculateBasicLowerBand(workingBar, workingEMA, this.bandSize);
        Decimal workingFUB = calculateFinalUpperBand(workingBUB, this.previousFUB, this.previousBar);
        Decimal workingFLB = calculateFinalLowerBand(workingBLB, this.previousFLB, this.previousBar);
        Decimal workingSuperTrend = calculateSuperTrend(workingBar, workingFUB, workingFLB);
        String buySell = calculateBuySell(workingBar, workingSuperTrend);
        updatePreviousValue(workingFUB, workingFLB, workingEMA);
        analyze(workingBar, workingTrueRange, workingEMA, workingBUB, workingBLB, workingFUB, workingFLB,
                workingSuperTrend, buySell);
        return workingSuperTrend;
    }

    private Decimal init(Bar workingBar) {
        Decimal workingTrueRange;
        Decimal workingSMA;
        Decimal workingBUB;
        Decimal workingBLB;
        Decimal workingFUB;
        Decimal workingFLB;
        Decimal workingSuperTrend = Decimal.ZERO;
        if (this.previousBar != null) {
            workingTrueRange = calculateTrueRange(this.previousBar, workingBar);
            this.initTrueRangeList.add(workingTrueRange);
        }
        if (this.initTrueRangeList.size() == this.smaSize) {
            workingSMA = calculateSMA(this.initTrueRangeList);
            workingBUB = calculateBasicUpperBand(workingBar, workingSMA, this.bandSize);
            workingBLB = calculateBasicLowerBand(workingBar, workingSMA, this.bandSize);
            workingFUB = calculateFinalUpperBand(workingBUB, this.previousFUB, this.previousBar);
            workingFLB = calculateFinalLowerBand(workingBLB, this.previousFLB, this.previousBar);
            workingSuperTrend = calculateSuperTrend(workingBar, workingFUB, workingFLB);
            this.isInit = true;
            updatePreviousValue(workingFUB, workingFLB, workingSMA);
        }
        return workingSuperTrend;
    }

    private void updatePreviousValue(Decimal workingFUB, Decimal workingFLB, Decimal workingEMA) {
        this.previousFLB = workingFLB;
        this.previousFUB = workingFUB;
        this.previousEMA = workingEMA;
    }

    public void analyze(Bar workingBar, Decimal workingTrueRange, Decimal workingEMA, Decimal workingBUB,
                        Decimal workingBLB, Decimal workingFUB, Decimal workingFLB, Decimal workingSuperTrend, String buySell) {

        algorithmDataLoger.logData(workingBar, this.getInstrument(), workingTrueRange, workingEMA, workingBUB,
                workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell);
        if (buySell != null && this.havebuy && Constant.SELL.equals(buySell)) {
            Decimal currentProfitLoss = workingBar.getClosePrice().minus(this.lastBuyBar.getClosePrice());
            this.totalProfitLoss = this.totalProfitLoss.plus(currentProfitLoss);
            this.algorithmDataLoger.logActionLogData(workingBar, this.getInstrument(), workingTrueRange, workingEMA,
                    workingBUB, workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell, currentProfitLoss,
                    this.totalProfitLoss);
            this.algorithmDataLoger.logProfitLossRepository(this.getInstrument(), this.totalProfitLoss.doubleValue());
            this.havebuy = false;

        } else if (buySell != null && !this.havebuy && Constant.BUY.equals(buySell)) {
            this.havebuy = true;
            this.lastBuyBar = workingBar;
            this.algorithmDataLoger.logActionLogData(workingBar, this.getInstrument(), workingTrueRange, workingEMA,
                    workingBUB, workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell, Decimal.ZERO,
                    Decimal.ZERO);
        }

    }

    public long getInstrument() {
        return this.instrument;
    }

}
