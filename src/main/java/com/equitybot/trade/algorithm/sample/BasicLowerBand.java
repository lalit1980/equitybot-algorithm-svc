package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;
@Service
public class BasicLowerBand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
   
    private List<Decimal> basicLowerBandList;
    private int bandSize;
    private int range;

    public BasicLowerBand() {
        this.basicLowerBandList = new LinkedList<>();
    }

    public void buildBasicLowerBand(TimeSeries timeSeries, AverageTrueRange averageTrueRange, int bandSize) {
        logger.info(" * Building Basic Lower Band ");
        this.bandSize = bandSize;
        this.range = averageTrueRange.getRange();
        Decimal basicLowerBand = Decimal.valueOf(0);
        Bar bar;
        for (int index = 0; index < averageTrueRange.getRange(); index++) {
            basicLowerBandList.add(basicLowerBand);
            logger.info(basicLowerBand.toString());
        }

        for (int index = averageTrueRange.getRange(); index < timeSeries.getBarData().size(); index++) {
            bar = timeSeries.getBar(index);
            basicLowerBand = bar.getMaxPrice().plus(bar.getMinPrice()).dividedBy(2)
                    .minus(averageTrueRange.getAverageTrueRangeList().get(index).multipliedBy(this.bandSize));
            this.basicLowerBandList.add(basicLowerBand);
            logger.info(basicLowerBand.toString());
        }
    }

    public void addNewBasicLowerBand(TimeSeries updatedTimeSeries, AverageTrueRange updatedAverageTrueRange) {
        logger.info(" * adding new Basic Lower Band");
        Bar bar = updatedTimeSeries.getBar(updatedTimeSeries.getBarData().size()-1);
        Decimal basicLowerBand = bar.getMaxPrice().plus(bar.getMinPrice()).dividedBy(2)
                .minus(updatedAverageTrueRange.getAverageTrueRangeList().
                        get(updatedAverageTrueRange.getAverageTrueRangeList().size()-1).multipliedBy(this.bandSize));
        this.basicLowerBandList.add(basicLowerBand);
        logger.info(basicLowerBand.toString());
    }

	public List<Decimal> getBasicLowerBandList() {
		return basicLowerBandList;
	}

	public int getBandSize() {
		return bandSize;
	}

	public int getRange() {
		return range;
	}

}
