package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.equitybot.trade.algorithm.mongodb.domain.LogData;

public class LogDataRepositoryImpl implements LogDataRespositoryCustom {
	@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public List<LogData> findByInstrumentToken(long token) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, "transactionTime"));
		query.addCriteria(Criteria.where("instrumentToken").is(token));
		return mongoTemplate.find(query, LogData.class);
	}

	@Override
	public void saveTickData(LogData logData) {
		mongoTemplate.save(logData);
		
	}

}
