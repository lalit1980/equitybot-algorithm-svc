package com.equitybot.trade.algorithm.indicator;


import com.equitybot.trade.algorithm.constants.Constant;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

public class SuperTrendAnalyzer extends BaseIndicator {

    private boolean haveBuy;
    private Bar lastBuyBar;
    private Decimal totalProfitLoss;
    private SuperTradeIndicator superTradeIndicator;
    private Decimal currentProfitLoss;
    private byte action;
    private long barCount;

    public SuperTrendAnalyzer(int multiplier, int period, long instrument) {
        this.superTradeIndicator = new SuperTradeIndicator(multiplier, period, instrument);
        this.haveBuy = false;
        this.totalProfitLoss = Decimal.ZERO;
        action = 0;
    }

    public void analyze(Bar bar) {
        this.superTradeIndicator.calculate(bar);
        calculateProfitLoss();
        barCount++;
    }

    private void calculateProfitLoss() {
        if (this.superTradeIndicator.getBuySell() != null && this.haveBuy && Constant.SELL.equals(this.superTradeIndicator.getBuySell())) {
            sell();
            this.action = -1;
        } else if (this.superTradeIndicator.getBuySell() != null && !this.haveBuy && Constant.BUY.equals(this.superTradeIndicator.getBuySell())) {
            buy();
            this.action = 1;
        } else {
            this.action = 0;
        }
    }

    private void buy() {
        this.haveBuy = true;
        this.lastBuyBar = this.superTradeIndicator.getBar();
        this.currentProfitLoss = Decimal.ZERO;
    }

    private void sell() {
        this.currentProfitLoss = this.superTradeIndicator.getBar().getClosePrice().minus(this.lastBuyBar.getClosePrice());
        this.totalProfitLoss = this.totalProfitLoss.plus(currentProfitLoss);
        this.haveBuy = false;
    }

    public Decimal getTotalProfitLoss() {
        return totalProfitLoss;
    }

    public SuperTradeIndicator getSuperTradeIndicator() {
        return superTradeIndicator;
    }

    public Decimal getCurrentProfitLoss() {
        return currentProfitLoss;
    }

    public byte getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "SuperTrendAnalyzer{" +
                "haveBuy=" + haveBuy +
                ", lastBuyBar=" + lastBuyBar +
                ", totalProfitLoss=" + totalProfitLoss +
                ", superTradeIndicator=" + superTradeIndicator +
                ", currentProfitLoss=" + currentProfitLoss +
                ", action=" + action +
                ", barCount=" + barCount +
                '}';
    }
}
