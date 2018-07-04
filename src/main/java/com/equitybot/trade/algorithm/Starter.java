package com.equitybot.trade.algorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.equitybot.trade.algorithm.mongodb.repository.ActionLogDataRepository;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2

public class Starter {
	
	 @Autowired
	    private ActionLogDataRepository actionRepo;
	public static void main(String[] args){
		SpringApplication.run(Starter.class, args);
		
		
	}
	
	
}
