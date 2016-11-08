package com.ty.spark.domain;

import java.io.Serializable;




public class Parameter implements Serializable{
	private  Double C;
	private  Double gamma;
	public Parameter(double C,double gamma) {
		this.C=C;
		this.gamma=gamma;
	}
	
	public Double getC() {
		return C;
	}
	public void setC(Double c) {
		C = c;
	}
	public Double getGamma() {
		return gamma;
	}
	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}


}
