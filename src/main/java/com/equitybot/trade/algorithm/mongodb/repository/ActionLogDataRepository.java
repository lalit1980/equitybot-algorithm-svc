package com.equitybot.trade.algorithm.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.equitybot.trade.algorithm.bo.ActionLogData;

public interface ActionLogDataRepository extends MongoRepository<ActionLogData, Long>, ActionLogDataRespositoryCustom{

}
