package com.ty.spark.util;

public class RequestURLBuilder {
	private final String IPPrefix = "http://10.109.247.121/electric_backend/public/transformer/ems/";
	private String devid;
	private String type;
	private String beginTime;
	private String endTime;

	public RequestURLBuilder(String devid, String type, String beginTime,
			String endTime) {
		this.devid = devid;
		this.type = type;
		this.beginTime = beginTime.replaceAll("\\s", "%20");
		this.endTime = endTime.replaceAll("\\s", "%20");
		this.URL = this.IPPrefix + this.type + "?devid=" + this.devid
				+ "&beginDateTime=" + this.beginTime + "&endDateTime="
				+ this.endTime;
		// System.out.println(this.URL);
		// http://10.109.247.121/eletric_backend/public/transformer/ems/
		// q?devid=1900697288&beginDateTime=2015-07-15%2000:00:00&endDateTime=2015-7-15%2001:00:00
	}



	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	private String URL;

}

