package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import com.equitybot.trade.algorithm.bo.ActionLogData;

public interface ActionLogDataRespositoryCustom {
	
	public List<ActionLogData> findByInstrumentToken(long token);
	public void saveActionLogData(ActionLogData actionLogData);
	public void deleteAll();

}
