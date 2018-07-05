package com.equitybot.trade.algorithm.strategy;



import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.bo.ActionLogData;
import com.equitybot.trade.algorithm.bo.LogData;
import com.equitybot.trade.algorithm.constants.Constant;
import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.LogDataRepository;

public class SuperTrendAnalyzer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Decimal smoothingConstant;
    private int bandSize;
    private final int smaSize;
    private long instrument;

    private boolean isInit;
    private List<Decimal> initTrueRangeList;

    private Bar previousBar;
    private Decimal previousFUB;
    private Decimal previousFLB;
    private Decimal previousEMA;
    private boolean haveBye;

    private LogDataRepository repo;
    private ActionLogDataRepository actionRepo;
    
    private static boolean isDBClen = false;
   

    public SuperTrendAnalyzer(int bandSize, int smaSize, long instrument, LogDataRepository repo, ActionLogDataRepository actionRepo) {
        this.bandSize = bandSize;
        this.smaSize = smaSize;
        this.instrument = instrument;
        this.isInit = false;
        this.initTrueRangeList = new LinkedList<>();
        this.previousFUB = Decimal.ZERO;
        this.previousFLB = Decimal.ZERO;
        this.previousEMA = Decimal.ZERO;
        this.repo = repo;
        this.haveBye = false;
        this.actionRepo = actionRepo;
    }

    public Decimal analysis(Bar workingBar) throws IOException {
        if (isInit) {
            calculate(workingBar);
        } else {
            init(workingBar);
        }
        this.previousBar = workingBar;
        return null;
    }

    public Decimal calculate(Bar workingBar) throws IOException {
        Decimal workingTrueRange = calculateTrueRange(this.previousBar, workingBar);
        Decimal workingEMA = calculateEMA(this.previousEMA, workingTrueRange);
        Decimal workingBUB = calculateBasicUpperBand(workingBar, workingEMA, this.bandSize);
        Decimal workingBLB = calculateBasicLowerBand(workingBar, workingEMA, this.bandSize);
        Decimal workingFUB = calculateFinalUpperBand(workingBUB, this.previousFUB, this.previousBar);
        Decimal workingFLB = calculateFinalLowerBand(workingBLB, this.previousFLB, this.previousBar);
        Decimal workingSuperTrend = calculateSuperTrend(workingBar, workingFUB, workingFLB);
        String buySell = calculateBuySell(workingBar, workingSuperTrend);
        updatePreviousValue(workingFUB, workingFLB, workingEMA);
        logData(workingBar, workingTrueRange, workingEMA, workingBUB,
                workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell);
        return workingSuperTrend;
    }

    public Decimal init(Bar workingBar) throws IOException {
        Decimal workingTrueRange = Decimal.ZERO;
        Decimal workingSMA = Decimal.ZERO;
        Decimal workingBUB = Decimal.ZERO;
        Decimal workingBLB = Decimal.ZERO;
        Decimal workingFUB = Decimal.ZERO;
        Decimal workingFLB = Decimal.ZERO;
        Decimal workingSuperTrend = Decimal.ZERO;
        String buySell = "";
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
            buySell = calculateBuySell(workingBar, workingSuperTrend);
            this.isInit = true;
            updatePreviousValue(workingFUB, workingFLB, workingSMA);
        }
        logData(workingBar, workingTrueRange, workingSMA, workingBUB,
                workingBLB, workingFUB, workingFLB, workingSuperTrend, buySell);
        return workingSuperTrend;
    }

    public void updatePreviousValue(Decimal workingFUB, Decimal workingFLB, Decimal workingEMA) {
        this.previousFLB = workingFLB;
        this.previousFUB = workingFUB;
        this.previousEMA = workingEMA;
    }

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

    public void logData(Bar workingBar, Decimal workingTrueRange, Decimal workingEMA, Decimal workingBUB,
                        Decimal workingBLB, Decimal workingFUB, Decimal workingFLB, Decimal workingSuperTrend,
                        String buySell) throws IOException {
      
       LogData data=new LogData();
       data.setInstrumentToken(this.getInstrument());
       data.setOpen(workingBar.getOpenPrice().doubleValue());
       data.setHigh(workingBar.getMaxPrice().doubleValue());
       data.setLow(workingBar.getMinPrice().doubleValue());
       data.setClose(workingBar.getClosePrice().doubleValue());
       data.setTrueRange(workingTrueRange.doubleValue());
       data.setEma(workingEMA.doubleValue());
       data.setBasicUpperBand(workingBUB.doubleValue());
       data.setBasicLowerBand(workingBLB.doubleValue());
       data.setFinalUpperBand(workingFUB.doubleValue());
       data.setFinalLowerBand(workingFLB.doubleValue());
       data.setSuperTrend(workingSuperTrend.doubleValue());
       data.setSignal(buySell);
       data.setId(UUID.randomUUID().toString());
       data.setTransactionTime(new Date());
       logger.info(data.toString());
        repo.saveTickData(data);
        if(buySell != null && this.haveBye && Constant.SELL.equals(buySell)) {
        	actionRepo.saveActionLogData(new ActionLogData(data));
        	this.haveBye = false;
        	
        }else if( buySell != null && !this.haveBye && Constant.BUY.equals(buySell)){
        	actionRepo.saveActionLogData(new ActionLogData(data));
        	this.haveBye = true;
        }
       
    }

    public long getInstrument() {
        return instrument;
    }

}
