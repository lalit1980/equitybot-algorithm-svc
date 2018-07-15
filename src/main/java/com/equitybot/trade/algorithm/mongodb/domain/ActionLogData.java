package com.equitybot.trade.algorithm.mongodb.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "ActionLogData")
public class ActionLogData {
	
	@Id
	private String id;
	@Indexed(name = "instrumentToken_index")
	private long instrumentToken;
	private double close;
	private String signal;
	private Date transactionTime;
	private double profitAndLoss;
	private double totalProfitLoss;
	private String type;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getInstrumentToken() {
		return instrumentToken;
	}
	public void setInstrumentToken(long instrumentToken) {
		this.instrumentToken = instrumentToken;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public String getSignal() {
		return signal;
	}
	public void setSignal(String signal) {
		this.signal = signal;
	}
	public Date getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(Date transactionTime) {
		this.transactionTime = transactionTime;
	}
	public double getProfitAndLoss() {
		return profitAndLoss;
	}
	public void setProfitAndLoss(double profitAndLoss) {
		this.profitAndLoss = profitAndLoss;
	}
	public double getTotalProfitLoss() {
		return totalProfitLoss;
	}
	public void setTotalProfitLoss(double totalProfitLoss) {
		this.totalProfitLoss = totalProfitLoss;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "ActionLogData [id=" + id + ", instrumentToken=" + instrumentToken + ", close=" + close + ", signal="
				+ signal + ", transactionTime=" + transactionTime + ", profitAndLoss=" + profitAndLoss
				+ ", totalProfitLoss=" + totalProfitLoss + ", type=" + type + "]";
	}
	
}
