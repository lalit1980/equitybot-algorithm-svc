package com.equitybot.trade.algorithm.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.equitybot.trade.algorithm.mongodb.domain.LogData;

public interface LogDataRepository extends MongoRepository<LogData, Long>, LogDataRespositoryCustom {

}
