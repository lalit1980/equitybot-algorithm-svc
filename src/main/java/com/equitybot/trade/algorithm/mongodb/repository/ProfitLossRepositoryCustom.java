package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import com.equitybot.trade.algorithm.bo.ProfitLossData;

public interface ProfitLossRepositoryCustom {
	
	public void saveData(ProfitLossData profitLossData) ;
	
	public long updateByInstrumentToken(ProfitLossData profitLossData);
	
	public void saveUpdate(ProfitLossData profitLossData);
	
	public long deleteByInstrumentToken(ProfitLossData profitLossData);
	
	public void deleteAll();
	
	public List<ProfitLossData> findByInstrumentToken(long token);
	
	public List<ProfitLossData> getMexProfitDataList(int numOfMexRecord);
	
	public List<ProfitLossData> getMexLossDataList(int numOfMenRecord);
	

}
