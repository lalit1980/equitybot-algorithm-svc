package com.equitybot.trade.algorithm.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.util.LinkedList;
import java.util.List;
@Service
public class FinalLowerBand {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<Decimal> finalLowerBandList;
    private int bandSize;
    private int range;

    public FinalLowerBand(){
        this.finalLowerBandList = new LinkedList<>();
    }

    public void buildFinalLowerBand(TimeSeries timeSeries, BasicLowerBand basicLowerBand) {
        logger.info(" * Building Final Lower Band");
        this.bandSize = basicLowerBand.getBandSize();
        this.range = basicLowerBand.getRange();
        Decimal dinalLowerBand = Decimal.valueOf(0);
        for (int index = 0; index < basicLowerBand.getRange(); index++) {
            this.finalLowerBandList.add(dinalLowerBand);
            logger.info(dinalLowerBand.toString());
        }
        /*dinalLowerBand = basicLowerBand.getBasicLowerBandList().get(basicLowerBand.getRange());
        this.finalLowerBandList.add(dinalLowerBand);*/
        //logger.info(dinalLowerBand.toString());
        Decimal basicLowerBandValue;
        Decimal previousFinalLowerBand;
        for (int index = basicLowerBand.getRange() ; index < basicLowerBand.getBasicLowerBandList().size(); index++) {
            basicLowerBandValue = basicLowerBand.getBasicLowerBandList().get(index);
            previousFinalLowerBand = this.finalLowerBandList.get(index - 1);
            if (basicLowerBandValue.isGreaterThan(previousFinalLowerBand) ||
                    timeSeries.getBarData().get(index - 1).getClosePrice().isLessThan(previousFinalLowerBand)) {
                dinalLowerBand = basicLowerBandValue;
            } else {
                dinalLowerBand = previousFinalLowerBand;
            }
            this.finalLowerBandList.add(dinalLowerBand);
            logger.info(dinalLowerBand.toString());
        }
    }

    public void addNewFinalLowerBand(TimeSeries newTimeSeries, BasicLowerBand newBasicLowerBand){
        logger.info(" * adding new Final Lower Band");
        Decimal finalLowerBand ;
        Decimal basicLowerBandValue = newBasicLowerBand.getBasicLowerBandList()
                .get(newBasicLowerBand.getBasicLowerBandList().size()-1);
        Decimal previousFinalLowerBand = this.finalLowerBandList.get(finalLowerBandList.size() - 1);
        if (basicLowerBandValue.isGreaterThan(previousFinalLowerBand) ||
                newTimeSeries.getBarData().get(newTimeSeries.getBarData().size() - 2)
                        .getClosePrice().isLessThan(previousFinalLowerBand)) {
            finalLowerBand = basicLowerBandValue;
        } else {
            finalLowerBand = previousFinalLowerBand;
        }
        this.finalLowerBandList.add(finalLowerBand);
        logger.info(finalLowerBand.toString());
    }

	public List<Decimal> getFinalLowerBandList() {
		return finalLowerBandList;
	}

	public int getBandSize() {
		return bandSize;
	}

	public int getRange() {
		return range;
	}
}
