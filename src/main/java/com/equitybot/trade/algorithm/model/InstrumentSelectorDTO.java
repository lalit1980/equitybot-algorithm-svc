package com.equitybot.trade.algorithm.model;

import java.io.Serializable;

public class InstrumentSelectorDTO implements Serializable {

    private boolean eligibleInstrument;
    private double instrumentProfit;
    private double instrumentClosePrice;
    private double totalStockSize;
    private Long expectedQuantity;
    private double averageBuyPrice;

    public boolean isEligibleInstrument() {
        return eligibleInstrument;
    }

    public void setEligibleInstrument(boolean eligibleInstrument) {
        this.eligibleInstrument = eligibleInstrument;
    }

    public double getInstrumentProfit() {
        return instrumentProfit;
    }

    public void setInstrumentProfit(double instrumentProfit) {
        this.instrumentProfit = instrumentProfit;
    }

    public double getInstrumentClosePrice() {
        return instrumentClosePrice;
    }

    public void setInstrumentClosePrice(double instrumentClosePrice) {
        this.instrumentClosePrice = instrumentClosePrice;
    }

    public double getTotalStockSize() {
        return totalStockSize;
    }

    public void setTotalStockSize(double totalStockSize) {
        this.totalStockSize = totalStockSize;
    }

    public Long getExpectedQuantity() {
        return expectedQuantity;
    }

    public void setExpectedQuantity(Long expectedQuantity) {
        this.expectedQuantity = expectedQuantity;
    }

    public double getAverageBuyPrice() {
        return averageBuyPrice;
    }

    public void setAverageBuyPrice(double averageBuyPrice) {
        this.averageBuyPrice = averageBuyPrice;
    }

    @Override
    public String toString() {
        return "InstrumentSelectorDTO{" +
                "eligibleInstrument=" + eligibleInstrument +
                ", instrumentProfit=" + instrumentProfit +
                ", instrumentClosePrice=" + instrumentClosePrice +
                ", totalStockSize=" + totalStockSize +
                ", expectedQuantity=" + expectedQuantity +
                ", averageBuyPrice=" + averageBuyPrice +
                '}';
    }
}
