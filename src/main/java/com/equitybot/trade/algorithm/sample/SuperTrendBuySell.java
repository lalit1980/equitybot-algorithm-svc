package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;

public class SuperTrendBuySell {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<String> SuperTrendBuySellList;

    public SuperTrendBuySell() {
        this.SuperTrendBuySellList = new LinkedList<>();
    }

    public void buildSuperTrendBuySell(TimeSeries timeSeries, SuperTrend superTrend) {
        logger.info(" * Building Super Trend BuyS Sell");
        String superTrendBuySell = "";
        for (int index = 0; index < superTrend.getRange(); index++) {
            this.SuperTrendBuySellList.add(superTrendBuySell);
            logger.info(superTrendBuySell);
        }
        for (int index = superTrend.getRange(); index < superTrend.getSuperTrendList().size();
             index++) {
            if (timeSeries.getBarData().get(index).getClosePrice().isLessThan(superTrend.getSuperTrendList().get(index))) {
                superTrendBuySell = "BUY";
            } else {
                superTrendBuySell = "SELL";
            }
            this.SuperTrendBuySellList.add(superTrendBuySell);
            logger.info(superTrendBuySell);
        }
    }

    public void addNewSuperTrendBuySell(TimeSeries timeSeries, SuperTrend superTrend) {
        logger.info(" * Adding new Super Trend BuyS Sell");
        String superTrendBuySell;
        if (timeSeries.getBarData().get(timeSeries.getBarData().size() - 1).getClosePrice()
                .isLessThan(superTrend.getSuperTrendList().get(timeSeries.getBarData().size() - 1))) {
            superTrendBuySell = "BUY";
        } else {
            superTrendBuySell = "SELL";
        }
        this.SuperTrendBuySellList.add(superTrendBuySell);
        logger.info(superTrendBuySell);
    }

	public List<String> getSuperTrendBuySellList() {
		return SuperTrendBuySellList;
	}
}
