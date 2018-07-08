package com.equitybot.trade.algorithm.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.equitybot.trade.algorithm.bo.ProfitLossData;


public interface ProfitLossRepository extends MongoRepository<ProfitLossData, Long>, ProfitLossRepositoryCustom{

}
