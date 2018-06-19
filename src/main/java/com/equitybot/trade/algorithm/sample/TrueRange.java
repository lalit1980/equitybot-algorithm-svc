package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;

public class TrueRange {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Decimal>trueRangeList;

    public TrueRange(){
        this.trueRangeList = new LinkedList<>();
    }

    public void buildTrueRange(TimeSeries timeSeries) {
        logger.info(" * Building TrueRange  ");
        Decimal trueRange = Decimal.valueOf(0);
        List<Bar> bars = timeSeries.getBarData();
        this.trueRangeList.add(trueRange);
        logger.info(trueRange.toString());
        for(int index = 1 ; index < bars.size() ; index++){
            trueRange = bars.get(index).getMaxPrice().minus( bars.get(index).getMinPrice())
                    .max( (bars.get(index).getMaxPrice().minus(bars.get(index-1).getClosePrice()).abs()))
                    .max((bars.get(index).getMinPrice().minus(bars.get(index-1).getClosePrice()).abs()));
            this.trueRangeList.add(trueRange);
            logger.info(trueRange.toString());
        }
    }

    public void addNewTrueRange(Bar newBar, Bar previousBar){
        logger.info(" * added new True Range  ");
        Decimal trueRange = newBar.getMaxPrice().minus( newBar.getMinPrice())
                .max( (newBar.getMaxPrice().minus(previousBar.getClosePrice()).abs()))
                .max((newBar.getMinPrice().minus(previousBar.getClosePrice()).abs()));
        this.trueRangeList.add(trueRange);
        logger.info(trueRange.toString());
    }

	public List<Decimal> getTrueRangeList() {
		return trueRangeList;
	}

}
