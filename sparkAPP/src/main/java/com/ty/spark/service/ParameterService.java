package com.ty.spark.service;

import java.util.ArrayList;
import java.util.List;
import com.ty.spark.domain.Parameter;

public class ParameterService {
	public  List<Parameter> getParameterList() {
		List<Parameter> lists=new ArrayList<Parameter>();
		double stepSizeC=0.1;
		double stepSizeGamma=0.1;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				double C=0+i*stepSizeC;
				double gamma=0+j*stepSizeGamma;
				Parameter parameter=new Parameter(C,gamma);
				lists.add(parameter);
			}
		}
		return lists;
	}
}
