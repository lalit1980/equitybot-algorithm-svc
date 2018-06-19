package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.Decimal;

import java.util.LinkedList;
import java.util.List;

public class AverageTrueRange {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    private List<Decimal>averageTrueRangeList;

    private int range;

    private int rangeLessOne;

    public AverageTrueRange(){
        this.averageTrueRangeList = new LinkedList<>();
    }

    public void buildAverageTrueRange(List<Decimal>trueRangeList, int range){
        logger.info(" * Building Average True Range ");
        this.range = range;
        this.rangeLessOne = range-1;
        long trueRangeListSize = trueRangeList.size();
        Decimal atr = Decimal.valueOf(0);
        for (int index = 0 ; index < this.range ; index++) {
            averageTrueRangeList.add(atr);
            logger.info(atr.toString());
        }
        for (int index = this.range; index < trueRangeListSize ; index++) {
            atr = Decimal.valueOf(0);
            for (int index2 = index-rangeLessOne; index2 <= index; index2++) {
                atr = atr.plus(trueRangeList.get(index2));
            }
            atr = atr.dividedBy(range);
            averageTrueRangeList.add(atr);
            logger.info(atr.toString());
        }
    }

    public void addNewAverageTrueRange(List<Decimal>trueRangeList){
        logger.info(" * adding new Average True Range  ");
        Decimal atr  = Decimal.valueOf(0);
        for (int index = trueRangeList.size()-this.range; index <= index; index++) {
            atr = atr.plus(trueRangeList.get(index));
        }
        atr = atr.dividedBy(range);
        averageTrueRangeList.add(atr);
        logger.info(atr.toString());
    }

	public List<Decimal> getAverageTrueRangeList() {
		return averageTrueRangeList;
	}

	public int getRange() {
		return range;
	}

}
