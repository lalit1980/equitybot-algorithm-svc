package com.equitybot.trade.algorithm.controller;

import java.util.List;

import com.equitybot.trade.algorithm.util.Pool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equitybot.trade.algorithm.mongodb.domain.ActionLogData;
import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;

@RestController
@RequestMapping("/api")
public class ActionLogController {
	@Autowired
	ActionLogDataRepository actionRepository;

	@Autowired
	private Pool pool;

	@DeleteMapping({ "/actionlog/v1.0" })
	public void delete() {
		 actionRepository.deleteAll();
	}

	@GetMapping("/actionlog/v1.0")
	public List<ActionLogData> findAll() {

		return actionRepository.findAll();
	}
	
	@GetMapping("/actionlog/v1.0/{instrumentToken}")
	public List<ActionLogData> findByInstrumentToken(@PathVariable("instrumentToken") long instrumentToken) {

		return actionRepository.findByInstrumentToken(instrumentToken);
	}

	@GetMapping("/actionlog/clearpool/v1.0")
	public void clearSuperTrendcache() {

		this.pool.cleanPool();
	}
}
