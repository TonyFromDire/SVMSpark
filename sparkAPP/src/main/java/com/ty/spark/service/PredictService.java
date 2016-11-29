package com.ty.spark.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm;
import libsvm.svm_model;

import com.ty.spark.builder.SVMNodeAndLableBuilder;
import com.ty.spark.builder.SVMParamBuilder;
import com.ty.spark.builder.SVMPredictor;
import com.ty.spark.builder.SVMProblemBuilder;
import com.ty.spark.builder.SVMTrainer;
import com.ty.spark.domain.DataPoint;
import com.ty.spark.domain.Parameter;
import com.ty.spark.domain.TrainResult;
import com.ty.spark.util.HttpHelper;
import com.ty.spark.util.RequestURLBuilder;

public class PredictService {
	public static String getJsonArray(String devid, String type,
			String beginTime, String endTime) {
		RequestURLBuilder requestURLBuilder = new RequestURLBuilder(devid,
				type, beginTime, endTime);
		String urlString = requestURLBuilder.getURL();
		System.out.println(urlString);
		HttpHelper httpHelper = new HttpHelper(urlString);
		String jsonArrayString = httpHelper.getrequest();
		return jsonArrayString;
	}

	public static void predictWithB() {
		// 接口请求数据
		// RequestURLBuilder requestURLBuilder = new RequestURLBuilder(
		// "1800002459", "p", "2015-06-01 00:00:00", "2015-06-02 00:00:00");
		// String urlString = requestURLBuilder.getURL();
		// System.out.println(urlString);
		// HttpHelper httpHelper = new HttpHelper(urlString);
		// String jsonArrayString = httpHelper.getrequest();
		PredictService predictService = new PredictService();
		String jsonArrayString = predictService.getJsonArray("1800002459", "p",
				"2015-06-08 00:00:00", "2015-06-13 00:00:00");
		System.out.println(jsonArrayString);
		// 构造nodes和lable
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayString);
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder();
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		// 训练获取模型
		svm_model svmModel = svmTrainer.train();
		// 保存模型，方便实验
		try {
			svm.svm_save_model("modelwithB", svmModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 预测
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
		String jsonArrayString2 = predictService.getJsonArray("1800002459",
				"p", "2015-06-13 00:00:00", "2015-06-14 00:00:00");
		System.out.println(jsonArrayString2);
		SVMNodeAndLableBuilder svmNodeAndLableBuilder2 = new SVMNodeAndLableBuilder(
				jsonArrayString2);
		int ALLCOUNT = svmNodeAndLableBuilder2.getLabel().size();// 总数
		System.out.println("总数：" + ALLCOUNT);
		int Ecount = ALLCOUNT;
		int ERROR = 0;
		double rule = 0.05;
		BigDecimal ruleBigDecimal = new BigDecimal(rule);
		double half = 0.99;
		BigDecimal halfBigDecimal = new BigDecimal(half);
		double Emre = 0;// 平均相对误差
		double Ermse = 0;// 均方根相对误差
		for (int i = 0; i < svmNodeAndLableBuilder2.getLabel().size(); i++) {
			double truevalue = svmNodeAndLableBuilder2.getLabel().get(i);
			System.out.print("真值:" + truevalue + " ");
			double predictValue = svmPredictor.predict(svmNodeAndLableBuilder2
					.getNodeSet().get(i));
			System.out.print("预测值:" + predictValue + " ");
			// 算误差百分比是否比5.0%大，Esingle
			double Esingle = Math
					.abs((double) ((truevalue - predictValue) / truevalue));
			BigDecimal EsingleBigDecimal = new BigDecimal(Esingle);
			if (EsingleBigDecimal.compareTo(ruleBigDecimal) == 1) {// 大于3，不合格
				ERROR++;
			}
			// 做统计时舍去突变点，突然从180到8，有异常的点,误差大于99%的点,舍去为了好计算Emre，和Ermse
			if (EsingleBigDecimal.compareTo(halfBigDecimal) == 1) {
				Ecount--;
			} else {
				Emre += Esingle;// 所有的单个误差百分比加起来后除以例子总数
				Ermse += Math.pow(Esingle, 2);
			}

			System.out.println(Esingle + "---误差百分比:" + Esingle * 100 + "%|"
					+ Math.abs(truevalue - predictValue) + "/" + truevalue);
		}
		Ermse = Math.sqrt(Ermse / Ecount);
		System.out.println("Error：" + ERROR);
		System.out.println("Ermse：" + Ermse);
		System.out.println("Emre：" + Emre / Ecount);
		System.out.println("合格率r："
				+ (1.0 - ((double) ERROR / (double) ALLCOUNT)));
		// 写回数据库
	}

	public static void predictWithTB() {
		PredictService predictService = new PredictService();
		String jsonArrayStringB = predictService.getJsonArray("1800002459",
				"p", "2015-06-08 00:00:00", "2015-06-13 00:00:00");
		System.out.println(jsonArrayStringB);
		String jsonArrayStringT = predictService.getJsonArray("1800002459",
				"t", "2015-06-08 00:00:00", "2015-06-13 00:00:00");
		System.out.println(jsonArrayStringT);
		// 构造nodes和lable
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayStringB, jsonArrayStringT);
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder();
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		// 训练获取模型
		svm_model svmModel = svmTrainer.train();
		// 保存模型，方便实验
		try {
			svm.svm_save_model("modelwithTB", svmModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 预测
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
		String jsonArrayStringBtest = predictService.getJsonArray("1800002459",
				"p", "2015-06-13 00:00:00", "2015-06-14 00:00:00");
		System.out.println(jsonArrayStringBtest);
		String jsonArrayStringTtest = predictService.getJsonArray("1800002459",
				"t", "2015-06-13 00:00:00", "2015-06-14 00:00:00");
		System.out.println(jsonArrayStringTtest);
		SVMNodeAndLableBuilder svmNodeAndLableBuilderTest = new SVMNodeAndLableBuilder(
				jsonArrayStringBtest, jsonArrayStringTtest);
		int ALLCOUNT = svmNodeAndLableBuilderTest.getLabel().size();// 总数
		System.out.println("总数：" + ALLCOUNT);
		int Ecount = ALLCOUNT;
		int ERROR = 0;
		double rule = 0.05;
		BigDecimal ruleBigDecimal = new BigDecimal(rule);
		double half = 0.99;
		BigDecimal halfBigDecimal = new BigDecimal(half);
		double Emre = 0;// 平均相对误差
		double Ermse = 0;// 均方根相对误差
		for (int i = 0; i < svmNodeAndLableBuilderTest.getLabel().size(); i++) {
			double truevalue = svmNodeAndLableBuilderTest.getLabel().get(i);
			System.out.print("真值:" + truevalue + " ");
			double predictValue = svmPredictor
					.predict(svmNodeAndLableBuilderTest.getNodeSet().get(i));
			System.out.print("预测值:" + predictValue + " ");
			// 算误差百分比是否比5.0%大，Esingle
			double Esingle = Math
					.abs((double) ((truevalue - predictValue) / truevalue));
			BigDecimal EsingleBigDecimal = new BigDecimal(Esingle);
			if (EsingleBigDecimal.compareTo(ruleBigDecimal) == 1) {// 大于3，不合格
				ERROR++;
			}
			// 做统计时舍去突变点，突然从180到8，有异常的点,误差大于99%的点,舍去为了好计算Emre，和Ermse
			if (EsingleBigDecimal.compareTo(halfBigDecimal) == 1) {
				Ecount--;
			} else {
				Emre += Esingle;// 所有的单个误差百分比加起来后除以例子总数
				Ermse += Math.pow(Esingle, 2);
			}

			System.out.println(Esingle + "---误差百分比:" + Esingle * 100 + "%|"
					+ Math.abs(truevalue - predictValue) + "/" + truevalue);
		}
		Ermse = Math.sqrt(Ermse / Ecount);
		System.out.println("Error：" + ERROR);
		System.out.println("Ermse：" + Ermse);
		System.out.println("Emre：" + Emre / Ecount);
		System.out.println("合格率r："
				+ (1.0 - ((double) ERROR / (double) ALLCOUNT)));
		// 写回数据库
	}

	public static void predictWithHB() {
		PredictService predictService = new PredictService();
		String jsonArrayStringB = predictService.getJsonArray("1800002459",
				"p", "2015-06-08 00:00:00", "2015-06-13 00:00:00");
		System.out.println(jsonArrayStringB);
		//
		String jsonArrayStringH1 = predictService.getJsonArray("1800002459",
				"p", "2015-06-01 00:00:00", "2015-06-06 00:00:00");
		System.out.println(jsonArrayStringH1);// last week
		
		
		String jsonArrayStringH2 = predictService.getJsonArray("1800002459",
				"p", "2015-06-07 00:00:00", "2015-06-12 00:00:00");
		System.out.println(jsonArrayStringH2);// yesterday
		// 构造nodes和lable
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayStringB, jsonArrayStringH1, jsonArrayStringH2);
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder();
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		// 训练获取模型
		svm_model svmModel = svmTrainer.train();
		// 保存模型，方便实验
		try {
			svm.svm_save_model("modelwithHB", svmModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 预测
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
		String jsonArrayStringBtest = predictService.getJsonArray("1800002459",
				"p", "2015-06-13 00:00:00", "2015-06-14 00:00:00");
		System.out.println(jsonArrayStringBtest);
		String jsonArrayStringH1test = predictService
				.getJsonArray("1800002459", "p", "2015-06-06 00:00:00",
						"2015-06-07 00:00:00");
		System.out.println(jsonArrayStringH1test);
		String jsonArrayStringH2test = predictService
				.getJsonArray("1800002459", "p", "2015-06-12 00:00:00",
						"2015-06-13 00:00:00");
		System.out.println(jsonArrayStringH2test);
		SVMNodeAndLableBuilder svmNodeAndLableBuilderTest = new SVMNodeAndLableBuilder(
				jsonArrayStringBtest, jsonArrayStringH1test,
				jsonArrayStringH2test);
		int ALLCOUNT = svmNodeAndLableBuilderTest.getLabel().size();// 总数
		System.out.println("总数：" + ALLCOUNT);
		int Ecount = ALLCOUNT;
		int ERROR = 0;
		double rule = 0.05;
		BigDecimal ruleBigDecimal = new BigDecimal(rule);
		double half = 0.99;
		BigDecimal halfBigDecimal = new BigDecimal(half);
		double Emre = 0;// 平均相对误差
		double Ermse = 0;// 均方根相对误差
		for (int i = 0; i < svmNodeAndLableBuilderTest.getLabel().size(); i++) {
			double truevalue = svmNodeAndLableBuilderTest.getLabel().get(i);
			System.out.print("真值:" + truevalue + " ");
			double predictValue = svmPredictor
					.predict(svmNodeAndLableBuilderTest.getNodeSet().get(i));
			System.out.print("预测值:" + predictValue + " ");
			// 算误差百分比是否比5.0%大，Esingle
			double Esingle = Math
					.abs((double) ((truevalue - predictValue) / truevalue));
			BigDecimal EsingleBigDecimal = new BigDecimal(Esingle);
			if (EsingleBigDecimal.compareTo(ruleBigDecimal) == 1) {// 大于3，不合格
				ERROR++;
			}
			// 做统计时舍去突变点，突然从180到8，有异常的点,误差大于99%的点,舍去为了好计算Emre，和Ermse
			if (EsingleBigDecimal.compareTo(halfBigDecimal) == 1) {
				Ecount--;
			} else {
				Emre += Esingle;// 所有的单个误差百分比加起来后除以例子总数
				Ermse += Math.pow(Esingle, 2);
			}

			System.out.println(Esingle + "---误差百分比:" + Esingle * 100 + "%|"
					+ Math.abs(truevalue - predictValue) + "/" + truevalue);
		}
		Ermse = Math.sqrt(Ermse / Ecount);
		System.out.println("Error：" + ERROR);
		System.out.println("Ermse：" + Ermse);
		System.out.println("Emre：" + Emre / Ecount);
		System.out.println("合格率r："
				+ (1.0 - ((double) ERROR / (double) ALLCOUNT)));
		// 写回数据库
	}

	public static void predictWithTHB() {
		PredictService predictService = new PredictService();
		String jsonArrayStringB = predictService.getJsonArray("1800002459",
				"p", "2015-06-09 00:00:00", "2015-06-11 00:00:00");
		System.out.println(jsonArrayStringB);
		String jsonArrayStringT = predictService.getJsonArray("1800002459",
				"t", "2015-06-09 00:00:00", "2015-06-11 00:00:00");
		System.out.println(jsonArrayStringT);
		String jsonArrayStringH1 = predictService.getJsonArray("1800002459",
				"p", "2015-06-02 00:00:00", "2015-06-05 00:00:00");
		System.out.println(jsonArrayStringH1);// last week
		String jsonArrayStringH2 = predictService.getJsonArray("1800002459",
				"p", "2015-06-08 00:00:00", "2015-06-10 00:00:00");
		System.out.println(jsonArrayStringH2);// yesterday
		// 构造nodes和lable
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayStringB, jsonArrayStringT, jsonArrayStringH1,
				jsonArrayStringH2);
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder();
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		// 训练获取模型
		svm_model svmModel = svmTrainer.train();
		// 保存模型，方便实验
		try {
			svm.svm_save_model("modelwithTHB", svmModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 预测
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
		String jsonArrayStringBtest = predictService.getJsonArray("1800002459",
				"p", "2015-06-11 00:00:00", "2015-06-11 12:00:00");
		System.out.println(jsonArrayStringBtest);
		String jsonArrayStringTtest = predictService.getJsonArray("1800002459",
				"t", "2015-06-11 00:00:00", "2015-06-12 11:00:00");
		System.out.println(jsonArrayStringTtest);
		String jsonArrayStringH1test = predictService
				.getJsonArray("1800002459", "p", "2015-06-04 00:00:00",
						"2015-06-04 12:00:00");
		System.out.println(jsonArrayStringH1test);
		String jsonArrayStringH2test = predictService
				.getJsonArray("1800002459", "p", "2015-06-10 00:00:00",
						"2015-06-10 12:00:00");
		System.out.println(jsonArrayStringH2test);
		SVMNodeAndLableBuilder svmNodeAndLableBuilderTest = new SVMNodeAndLableBuilder(
				jsonArrayStringBtest, jsonArrayStringTtest,
				jsonArrayStringH1test, jsonArrayStringH2test);
		int ALLCOUNT = svmNodeAndLableBuilderTest.getLabel().size();// 总数
		System.out.println("总数：" + ALLCOUNT);
		int Ecount = ALLCOUNT;
		int ERROR = 0;
		double rule = 0.05;
		BigDecimal ruleBigDecimal = new BigDecimal(rule);
		double half = 0.99;
		BigDecimal halfBigDecimal = new BigDecimal(half);
		double Emre = 0;// 平均相对误差
		double Ermse = 0;// 均方根相对误差
		for (int i = 0; i < svmNodeAndLableBuilderTest.getLabel().size(); i++) {
			double truevalue = svmNodeAndLableBuilderTest.getLabel().get(i);
			System.out.print("真值:" + truevalue + " ");
			double predictValue = svmPredictor
					.predict(svmNodeAndLableBuilderTest.getNodeSet().get(i));
			System.out.print("预测值:" + predictValue + " ");
			// 算误差百分比是否比5.0%大，Esingle
			double Esingle = Math
					.abs((double) ((truevalue - predictValue) / truevalue));
			BigDecimal EsingleBigDecimal = new BigDecimal(Esingle);
			if (EsingleBigDecimal.compareTo(ruleBigDecimal) == 1) {// 大于3，不合格
				ERROR++;
			}
			// 做统计时舍去突变点，突然从180到8，有异常的点,误差大于99%的点,舍去为了好计算Emre，和Ermse
			if (EsingleBigDecimal.compareTo(halfBigDecimal) == 1) {
				Ecount--;
			} else {
				Emre += Esingle;// 所有的单个误差百分比加起来后除以例子总数
				Ermse += Math.pow(Esingle, 2);
			}

			System.out.println(Esingle + "---误差百分比:" + Esingle * 100 + "%|"
					+ Math.abs(truevalue - predictValue) + "/" + truevalue);
		}
		Ermse = Math.sqrt(Ermse / Ecount);
		System.out.println("Error：" + ERROR);
		System.out.println("Ermse：" + Ermse);
		System.out.println("Emre：" + Emre / Ecount);
		System.out.println("合格率r："
				+ (1.0 - ((double) ERROR / (double) ALLCOUNT)));
		// 写回数据库
	}

	
	//做出拆分解耦合 分模块，主要用这个函数，最后是最规范的
	
	public TrainResult predictWithTHB(Parameter parameter) {
		//通过dataService获取数据
		DataService dataService=new DataService();
		String jsonArrayStringB = dataService.getJsonArray("1800002459",
				"p", "2015-06-09 00:00:00", "2015-06-11 00:00:00");
		System.out.println(jsonArrayStringB);
		
		String jsonArrayStringT = dataService.getJsonArray("1800002459",
				"t", "2015-06-09 00:00:00", "2015-06-11 00:00:00");
		System.out.println(jsonArrayStringT);
		
		String jsonArrayStringH1 = dataService.getJsonArray("1800002459",
				"p", "2015-06-02 00:00:00", "2015-06-05 00:00:00");
		System.out.println(jsonArrayStringH1);// last week
		
		String jsonArrayStringH2 = dataService.getJsonArray("1800002459",
				"p", "2015-06-08 00:00:00", "2015-06-10 00:00:00");
		System.out.println(jsonArrayStringH2);// yesterday
		
		// 构造nodes和lable
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayStringB, jsonArrayStringT, jsonArrayStringH1,
				jsonArrayStringH2);
		//构造问题
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		//设置参数
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder(parameter.getC(),
				parameter.getGamma());
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		// 训练获取模型
		svm_model svmModel = svmTrainer.train();
		// 保存模型，方便实验
		try {
			svm.svm_save_model("modelwithTHB", svmModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 开始预测 需要手动修改代码来修改时间 
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
		String jsonArrayStringBtest = dataService.getJsonArray("1800002459",
				"p", "2015-06-11 00:00:00", "2015-06-11 12:00:00");
		System.out.println(jsonArrayStringBtest);
		String jsonArrayStringTtest = dataService.getJsonArray("1800002459",
				"t", "2015-06-11 00:00:00", "2015-06-12 11:00:00");
		System.out.println(jsonArrayStringTtest);
		String jsonArrayStringH1test = dataService
				.getJsonArray("1800002459", "p", "2015-06-04 00:00:00",
						"2015-06-04 12:00:00");
		System.out.println(jsonArrayStringH1test);
		String jsonArrayStringH2test = dataService
				.getJsonArray("1800002459", "p", "2015-06-10 00:00:00",
						"2015-06-10 12:00:00");
		System.out.println(jsonArrayStringH2test);
		
		//构造用于预测的向量数据
		SVMNodeAndLableBuilder svmNodeAndLableBuilderTest = new SVMNodeAndLableBuilder(
				jsonArrayStringBtest, jsonArrayStringTtest,
				jsonArrayStringH1test, jsonArrayStringH2test);
		int ALLCOUNT = svmNodeAndLableBuilderTest.getLabel().size();// 总数
		System.out.println("总数：" + ALLCOUNT);
		int Ecount = ALLCOUNT;
		int ERROR = 0;
		//定义5%的阈值来决定是否不合格，误差过大
		double rule = 0.05;
		BigDecimal ruleBigDecimal = new BigDecimal(rule);
		double half = 0.99;
		BigDecimal halfBigDecimal = new BigDecimal(half);
		double Emre = 0;// 平均相对误差
		double Ermse = 0;// 均方根相对误差

		List<DataPoint> resultDataPointList = new ArrayList<DataPoint>();

		for (int i = 0; i < svmNodeAndLableBuilderTest.getLabel().size(); i++) {

			double truevalue = svmNodeAndLableBuilderTest.getLabel().get(i);
			System.out.print("真值:" + truevalue + " ");
			double predictValue = svmPredictor
					.predict(svmNodeAndLableBuilderTest.getNodeSet().get(i));
			System.out.print("预测值:" + predictValue + " ");
			// 算误差百分比是否比5.0%大，Esingle
			double Esingle = Math
					.abs((double) ((truevalue - predictValue) / truevalue));
			BigDecimal EsingleBigDecimal = new BigDecimal(Esingle);
			if (EsingleBigDecimal.compareTo(ruleBigDecimal) == 1) {// 大于3，不合格
				ERROR++;
			}
			// 做统计时舍去突变点，突然从180到8，有异常的点,误差大于99%的点,舍去为了好计算Emre，和Ermse
			//以免因为误差太大的突变点影响了计算Emre，和Ermse
			if (EsingleBigDecimal.compareTo(halfBigDecimal) == 1) {
				Ecount--;
			} else {
				Emre += Esingle;// 所有的单个误差百分比加起来后除以例子总数
				Ermse += Math.pow(Esingle, 2);
			}

			DataPoint dataPoint = new DataPoint(truevalue, predictValue,
					Esingle * 100);
			resultDataPointList.add(dataPoint);

			System.out.println(Esingle + "---误差百分比:" + Esingle * 100 + "%|"
					+ Math.abs(truevalue - predictValue) + "/" + truevalue);

		}
		Ermse = Math.sqrt(Ermse / Ecount);
		System.out.println("Error：" + ERROR);
		System.out.println("Ermse：" + Ermse);
		System.out.println("Emre：" + Emre / Ecount);
		System.out.println("合格率r："
				+ (1.0 - ((double) ERROR / (double) ALLCOUNT)));
		ResultDataService resultDataService =new ResultDataService();
		
		return resultDataService.getTrainResult(ALLCOUNT, ERROR, (1.0 - ((double) ERROR / (double) ALLCOUNT)), parameter, resultDataPointList, Ermse, Emre / Ecount);
		

	}



	public static void main(String[] args) {
		//一次测试
		double C = 100;
		double gamma = 0.000001;
		Parameter parameter = new Parameter(C, gamma);
		PredictService predictService=new PredictService();
		predictService.predictWithTHB(parameter);
	}

}
