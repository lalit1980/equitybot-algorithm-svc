package com.equitybot.trade.algorithm.model;

import com.equitybot.trade.algorithm.strategy.BaseIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import java.util.LinkedList;
import java.util.List;

public class AlgorithmPipelineData {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private long instrument;
    private int bandSize;
    private int smaSize;
    private AlgorithmData algorithmData;
    private AlgorithmData previousAlgorithmData;
    private BaseIndicator baseIndicator;

    private boolean eligibleForEma;
    private List<Decimal> initTrueRangeList;

    public void processComplite(){
        this.previousAlgorithmData = algorithmData;
        this.algorithmData = new AlgorithmData();
    }

    public void processStart(Bar bar){
        this.algorithmData.setBar(bar);
    }


    public AlgorithmPipelineData(long instrument, int bandSize, int smaSize) {
        this.algorithmData = new AlgorithmData();
        this.instrument = instrument;
        this.bandSize = bandSize;
        this.smaSize = smaSize;
        this.eligibleForEma = false;
        this.initTrueRangeList = new LinkedList<>();
        this.algorithmData = new AlgorithmData();
        this.baseIndicator = new BaseIndicator();
    }

    public Bar getBar() {
        return this.algorithmData.getBar();
    }

    public Decimal getTrueRange() {
        Decimal trueRange = this.algorithmData.getTrueRange();
        if (trueRange == null ){
            if( this.previousAlgorithmData != null) {
                trueRange = this.baseIndicator.calculateTrueRange(this.previousAlgorithmData.getBar(), this.algorithmData.getBar());
            }else{
                trueRange = Decimal.ZERO;
            }
            this.algorithmData.setTrueRange(trueRange);
        }
        if(!this.eligibleForEma){
            this.initTrueRangeList.add(trueRange);
            if (this.initTrueRangeList.size() == this.smaSize+1){
                this.algorithmData.setExponentialMovingAverage(this.baseIndicator.calculateSMA(this.initTrueRangeList));
                this.previousAlgorithmData.setFinalUpperBand(Decimal.ZERO);
                this.previousAlgorithmData.setFinalLowerBand(Decimal.ZERO);
                this.eligibleForEma = true;
            }
        }
        return trueRange;
    }


    public Decimal getExponentialMovingAverage() {
        Decimal exponentialMovingAverage = this.algorithmData.getExponentialMovingAverage();
        if (exponentialMovingAverage == null && this.eligibleForEma){
            exponentialMovingAverage = this.baseIndicator.calculateEMA( this.previousAlgorithmData.getExponentialMovingAverage(), getTrueRange());
            this.algorithmData.setExponentialMovingAverage(exponentialMovingAverage);
        }
        return exponentialMovingAverage;
    }


    public Decimal getBasicUpperBand() {
        Decimal basicUpperBand = this.algorithmData.getBasicUpperBand();
        if(basicUpperBand == null  && this.eligibleForEma){
            basicUpperBand = this.baseIndicator.calculateBasicUpperBand(getBar(),getExponentialMovingAverage(),this.bandSize);
            this.algorithmData.setBasicUpperBand(basicUpperBand);
        }
        return basicUpperBand;
    }


    public Decimal getBasicLowerBand() {
        Decimal basicLowerBand = this.algorithmData.getBasicLowerBand();
        if(basicLowerBand == null  && this.eligibleForEma){
            basicLowerBand = this.baseIndicator.calculateBasicLowerBand(getBar(),getExponentialMovingAverage(),this.bandSize);
            this.algorithmData.setBasicLowerBand(basicLowerBand);
        }
            return basicLowerBand;
    }


    public Decimal getFinalUpperBand() {
        Decimal finalUpperBand = this.algorithmData.getFinalUpperBand();
        if(finalUpperBand == null  && this.eligibleForEma){
            finalUpperBand = this.baseIndicator.calculateFinalUpperBand(getBasicUpperBand(),this.previousAlgorithmData.getFinalUpperBand(),this.previousAlgorithmData.getBar());
            this.algorithmData.setFinalUpperBand(finalUpperBand);
        }
            return finalUpperBand;
    }


    public Decimal getFinalLowerBand() {
        Decimal finalLowerBand = this.algorithmData.getFinalLowerBand();
        if(finalLowerBand == null  && this.eligibleForEma){
            finalLowerBand = this.baseIndicator.calculateFinalLowerBand(getBasicLowerBand(), this.previousAlgorithmData.getFinalLowerBand(),this.previousAlgorithmData.getBar());
            this.algorithmData.setFinalLowerBand(finalLowerBand);
        }
            return finalLowerBand;
    }


    public Decimal getSuperTrend() {
        Decimal superTrend = this.algorithmData.getSuperTrend();
        if(superTrend == null  && this.eligibleForEma){
            superTrend = this.baseIndicator.calculateSuperTrend(getBar(),getFinalUpperBand(),getBasicLowerBand());
        }
            return superTrend;
    }

    public long getInstrument() {
        return instrument;
    }
}
