package com.ty.spark.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import com.ty.spark.domain.DataPoint;
import com.ty.spark.domain.Parameter;
import com.ty.spark.domain.TrainResult;

public class ResultDataService {
	
	//一次训练预测后的，返回整体的结果情况，存在一个对象中
	public TrainResult getTrainResult(int total, int error, Double rate,
			Parameter parameter, List<DataPoint> dataList, Double Ermse,
			Double Emre) {
		TrainResult trainResult = new TrainResult(total, error, rate,
				parameter, dataList, Ermse, Emre);
		return trainResult;
	}
	//把一次训练的结果TrainResult写到一个txt文件中
	public void writerTxt(TrainResult trainResult,String startTime) {
		BufferedWriter fw = null;
		try {
			File file = new File("D://text.txt");
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			fw.append("我写入的内容");
			fw.newLine();//换行 .append("\r\n") 也行
			fw.append("我又写入的内容");
			fw.flush(); // 全部写入缓存中的内容
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
