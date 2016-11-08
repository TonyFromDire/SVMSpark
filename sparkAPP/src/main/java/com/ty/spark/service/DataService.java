package com.ty.spark.service;

import com.ty.spark.util.HttpHelper;
import com.ty.spark.util.RequestURLBuilder;

public class DataService {
	public String getJsonArray(String devid, String type, String beginTime,
			String endTime) {
		//从接口地址去获取json数据
		RequestURLBuilder requestURLBuilder = new RequestURLBuilder(devid,
				type, beginTime, endTime);
		String urlString = requestURLBuilder.getURL();
		System.out.println(urlString);
		HttpHelper httpHelper = new HttpHelper(urlString);
		String jsonArrayString = httpHelper.getrequest();
		return jsonArrayString;
	}
}
