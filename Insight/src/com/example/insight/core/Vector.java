package com.example.insight.core;

public class Vector {

	
	private String BSSID="";
	public double meanLevel=0;
	private double variance=0;
	public Vector(String BSSID,double meanLevel,double variance)
	{
		this.BSSID=BSSID;
		this.meanLevel=meanLevel;
		this.variance=variance;
	}
	public String getBSSID() {
		return BSSID;
	}
//	public double getMeanLevel() {
//		return meanLevel;
//	}
	public double getVariance() {
		return variance;
	}
	public void setMeanLevel(double meanLevel) {
		this.meanLevel = meanLevel;
	}
	public void setVariance(double variance) {
		this.variance = variance;
	}
	
}
