package com.equitybot.trade.algorithm.strategy;

import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.Bar;
import org.ta4j.core.Decimal;

import com.equitybot.trade.algorithm.mongodb.domain.ActionLogData;
import com.equitybot.trade.algorithm.mongodb.domain.LogData;
import com.equitybot.trade.algorithm.mongodb.domain.ProfitLossData;
import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.LogDataRepository;
import com.equitybot.trade.algorithm.mongodb.repository.ProfitLossRepository;

@Service
public class AlgorithmDataLoger {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LogDataRepository repo;
	@Autowired
	private ActionLogDataRepository actionRepo;
	@Autowired
	private ProfitLossRepository profitLossRepository;

	public void logData(Bar workingBar, long instrumentToken, Decimal workingTrueRange, Decimal workingEMA,
			Decimal workingBUB, Decimal workingBLB, Decimal workingFUB, Decimal workingFLB, Decimal workingSuperTrend,
			String buySell){

		LogData data = new LogData();
		data.setInstrumentToken(instrumentToken);
		data.setOpen(workingBar.getOpenPrice().doubleValue());
		data.setHigh(workingBar.getMaxPrice().doubleValue());
		data.setLow(workingBar.getMinPrice().doubleValue());
		data.setClose(workingBar.getClosePrice().doubleValue());
		data.setTrueRange(workingTrueRange.doubleValue());
		data.setEma(workingEMA.doubleValue());
		data.setBasicUpperBand(workingBUB.doubleValue());
		data.setBasicLowerBand(workingBLB.doubleValue());
		data.setFinalUpperBand(workingFUB.doubleValue());
		data.setFinalLowerBand(workingFLB.doubleValue());
		data.setSuperTrend(workingSuperTrend.doubleValue());
		data.setSignal(buySell);
		data.setId(UUID.randomUUID().toString());
		data.setTransactionTime(new Date());
		repo.saveTickData(data);
	}

	public void logActionLogData(Decimal openPrice,Decimal maxPrice, Decimal minPrice, Decimal closePrice, long instrumentToken, Decimal workingTrueRange, Decimal workingEMA,
			Decimal workingBUB, Decimal workingBLB, Decimal workingFUB, Decimal workingFLB, Decimal workingSuperTrend,
			String buySell, Decimal profitAndLoss, Decimal totalProfitLoss, String type){

		ActionLogData data = new ActionLogData();
		data.setInstrumentToken(instrumentToken);
		data.setClose(closePrice.doubleValue());
		data.setSignal(buySell);
		data.setId(UUID.randomUUID().toString());
		data.setTransactionTime(new Date());
		data.setProfitAndLoss(profitAndLoss.doubleValue());
		data.setTotalProfitLoss(totalProfitLoss.doubleValue());
		data.setType(type);
		logger.info("Instrument Token: "+instrumentToken+" Order Detail: "+data.toString());
		actionRepo.saveActionLogData(data);
	}

	public void logProfitLossRepository(long instrumentToken, double totalProfitLoss) {

		ProfitLossData profitLossData = new ProfitLossData();
		profitLossData.setId(UUID.randomUUID().toString());
		profitLossData.setInstrumentToken(instrumentToken);
		profitLossData.setTotalProfitLoss(totalProfitLoss);
		logger.info("Instrument Token: "+instrumentToken+" Total Profit: "+profitLossData.toString());
		profitLossRepository.saveUpdate(profitLossData);
	}

}
