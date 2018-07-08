package com.equitybot.trade.algorithm.strategy;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ta4j.core.TimeSeries;

import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.LogDataRepository;

@Service
public class ValidateSuperTrend {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<Long, SuperTrendAnalyzer> superTrendAnalyzerMap;
    @Value("${supertrend.bandSize}")
    private int bandSize;
    @Value("${supertrend.smaSize}")
    private int smaSize;
    @Value("${supertrend.csvPath}")
    private String csvPath;
    @Autowired
	private AlgorithmDataLoger algorithmDataLoger;
    
    
    public ValidateSuperTrend() {
        this.superTrendAnalyzerMap = new HashMap<>();
    }

    public void evaluate(TimeSeries timeSeries,IgniteCache<Long, Double> totalProfitCache) throws IOException {
        if (timeSeries != null && timeSeries.getBarData().size() > 0) {
            SuperTrendAnalyzer superTrendAnalyzer = this.superTrendAnalyzerMap.get(Long.parseLong(timeSeries.getName()));
            if (superTrendAnalyzer == null) {
                superTrendAnalyzer = getSuperTrendAnalyzer(Long.parseLong(timeSeries.getName()),totalProfitCache);
                this.superTrendAnalyzerMap.put(Long.parseLong(timeSeries.getName()), superTrendAnalyzer);
            }
            superTrendAnalyzer.analysis(timeSeries.getBarData().get(timeSeries.getBarData().size() - 1));
            logger.info(" * Super Trend evaluate for instrument" + timeSeries.getName());
        } else {
            logger.info(" * Super Trend evaluate fail timeSeries is empty or null for instrument" + timeSeries.getName());
        }
    }

    private SuperTrendAnalyzer getSuperTrendAnalyzer(final Long instrument,IgniteCache<Long, Double> totalProfitCache) throws IOException {
       
        return new SuperTrendAnalyzer(this.bandSize, this.smaSize, instrument, algorithmDataLoger);
        }


    
}