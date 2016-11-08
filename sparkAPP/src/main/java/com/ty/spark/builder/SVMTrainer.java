package com.ty.spark.builder;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVMTrainer {
	private svm_problem svm_problem;
	private svm_parameter svm_parameter;

	public SVMTrainer(svm_problem svm_problem, svm_parameter svm_parameter) {
		this.svm_problem = svm_problem;
		this.svm_parameter = svm_parameter;
	}

	public void checkParameter() {
		svm.svm_check_parameter(svm_problem, svm_parameter);
		System.out.println("check parameter ok.");
	}

	public svm_model train() {
		svm_model svm_model = svm.svm_train(svm_problem, svm_parameter);
		return svm_model;
	}

	public svm_problem getSvm_problem() {
		return svm_problem;
	}

	public void setSvm_problem(svm_problem svm_problem) {
		this.svm_problem = svm_problem;
	}

	public svm_parameter getSvm_parameter() {
		return svm_parameter;
	}

	public void setSvm_parameter(svm_parameter svm_parameter) {
		this.svm_parameter = svm_parameter;
	}

}
