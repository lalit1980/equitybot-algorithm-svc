package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.indicator.SuperTrendAnalyzer;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.ta4j.core.Decimal;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Pool {

    private Map<Long,Decimal> superTrendProfitPool;

    private Map<Long, SuperTrendAnalyzer> superTrendAnalyzerMap;


    public Pool(){
        this.superTrendProfitPool = new HashMap<>();
        this.superTrendAnalyzerMap = new HashMap<>();
    }

    public Map<Long, Decimal> getSuperTrendProfitPool() {
        return superTrendProfitPool;
    }

    public Map<Long, SuperTrendAnalyzer> getSuperTrendAnalyzerMap() {
        return superTrendAnalyzerMap;
    }

    public void cleanPool(){
        this.superTrendProfitPool.clear();
        this.superTrendAnalyzerMap.clear();
    }
}
