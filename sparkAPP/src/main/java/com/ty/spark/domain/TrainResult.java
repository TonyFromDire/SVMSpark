package com.ty.spark.domain;

import java.io.Serializable;
import java.util.List;

public class TrainResult implements Serializable {
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Parameter getParameter() {
		return parameter;
	}

	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}

	public List<DataPoint> getDataList() {
		return dataList;
	}

	public void setDataList(List<DataPoint> dataList) {
		this.dataList = dataList;
	}

	public Double getErmse() {
		return Ermse;
	}

	public void setErmse(Double ermse) {
		Ermse = ermse;
	}

	public Double getEmre() {
		return Emre;
	}

	public void setEmre(Double emre) {
		Emre = emre;
	}



	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	private int total;// 总数据条数
	private int error;// 预测错误失准条数
	private Double rate;// 合格率(1.0 - ((double) ERROR / (double) ALLCOUNT)) 是个0.N的小于1的小数
	private Parameter parameter;// 此次模型训练的参数
	private List<DataPoint> dataList;// 此次预测的结果列表，包括数据点原始值和真值

	private Double Ermse;// 均方根相对误差
	private Double Emre;// 平均相对误差
	public TrainResult(int total,int error,Double rate,Parameter parameter,List<DataPoint> dataList,Double Ermse,Double Emre) {
		this.total=total;
		this.error=error;
		this.rate=rate;
		this.parameter=parameter;
		this.dataList=dataList;
		this.Ermse=Ermse;
		this.Emre=Emre;
		
	}

}
