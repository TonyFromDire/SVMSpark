package com.ty.spark.domain;

import java.io.Serializable;

public class DataPoint implements Serializable{
	private  Double trueValue;//原数据点真值
	private  Double predictValue;//预测值
	private  Double errorPercent;//误差百分比 N%，是N，不是0.N
	public Double getTrueValue() {
		return trueValue;
	}
	public void setTrueValue(Double trueValue) {
		this.trueValue = trueValue;
	}
	public Double getPredictValue() {
		return predictValue;
	}
	public void setPredictValue(Double predictValue) {
		this.predictValue = predictValue;
	}
	public Double getErrorPercent() {
		return errorPercent;
	}
	public void setErrorPercent(Double errorPercent) {
		this.errorPercent = errorPercent;
	}
	
	public DataPoint(Double trueValue,Double predictValue,Double errorPercent) {
		// TODO Auto-generated constructor stub
		this.trueValue=trueValue;
		this.predictValue=predictValue;
		this.errorPercent=errorPercent;
	}
	
}
