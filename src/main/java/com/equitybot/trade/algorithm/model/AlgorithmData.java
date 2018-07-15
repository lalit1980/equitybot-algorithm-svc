package com.equitybot.trade.algorithm.model;

import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;


public class AlgorithmData {

    private Bar bar;
    private Decimal trueRange;
    private Decimal exponentialMovingAverage;
    private Decimal basicUpperBand;
    private Decimal basicLowerBand;
    private Decimal finalUpperBand;
    private Decimal finalLowerBand;
    private Decimal superTrend;

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Decimal getExponentialMovingAverage() {
        return exponentialMovingAverage;
    }

    public void setExponentialMovingAverage(Decimal exponentialMovingAverage) {
        this.exponentialMovingAverage = exponentialMovingAverage;
    }

    public Decimal getBasicUpperBand() {
        return basicUpperBand;
    }

    public void setBasicUpperBand(Decimal basicUpperBand) {
        this.basicUpperBand = basicUpperBand;
    }

    public Decimal getBasicLowerBand() {
        return basicLowerBand;
    }

    public void setBasicLowerBand(Decimal basicLowerBand) {
        this.basicLowerBand = basicLowerBand;
    }

    public Decimal getFinalUpperBand() {
        return finalUpperBand;
    }

    public void setFinalUpperBand(Decimal finalUpperBand) {
        this.finalUpperBand = finalUpperBand;
    }

    public Decimal getFinalLowerBand() {
        return finalLowerBand;
    }

    public void setFinalLowerBand(Decimal finalLowerBand) {
        this.finalLowerBand = finalLowerBand;
    }

    public Decimal getSuperTrend() {
        return superTrend;
    }

    public void setSuperTrend(Decimal superTrend) {
        this.superTrend = superTrend;
    }

    public Decimal getTrueRange() {
        return trueRange;
    }

    public void setTrueRange(Decimal trueRange) {
        this.trueRange = trueRange;
    }

    @Override
    public String toString() {
        return "AlgorithmData{" +
                "bar=" + bar +
                ", exponentialMovingAverage=" + exponentialMovingAverage +
                ", basicUpperBand=" + basicUpperBand +
                ", basicLowerBand=" + basicLowerBand +
                ", finalUpperBand=" + finalUpperBand +
                ", finalLowerBand=" + finalLowerBand +
                ", superTrend=" + superTrend +
                ", trueRange=" + trueRange +
                '}';
    }
}
