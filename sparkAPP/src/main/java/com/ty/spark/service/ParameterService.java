package com.ty.spark.service;

import java.util.ArrayList;
import java.util.List;
import com.ty.spark.domain.Parameter;

public class ParameterService {
	public  List<Parameter> getParameterList() {
		List<Parameter> lists=new ArrayList<Parameter>();
		double stepSizeC=1;
		double stepSizeGamma=0.000001;
		double startC=30;
		double startGamma=0.000001;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 5; j++) {
				double C=startC+i*stepSizeC;
				double gamma=startGamma*stepSizeGamma;
				Parameter parameter=new Parameter(C,gamma);
				lists.add(parameter);
			}
		}
		return lists;
	}
}
