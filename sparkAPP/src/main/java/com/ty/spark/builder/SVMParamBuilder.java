package com.ty.spark.builder;

import libsvm.svm_parameter;

public class SVMParamBuilder {
	private svm_parameter param;

	public svm_parameter getParam() {
		return param;
	}

	public void setParam(svm_parameter param) {
		this.param = param;
	}
	//构造方法
	public SVMParamBuilder() {
		this.param = new svm_parameter();
		param.svm_type = svm_parameter.EPSILON_SVR;// 需实现确定参数
		param.kernel_type = svm_parameter.RBF;
		// ＲＢＦ是局部性很强的核函数，其内推能力即学习能力，随着参数σ(param.gamma)的增大而减弱
		// 分析该对核函数的影响，可以得到：当参数设定太小，容易造成过学习的情况；
		// 当参数设定太大，会导致分类器泛化能力变差。
		//set the parameter C of C-SVC,epsilon-SVR, and nu-SVR (default 1)
		//set the epsilon in lossfunction of epsilon-SVR (default 0.1)
		//set gamma in kernel function(default 1/num_features)
		param.cache_size = 512;
		param.p = 1.0;
		param.gamma = 0.000001;
		param.C = 100;
		
		
		// 进行交叉验证
		// svm.svm_cross_validation(const struct svm_problem *prob, const struct
		// svm_parameter *param, int nr_fold, double *target)方法
		// 参数含义如下：
		// prob：待解决的分类问题，就是样本数据。
		// param：svm训练参数。
		// nr_fold：顾名思义就是k折交叉验证中的k,验证次数?如果k=n的话就是留一法了。
		// target：预测值，如果是分类问题的话就是类别标签了。
		// double[] target = new double[problem.l];
		//svm.svm_cross_validation(problem, param, 10, target );
	}
	public SVMParamBuilder(double C,double gamma) {
		this.param = new svm_parameter();
		param.svm_type = svm_parameter.EPSILON_SVR;// 需实现确定参数
		param.kernel_type = svm_parameter.RBF;
		param.cache_size = 512;
		param.p = 1.0;
		param.gamma = gamma;
		param.C = C;
	}

}