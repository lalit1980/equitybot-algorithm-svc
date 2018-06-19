package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;

public class BasicUpperBand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Decimal> basicUpperBandList;
    private int bandSize;
    private int range;

    public BasicUpperBand() {
        this.basicUpperBandList = new LinkedList<>();
    }

    public void buildBasicUpperBand(TimeSeries timeSeries, AverageTrueRange averageTrueRange, int bandSize) {
        logger.info(" * Building Basic Upper Band");
        this.bandSize = bandSize;
        this.range = averageTrueRange.getRange();
        Decimal basicUpperBand = Decimal.valueOf(0);
        Bar bar;
        for (int index = 0; index < averageTrueRange.getRange(); index++) {
            basicUpperBandList.add(basicUpperBand);
            logger.info(basicUpperBand.toString());
        }
        for (int index = averageTrueRange.getRange() ; index < timeSeries.getBarData().size(); index++) {
            bar = timeSeries.getBar(index);
            basicUpperBand = bar.getMaxPrice().plus(bar.getMinPrice()).dividedBy(2)
                    .plus(averageTrueRange.getAverageTrueRangeList().get(index).multipliedBy(this.bandSize));
            this.basicUpperBandList.add(basicUpperBand);
            logger.info(basicUpperBand.toString());
        }
    }

    public void addNewBasicUpperBand(TimeSeries updatedTimeSeries, AverageTrueRange updatedAverageTrueRange){
        logger.info(" * adding new Basic Upper Band");
        Bar bar = updatedTimeSeries.getBar(updatedTimeSeries.getBarData().size()-1);
        Decimal basicUpperBand = bar.getMaxPrice().plus(bar.getMinPrice()).dividedBy(2)
                .plus(updatedAverageTrueRange.getAverageTrueRangeList()
                        .get(updatedAverageTrueRange.getAverageTrueRangeList().size()-1).multipliedBy(this.bandSize));
        this.basicUpperBandList.add(basicUpperBand);
        logger.info(basicUpperBand.toString());
    }

	public List<Decimal> getBasicUpperBandList() {
		return basicUpperBandList;
	}

	public int getBandSize() {
		return bandSize;
	}

	public int getRange() {
		return range;
	}


}
