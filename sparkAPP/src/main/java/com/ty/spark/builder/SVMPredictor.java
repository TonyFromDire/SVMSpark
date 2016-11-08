package com.ty.spark.builder;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class SVMPredictor {
	svm_model svm_model;

	public SVMPredictor(svm_model svm_model) {
		this.svm_model = svm_model;
	}

	public double predict(svm_node[] predictNodes) {
		double predictValue = svm.svm_predict(this.svm_model, predictNodes);
		return predictValue;
	}

	public svm_model getSvm_model() {
		return svm_model;
	}

	public void setSvm_model(svm_model svm_model) {
		this.svm_model = svm_model;
		
	}
}
