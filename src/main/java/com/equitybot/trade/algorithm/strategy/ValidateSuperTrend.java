package com.equitybot.trade.algorithm.strategy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.ta4j.core.Decimal;
import org.ta4j.core.TimeSeries;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ValidateSuperTrend {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<Long, SuperTrendAnalyzer> superTrendAnalyzerMap;
    @Value("${supertrend.bandSize}")
    private int bandSize;
    @Value("${supertrend.smaSize}")
    private int smaSize;

    @Autowired
    private AlgorithmDataLoger algorithmDataLoger;

    static {
        ValidateSuperTrend.superTrendAnalyzerMap = new HashMap<>();
    }

    public void evaluate(TimeSeries timeSeries) throws IOException {
        if (timeSeries != null && !timeSeries.getBarData().isEmpty()) {
            SuperTrendAnalyzer superTrendAnalyzer = ValidateSuperTrend.superTrendAnalyzerMap.get(Long.parseLong(timeSeries.getName()));
            if (superTrendAnalyzer == null) {
                superTrendAnalyzer = getSuperTrendAnalyzer(Long.parseLong(timeSeries.getName()));
                ValidateSuperTrend.superTrendAnalyzerMap.put(Long.parseLong(timeSeries.getName()), superTrendAnalyzer);
            }
            superTrendAnalyzer.analysis(timeSeries.getBarData().get(timeSeries.getBarData().size() - 1));
            logger.info(" * Super Trend evaluate for instrument {}" , timeSeries.getName());
        } else {
            logger.info(" * Super Trend evaluate fail timeSeries is empty or null for instrument {}" ,
                    timeSeries == null ? "" : timeSeries.getName());
        }
    }

    private SuperTrendAnalyzer getSuperTrendAnalyzer(final Long instrument) throws IOException {
        return new SuperTrendAnalyzer(this.bandSize, this.smaSize, instrument, algorithmDataLoger);
    }

    public static void clearSuperTrendAnalyzerMap(){
        ValidateSuperTrend.superTrendAnalyzerMap = new HashMap<>();
    }
    
    public void stopLoss( Decimal closePrice, Long instrument) {
            SuperTrendAnalyzer superTrendAnalyzer = ValidateSuperTrend.superTrendAnalyzerMap.get(instrument);
            if (superTrendAnalyzer != null) {
            	superTrendAnalyzer.stopLoss(closePrice);
            }
    }

}