package com.equitybot.trade.algorithm.util;

import com.equitybot.trade.algorithm.indicator.SuperTrendAnalyzer;
import com.equitybot.trade.algorithm.mongodb.domain.ActionLogData;
import com.equitybot.trade.algorithm.mongodb.domain.LogData;
import com.equitybot.trade.algorithm.mongodb.domain.ProfitLossData;
import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.LogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.ProfitLossRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class DBLogger {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LogDataRepository repo;
    @Autowired
    private ActionLogDataRepository actionRepo;
    @Autowired
    private ProfitLossRepository profitLossRepository;
    @Autowired
    private Cache cache;

    public void logSuperTrendData(SuperTrendAnalyzer superTrendAnalyzer) {
        logger.info(superTrendAnalyzer.toString());
        LogData data = new LogData();
        data.setInstrumentToken(superTrendAnalyzer.getSuperTradeIndicator().getInstrument());
        data.setOpen(superTrendAnalyzer.getSuperTradeIndicator().getBar().getOpenPrice().doubleValue());
        data.setHigh(superTrendAnalyzer.getSuperTradeIndicator().getBar().getMaxPrice().doubleValue());
        data.setLow(superTrendAnalyzer.getSuperTradeIndicator().getBar().getMinPrice().doubleValue());
        data.setClose(superTrendAnalyzer.getSuperTradeIndicator().getBar().getClosePrice().doubleValue());
        data.setTrueRange(superTrendAnalyzer.getSuperTradeIndicator().getTrueRange().doubleValue());
        data.setEma(superTrendAnalyzer.getSuperTradeIndicator().getExponentialMovingAverage().doubleValue());
        data.setBasicUpperBand(superTrendAnalyzer.getSuperTradeIndicator().getBasicUpperBand().doubleValue());
        data.setBasicLowerBand(superTrendAnalyzer.getSuperTradeIndicator().getBasicLowerBand().doubleValue());
        data.setFinalUpperBand(superTrendAnalyzer.getSuperTradeIndicator().getFinalUpperBand().doubleValue());
        data.setFinalLowerBand(superTrendAnalyzer.getSuperTradeIndicator().getFinalLowerBand().doubleValue());
        data.setSuperTrend(superTrendAnalyzer.getSuperTradeIndicator().getSuperTrend().doubleValue());
        data.setSignal(superTrendAnalyzer.getSuperTradeIndicator().getBuySell());
        data.setId(UUID.randomUUID().toString());
        data.setTransactionTime(new Date());
        repo.saveTickData(data);
    }

    public void logSuperTrendAllProfitLossData(SuperTrendAnalyzer superTrendAnalyzer, String type) {
        ActionLogData data = new ActionLogData();
        data.setInstrumentToken(superTrendAnalyzer.getSuperTradeIndicator().getInstrument());
        data.setClose(superTrendAnalyzer.getSuperTradeIndicator().getBar().getClosePrice().doubleValue());
        data.setSignal(superTrendAnalyzer.getSuperTradeIndicator().getBuySell());
        data.setId(UUID.randomUUID().toString());
        data.setTransactionTime(new Date());
        data.setProfitAndLoss(superTrendAnalyzer.getCurrentProfitLoss().doubleValue());
        data.setTotalProfitLoss(superTrendAnalyzer.getTotalProfitLoss().doubleValue());
        data.setType(type);
        logger.info("Instrument Token: {} : Order Detail: {} ", superTrendAnalyzer.getSuperTradeIndicator().getInstrument(), data.toString());
        actionRepo.saveActionLogData(data);
    }

    public void logSuperTrendProfitLoss(SuperTrendAnalyzer superTrendAnalyzer) {
        ProfitLossData profitLossData = new ProfitLossData();
        profitLossData.setId(UUID.randomUUID().toString());
        profitLossData.setInstrumentToken(superTrendAnalyzer.getSuperTradeIndicator().getInstrument());
        profitLossData.setTotalProfitLoss(superTrendAnalyzer.getTotalProfitLoss().doubleValue());
        cache.getCacheTotalProfit().put(superTrendAnalyzer.getSuperTradeIndicator().getInstrument(), superTrendAnalyzer.getTotalProfitLoss().doubleValue());
        logger.info("Instrument Token: {} :Total Profit: {} :", superTrendAnalyzer.getSuperTradeIndicator().getInstrument(), profitLossData.toString());
        profitLossRepository.saveUpdate(profitLossData);
    }
}
