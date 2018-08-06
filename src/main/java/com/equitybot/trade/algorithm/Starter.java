package com.equitybot.trade.algorithm;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2

public class Starter {
	
	public static void main(String[] args){
		SpringApplication.run(Starter.class, args);
	}
	@Bean
	public Docket swaggerActionLogApi10() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("actionlog-api-1.0")
				.select()
					.apis(RequestHandlerSelectors.basePackage("com.equitybot.trade.algorithm.controller"))
					.paths(regex("/actionlog/v1.0.*"))
				.build()
				.apiInfo(new ApiInfoBuilder().version("1.0").title("Action Log API").description("Action Log API v1.0").build());
	}
	
	
}
