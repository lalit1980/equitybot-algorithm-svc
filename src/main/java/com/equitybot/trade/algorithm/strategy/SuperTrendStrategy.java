package com.equitybot.trade.algorithm.strategy;

import com.equitybot.trade.algorithm.indicator.SuperTrendAnalyzer;
import com.equitybot.trade.algorithm.util.Cache;
import com.equitybot.trade.algorithm.util.DBLogger;
import com.equitybot.trade.algorithm.util.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;


@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SuperTrendStrategy {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DBLogger DBLogger;

    @Value("${supertrend.multiplier}")
    private int multiplier;

    @Value("${supertrend.period}")
    private int period;


    @Autowired
    private Pool pool;
    
    @Autowired
    private Cache cache;


    public SuperTrendAnalyzer build(Bar bar, Long instrument) {
            SuperTrendAnalyzer superTrendAnalyzer = this.pool.getSuperTrendAnalyzerMap().get(instrument);

            if (superTrendAnalyzer == null) {
                superTrendAnalyzer = getSuperTrendAnalyzer(instrument);
                this.pool.getSuperTrendAnalyzerMap().put(instrument, superTrendAnalyzer);
            }
        superTrendAnalyzer.analyze(bar);
        this.pool.getSuperTrendProfitPool().put(superTrendAnalyzer.getSuperTradeIndicator().getInstrument(),
                superTrendAnalyzer.getTotalProfitLoss());
        log(superTrendAnalyzer);
        return superTrendAnalyzer;
    }

    private void log(SuperTrendAnalyzer superTrendAnalyzer) {
        this.DBLogger.logSuperTrendData(superTrendAnalyzer);
        if (superTrendAnalyzer.getAction() == -1) {
            sell(superTrendAnalyzer);
        } else if (superTrendAnalyzer.getAction() == 1) {
            buy(superTrendAnalyzer);
        }
    }

    private void buy(SuperTrendAnalyzer superTrendAnalyzer) {
    		this.DBLogger.logSuperTrendAllProfitLossData(superTrendAnalyzer, "SuperTrend");
    }

    private void sell(SuperTrendAnalyzer superTrendAnalyzer) {
    		this.DBLogger.logSuperTrendAllProfitLossData(superTrendAnalyzer, "SuperTrend");
    		this.DBLogger.logSuperTrendProfitLoss(superTrendAnalyzer);
    }

    private SuperTrendAnalyzer getSuperTrendAnalyzer(final Long instrument) {
        return new SuperTrendAnalyzer(this.multiplier, this.period, instrument);
    }

}