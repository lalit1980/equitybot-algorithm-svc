package com.equitybot.trade.algorithm.mongodb.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Sort;

import com.equitybot.trade.algorithm.mongodb.domain.ProfitLossData;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class ProfitLossRepositoryImpl implements ProfitLossRepositoryCustom {

	@Autowired
	MongoTemplate mongoTemplate;

	public void saveData(ProfitLossData profitLossData) {
		mongoTemplate.save(profitLossData);
	}

	@Override
	public long updateByInstrumentToken(ProfitLossData profitLossData) {
		Query query = new Query(Criteria.where("instrumentToken").is(profitLossData.getInstrumentToken()));
		Update update = new Update();
		update.set("totalProfitLoss", profitLossData.getTotalProfitLoss());
		UpdateResult result = mongoTemplate.updateMulti(query, update, ProfitLossData.class);
		if (result != null) {
			return result.getModifiedCount();
		} else {
			return 0;
		}
	}

	@Override
	public void saveUpdate(ProfitLossData profitLossData) {
		if (updateByInstrumentToken(profitLossData) == 0) {
			saveData(profitLossData);
		}
	}

	@Override
	public long deleteByInstrumentToken(ProfitLossData profitLossData) {
		Query query = new Query();
		query.addCriteria(Criteria.where("instrumentToken").is(profitLossData.getInstrumentToken()));
		DeleteResult result = mongoTemplate.remove(query, ProfitLossData.class);
		if (result != null) {
			return result.getDeletedCount();
		} else {
			return 0;
		}
	}

	@Override
	public void deleteAll() {
		mongoTemplate.remove(new Query(), "ProfitLossData");
	}

	@Override
	public List<ProfitLossData> findByInstrumentToken(long token) {
		Query query = new Query();
		query.addCriteria(Criteria.where("instrumentToken").is(token));
		return mongoTemplate.find(query, ProfitLossData.class);
	}

	@Override
	public List<ProfitLossData> getMexProfitDataList(int numOfMexRecord) {
		Query query = new Query();
		query.limit(numOfMexRecord);
		query.with(new Sort(Sort.Direction.DESC, "totalProfitLoss"));
		return mongoTemplate.find(query, ProfitLossData.class);
	}

	@Override
	public List<ProfitLossData> getMexLossDataList(int numOfMenRecord) {
		Query query = new Query();
		query.limit(numOfMenRecord);
		query.with(new Sort(Sort.Direction.ASC, "totalProfitLoss"));
		return mongoTemplate.find(query, ProfitLossData.class);
	}

}
