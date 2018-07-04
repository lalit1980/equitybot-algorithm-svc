package com.equitybot.trade.algorithm.mongodb.domain;

import org.ta4j.core.Decimal;

public class SuperTrendBO {

	private long instrumentToken;
	private Decimal previousBasicLowerBand=Decimal.valueOf(0.0);
	private Decimal previousBasicUpperBand=Decimal.valueOf(0.0);
	private Decimal currentBasicLowerBand=Decimal.valueOf(0.0);
	private Decimal currentBasicUpperBand=Decimal.valueOf(0.0);
	private Decimal previousFinalLowerBand=Decimal.valueOf(0.0);
	private Decimal previousFinalUpperBand=Decimal.valueOf(0.0);
	private Decimal currentFinalLowerBand=Decimal.valueOf(0.0);
	private Decimal currentFinalUpperBand=Decimal.valueOf(0.0);
	private Decimal currentATR=Decimal.valueOf(0.0);
	private Decimal previousATR=Decimal.valueOf(0.0);
	
	private Decimal high=Decimal.valueOf(0.0);
	private Decimal low=Decimal.valueOf(0.0);
	public long getInstrumentToken() {
		return instrumentToken;
	}
	public void setInstrumentToken(long instrumentToken) {
		this.instrumentToken = instrumentToken;
	}
	public Decimal getPreviousBasicLowerBand() {
		return previousBasicLowerBand;
	}
	public void setPreviousBasicLowerBand(Decimal previousBasicLowerBand) {
		this.previousBasicLowerBand = previousBasicLowerBand;
	}
	public Decimal getPreviousBasicUpperBand() {
		return previousBasicUpperBand;
	}
	public void setPreviousBasicUpperBand(Decimal previousBasicUpperBand) {
		this.previousBasicUpperBand = previousBasicUpperBand;
	}
	public Decimal getCurrentBasicLowerBand() {
		return currentBasicLowerBand;
	}
	public void setCurrentBasicLowerBand(Decimal currentBasicLowerBand) {
		this.currentBasicLowerBand = currentBasicLowerBand;
	}
	public Decimal getCurrentBasicUpperBand() {
		return currentBasicUpperBand;
	}
	public void setCurrentBasicUpperBand(Decimal currentBasicUpperBand) {
		this.currentBasicUpperBand = currentBasicUpperBand;
	}
	public Decimal getPreviousFinalLowerBand() {
		return previousFinalLowerBand;
	}
	public void setPreviousFinalLowerBand(Decimal previousFinalLowerBand) {
		this.previousFinalLowerBand = previousFinalLowerBand;
	}
	public Decimal getPreviousFinalUpperBand() {
		return previousFinalUpperBand;
	}
	public void setPreviousFinalUpperBand(Decimal previousFinalUpperBand) {
		this.previousFinalUpperBand = previousFinalUpperBand;
	}
	public Decimal getCurrentFinalLowerBand() {
		return currentFinalLowerBand;
	}
	public void setCurrentFinalLowerBand(Decimal currentFinalLowerBand) {
		this.currentFinalLowerBand = currentFinalLowerBand;
	}
	public Decimal getCurrentFinalUpperBand() {
		return currentFinalUpperBand;
	}
	public void setCurrentFinalUpperBand(Decimal currentFinalUpperBand) {
		this.currentFinalUpperBand = currentFinalUpperBand;
	}
	public Decimal getCurrentATR() {
		return currentATR;
	}
	public void setCurrentATR(Decimal currentATR) {
		this.currentATR = currentATR;
	}
	public Decimal getPreviousATR() {
		return previousATR;
	}
	public void setPreviousATR(Decimal previousATR) {
		this.previousATR = previousATR;
	}
	public Decimal getHigh() {
		return high;
	}
	public void setHigh(Decimal high) {
		this.high = high;
	}
	public Decimal getLow() {
		return low;
	}
	public void setLow(Decimal low) {
		this.low = low;
	}
	@Override
	public String toString() {
		return "SuperTrendBO [instrumentToken=" + instrumentToken + ", previousBasicLowerBand=" + previousBasicLowerBand
				+ ", previousBasicUpperBand=" + previousBasicUpperBand + ", currentBasicLowerBand="
				+ currentBasicLowerBand + ", currentBasicUpperBand=" + currentBasicUpperBand
				+ ", previousFinalLowerBand=" + previousFinalLowerBand + ", previousFinalUpperBand="
				+ previousFinalUpperBand + ", currentFinalLowerBand=" + currentFinalLowerBand
				+ ", currentFinalUpperBand=" + currentFinalUpperBand + ", currentATR=" + currentATR + ", previousATR="
				+ previousATR + ", high=" + high + ", low=" + low + "]";
	}
}
