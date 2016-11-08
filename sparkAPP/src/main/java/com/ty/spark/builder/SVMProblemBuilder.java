package com.ty.spark.builder;

import java.util.List;

import libsvm.svm_node;
import libsvm.svm_problem;

public class SVMProblemBuilder {
	private svm_problem problem;

	public svm_problem getProblem() {
		return problem;
	}

	public void setProblem(svm_problem problem) {
		this.problem = problem;
	}

	public SVMProblemBuilder(List<svm_node[]> nodeSet, List<Double> label) {
		problem = new svm_problem();
		problem.l = nodeSet.size(); // 向量个数
		int dataRange = nodeSet.get(0).length;
		svm_node[][] datas = new svm_node[nodeSet.size()][dataRange]; // 训练集的向量表
		for (int i = 0; i < datas.length; i++) {
			for (int j = 0; j < dataRange; j++) {
				datas[i][j] = nodeSet.get(i)[j];
			}
		}
		double[] lables = new double[label.size()]; // a,b 对应的lable
		for (int i = 0; i < lables.length; i++) {
			lables[i] = label.get(i);
		}
		// 因为x y向量表，和lable之能是二维数据和一维数组，所以得通过上门的循环转化list成数组。
		problem.x = datas; // 训练集向量表
		problem.y = lables; // 对应的lable数组
	}

}

