package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import com.equitybot.trade.algorithm.mongodb.domain.LogData;

public interface LogDataRespositoryCustom {
	public List<LogData> findByInstrumentToken(long token);
	public void saveTickData(LogData logData);
}
