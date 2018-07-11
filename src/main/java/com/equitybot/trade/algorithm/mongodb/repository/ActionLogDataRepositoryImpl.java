package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.equitybot.trade.algorithm.mongodb.domain.ActionLogData;

public class ActionLogDataRepositoryImpl implements ActionLogDataRespositoryCustom {
	@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public List<ActionLogData> findByInstrumentToken(long token) {
		Query query = new Query();
		query.with(new Sort(Sort.Direction.ASC, "transactionTime"));
		query.addCriteria(Criteria.where("instrumentToken").is(token));
		return mongoTemplate.find(query, ActionLogData.class);
	}

	@Override
	public void saveActionLogData(ActionLogData actionLogData) {
		mongoTemplate.save(actionLogData);
		
	}
	@Override
	public void deleteAll() {
		mongoTemplate.remove(new Query(), "ActionLogData");
		
	}
}
