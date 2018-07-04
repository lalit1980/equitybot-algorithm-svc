package com.equitybot.trade.algorithm.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.equitybot.trade.algorithm.bo.LogData;

public interface LogDataRepository extends MongoRepository<LogData, Long>, LogDataRespositoryCustom {

}
