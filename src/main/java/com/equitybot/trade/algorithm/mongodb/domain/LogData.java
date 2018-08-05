package com.equitybot.trade.algorithm.mongodb.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "LogData")
public class LogData {

	@Id
	private String id;
	@Indexed(name = "instrumentToken_index")
	private long instrumentToken;
	private String tradingSymbol;
	private double open; 
	private double high;
	private double low;
	private double close;
	private double trueRange;
	private double ema;
	private double basicUpperBand;
	private double basicLowerBand;
	private double finalUpperBand;
	private double finalLowerBand;
	private double superTrend;
	private String signal;
	private String transactionTime;
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
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getTrueRange() {
		return trueRange;
	}
	public void setTrueRange(double trueRange) {
		this.trueRange = trueRange;
	}
	public double getEma() {
		return ema;
	}
	public void setEma(double ema) {
		this.ema = ema;
	}
	public double getBasicUpperBand() {
		return basicUpperBand;
	}
	public void setBasicUpperBand(double basicUpperBand) {
		this.basicUpperBand = basicUpperBand;
	}
	public double getBasicLowerBand() {
		return basicLowerBand;
	}
	public void setBasicLowerBand(double basicLowerBand) {
		this.basicLowerBand = basicLowerBand;
	}
	public double getFinalUpperBand() {
		return finalUpperBand;
	}
	public void setFinalUpperBand(double finalUpperBand) {
		this.finalUpperBand = finalUpperBand;
	}
	public double getFinalLowerBand() {
		return finalLowerBand;
	}
	public void setFinalLowerBand(double finalLowerBand) {
		this.finalLowerBand = finalLowerBand;
	}
	public double getSuperTrend() {
		return superTrend;
	}
	public void setSuperTrend(double superTrend) {
		this.superTrend = superTrend;
	}
	public String getSignal() {
		return signal;
	}
	public void setSignal(String signal) {
		this.signal = signal;
	}
	public String getTransactionTime() {
		return transactionTime;
	}
	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}
	@Override
	public String toString() {
		return "LogData [id=" + id + ", instrumentToken=" + instrumentToken + ", tradingSymbol=" + tradingSymbol
				+ ", open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", trueRange=" + trueRange
				+ ", ema=" + ema + ", basicUpperBand=" + basicUpperBand + ", basicLowerBand=" + basicLowerBand
				+ ", finalUpperBand=" + finalUpperBand + ", finalLowerBand=" + finalLowerBand + ", superTrend="
				+ superTrend + ", signal=" + signal + ", transactionTime=" + transactionTime + "]";
	}
	public String getTradingSymbol() {
		return tradingSymbol;
	}
	public void setTradingSymbol(String tradingSymbol) {
		this.tradingSymbol = tradingSymbol;
	}
	
	
}
