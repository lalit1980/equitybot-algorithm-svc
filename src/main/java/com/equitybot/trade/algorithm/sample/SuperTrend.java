package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;

public class SuperTrend {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Decimal> superTrendList;
    private int bandSize;
    private int range;

    public SuperTrend() {
        this.superTrendList = new LinkedList<>();
    }

    public void buildSuperTrend(TimeSeries timeSeries, FinalLowerBand finalLowerBand, FinalUpperBand finalUpperBand) {
        logger.info(" * Building Super Trend");
        this.bandSize = finalLowerBand.getBandSize();
        this.range = finalLowerBand.getRange();
        Decimal superTrend = Decimal.valueOf(0);
        for (int index = 0; index < finalLowerBand.getRange(); index++) {
            this.superTrendList.add(superTrend);
            logger.info(superTrend.toString());
        }
        for (int index = finalLowerBand.getRange(); index < timeSeries.getBarData().size(); index++) {
            if (timeSeries.getBarData().get(index).getClosePrice()
                    .isLessThanOrEqual(finalUpperBand.getFinalUpperBandList().get(index))) {
                superTrend = finalUpperBand.getFinalUpperBandList().get(index);
            } else {
                superTrend = finalLowerBand.getFinalLowerBandList().get(index);
            }
            this.superTrendList.add(superTrend);
            logger.info(superTrend.toString());
        }
    }

    public void addNewSuperTrend(TimeSeries timeSeries, FinalLowerBand finalLowerBand, FinalUpperBand finalUpperBand){
        logger.info(" * adding new Super Trend");
        Decimal superTrend;
        if (timeSeries.getBarData().get(timeSeries.getBarData().size()-1).getClosePrice()
                .isLessThanOrEqual(finalUpperBand.getFinalUpperBandList().get(timeSeries.getBarData().size()-1))) {
            superTrend = finalUpperBand.getFinalUpperBandList().get(timeSeries.getBarData().size()-1);
        } else {
            superTrend = finalLowerBand.getFinalLowerBandList().get(timeSeries.getBarData().size()-1);
        }
        this.superTrendList.add(superTrend);
        logger.info(superTrend.toString());
    }

	public List<Decimal> getSuperTrendList() {
		return superTrendList;
	}

	public int getBandSize() {
		return bandSize;
	}

	public int getRange() {
		return range;
	}
}
