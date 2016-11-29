package com.ty.spark.domain;

import java.io.Serializable;




public class Parameter implements Serializable{
	private Double C;
	private Double gamma;

	private int svm_type;
	private int kernel_type;

	public Parameter(double C, double gamma, int svm_type, int kernel_type) {
		this.C = C;
		this.gamma = gamma;
		this.svm_type = svm_type;
		this.kernel_type = kernel_type;
	}

	public Parameter(double C, double gamma) {
		this.C = C;
		this.gamma = gamma;
	}

	public int getSvm_type() {
		return svm_type;
	}

	public void setSvm_type(int svm_type) {
		this.svm_type = svm_type;
	}

	public int getKernel_type() {
		return kernel_type;
	}

	public void setKernel_type(int kernel_type) {
		this.kernel_type = kernel_type;
	}

	public Double getC() {
		return C;
	}

	public void setC(Double c) {
		C = c;
	}

	public Double getGamma() {
		return gamma;
	}

	public void setGamma(Double gamma) {
		this.gamma = gamma;
	}

	// /* svm_type */
	// public static final int C_SVC = 0;
	// public static final int NU_SVC = 1;
	// public static final int ONE_CLASS = 2;
	// public static final int EPSILON_SVR = 3;
	// public static final int NU_SVR = 4;
	//
	// /* kernel_type */
	// public static final int LINEAR = 0;
	// public static final int POLY = 1;
	// public static final int RBF = 2;
	// public static final int SIGMOID = 3;
	// public static final int PRECOMPUTED = 4;

}
