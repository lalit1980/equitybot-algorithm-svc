package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;

public class FinalUpperBand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Decimal> finalUpperBandList;
    private int bandSize;
    private int range;

    public FinalUpperBand() {
        this.finalUpperBandList = new LinkedList<>();
    }

    public void buildFinalUpperBand(TimeSeries timeSeries, BasicUpperBand basicUpperBand) {
        logger.info(" * Building Final Upper Band");
        this.bandSize = basicUpperBand.getBandSize();
        this.range = basicUpperBand.getRange();
        Decimal finalUpperBand = Decimal.valueOf(0);
        for (int index = 0; index < basicUpperBand.getRange(); index++) {
            this.finalUpperBandList.add(finalUpperBand);
            logger.info(finalUpperBand.toString());
        }
        Decimal basicUpperBandValue;
        Decimal previousFinalUpperBand;
        for (int index = basicUpperBand.getRange(); index < basicUpperBand.getBasicUpperBandList().size();
             index++) {
            basicUpperBandValue = basicUpperBand.getBasicUpperBandList().get(index);
            previousFinalUpperBand = this.finalUpperBandList.get(index - 1);
            if (basicUpperBandValue.isLessThan(previousFinalUpperBand) ||
                    timeSeries.getBarData().get(index - 1).getClosePrice().isGreaterThan(previousFinalUpperBand)) {
                finalUpperBand = basicUpperBandValue;
            } else {
                finalUpperBand = previousFinalUpperBand;
            }
            this.finalUpperBandList.add(finalUpperBand);
            logger.info(finalUpperBand.toString());
        }
    }

    public void addNewFinalUpperBand(TimeSeries newTimeSeries, BasicUpperBand newBasicUpperBand) {
        logger.info(" * Adding new Final Upper Band");
        Decimal basicUpperBandValue = newBasicUpperBand.getBasicUpperBandList()
                .get(newBasicUpperBand.getBasicUpperBandList().size()-1);
        Decimal previousFinalUpperBand = this.finalUpperBandList.get(this.finalUpperBandList.size() - 1);
        Decimal finalUpperBand;
        if (basicUpperBandValue.isLessThan(previousFinalUpperBand) || newTimeSeries.getBarData()
                .get(newTimeSeries.getBarData().size() - 2).getClosePrice().isGreaterThan(previousFinalUpperBand)) {
            finalUpperBand = basicUpperBandValue;
        } else {
            finalUpperBand = previousFinalUpperBand;
        }
        this.finalUpperBandList.add(finalUpperBand);
        logger.info(finalUpperBand.toString());
    }

	public List<Decimal> getFinalUpperBandList() {
		return finalUpperBandList;
	}

	public int getBandSize() {
		return bandSize;
	}

	public int getRange() {
		return range;
	}
}
