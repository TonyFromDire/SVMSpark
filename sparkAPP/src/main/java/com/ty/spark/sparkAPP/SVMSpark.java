package com.ty.spark.sparkAPP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import com.ty.spark.domain.Parameter;
import com.ty.spark.service.ParameterService;

import scala.Tuple2;

public class SVMSpark implements Serializable{
	//https://github.com/TonyFromDire/SVMSpark.git

	public static void main(String[] args) {
		SVMSpark svmSpark=new SVMSpark();
		svmSpark.sparkSVM();
//		ParameterService parameterService = new ParameterService();
//		SparkConf conf = new SparkConf().setAppName(
//				"Spark WordCount written by java").setMaster("local");
//		JavaSparkContext sc = new JavaSparkContext(conf); // 其底层就是scala的sparkcontext
//		List<Parameter> data = parameterService.getParameterList();
//		System.out.println("list size:"+data.size());
//		// 并行集合，是通过对于驱动程序中的集合调用JavaSparkContext.parallelize来构建的RDD
//		JavaRDD<Parameter> parameterSDD = sc.parallelize(data);
//		parameterSDD=parameterSDD.repartition(10);
//		
//		JavaPairRDD<Parameter, Double> pairs = parameterSDD
//				.mapToPair(new PairFunction<Parameter, Parameter, Double>() {
//					public Tuple2<Parameter, Double> call(Parameter parameter)
//							throws Exception {
//						// TODO Auto-generated method stub
//						//通过率默认0
//						Tuple2<Parameter, Double> tuple2 = new Tuple2<Parameter, Double>(
//								parameter, (double) 0);
//						//训练前
//						System.out.println("before:");
//						System.out.println("C:" + tuple2._1.getC());
//						System.out.println("gamma:" + tuple2._1.getGamma());
//						System.out.println("passRate:" + tuple2._2);
//						//调用SVM过程算出newPassRate
//						//假设调用SVM训练接口得出newPassRate
//						double newPassRate=0.5;
//						//训练后
//						
//						Tuple2<Parameter, Double> tuple2New = new Tuple2<Parameter, Double>(
//								parameter, newPassRate);
//						System.out.println("after:");
//						System.out.println("C:" + tuple2New._1.getC());
//						System.out.println("gamma:" + tuple2New._1.getGamma());
//						System.out.println("passRate:" + tuple2New._2);
//						return tuple2New;
//					}
//				});
//
//		
//		//暂时用不上reduce来统计汇总结果
////		JavaPairRDD<Parameter, Double> parameterTest = pairs
////				.reduceByKey(new Function2<Double, Double, Double>() { // 对相同的Key，进行Value的累计（包括Local和Reducer级别同时Reduce）
////					public Double call(Double v1, Double v2) throws Exception {
////						// TODO Auto-generated method stub
////						return v1 + v2;
////					}
////				});
//		pairs.foreach(new VoidFunction<Tuple2<Parameter, Double>>() {
//			public void call(Tuple2<Parameter, Double> pairs) throws Exception {
//				// TODO Auto-generated method stub
//				System.out.println("C : "+pairs._1.getC() +"gamma : "+pairs._1.getGamma()+ " ,rate: " + pairs._2);
//			}
//		});
//		sc.close();

	}

	// RDD构造方式 1：并行化一个已经存在于驱动程序中的集合创建RDD，nice！
	// List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
	// //并行集合，是通过对于驱动程序中的集合调用JavaSparkContext.parallelize来构建的RDD
	// JavaRDD<Integer> distData = sc.parallelize(data);
	// 定义lineLengths作为Map转换的结果 由于惰性，不会立即计算lineLengths
	// 第一个参数为传入的内容，第二个参数为函数操作完后返回的结果类型
	// JavaRDD<Integer> lineLengths = lines.map(new Function<String, Integer>()
	// {
	// public Integer call(String s) {
	// System.out.println("每行长度"+s.length());
	// return s.length(); }
	// });
	// RDD构造方式 2：读文件 JavaRDD<String> lines =
	// sc.textFile("hdfs://master:9000/testFile/README.md");
	// 如何获取RDD:
	// a.从共享的文件系统获取，（如：HDFS）
	// b.通过已存在的RDD转换
	// c.将已存在scala集合（只要是Seq对象）并行化 ，通过调用SparkContext的parallelize方法实现
	// d.改变现有RDD的之久性；RDD是懒散，短暂的。（RDD的固化：cache缓存至内错； save保存到分布式文件系统）
	public void sparkSVM() {
		ParameterService parameterService = new ParameterService();
		SparkConf conf = new SparkConf().setAppName(
				"Spark WordCount written by java").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf); // 其底层就是scala的sparkcontext
		List<Parameter> data = parameterService.getParameterList();
		System.out.println("list size:"+data.size());
		// 并行集合，是通过对于驱动程序中的集合调用JavaSparkContext.parallelize来构建的RDD
		JavaRDD<Parameter> parameterSDD = sc.parallelize(data);
		parameterSDD=parameterSDD.repartition(10);
		
		JavaPairRDD<Parameter, Double> pairs = parameterSDD
				.mapToPair(new PairFunction<Parameter, Parameter, Double>() {
					public Tuple2<Parameter, Double> call(Parameter parameter)
							throws Exception {
						// TODO Auto-generated method stub
						//通过率默认0
						Tuple2<Parameter, Double> tuple2 = new Tuple2<Parameter, Double>(
								parameter, (double) 0);
						//训练前
						System.out.println("before:");
						System.out.println("C:" + tuple2._1.getC());
						System.out.println("gamma:" + tuple2._1.getGamma());
						System.out.println("passRate:" + tuple2._2);
						//调用SVM过程算出newPassRate
						//假设调用SVM训练接口得出newPassRate
						double newPassRate=trainAndPridictByParameter(tuple2);
						//训练后
						
						Tuple2<Parameter, Double> tuple2New = new Tuple2<Parameter, Double>(
								parameter, newPassRate);
						System.out.println("after:");
						System.out.println("C:" + tuple2New._1.getC());
						System.out.println("gamma:" + tuple2New._1.getGamma());
						System.out.println("passRate:" + tuple2New._2);
						return tuple2New;
					}
				});

		
		//暂时用不上reduce来统计汇总结果
//		JavaPairRDD<Parameter, Double> parameterTest = pairs
//				.reduceByKey(new Function2<Double, Double, Double>() { // 对相同的Key，进行Value的累计（包括Local和Reducer级别同时Reduce）
//					public Double call(Double v1, Double v2) throws Exception {
//						// TODO Auto-generated method stub
//						return v1 + v2;
//					}
//				});
		pairs.foreach(new VoidFunction<Tuple2<Parameter, Double>>() {
			public void call(Tuple2<Parameter, Double> pairs) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("C : "+pairs._1.getC() +"gamma : "+pairs._1.getGamma()+ " ,rate: " + pairs._2);
			}
		});
		sc.close();

	}
	public void testWordCount() {
		ParameterService parameterService = new ParameterService();
		SparkConf conf = new SparkConf().setAppName(
				"Spark WordCount written by java").setMaster("local");

		JavaSparkContext sc = new JavaSparkContext(conf); // 其底层就是scala的sparkcontext

		JavaRDD<String> lines = sc.textFile("D://readme.txt");

		JavaRDD<String> words = lines
				.flatMap(new FlatMapFunction<String, String>() { // 如果是scala由于Sam转化所以可以写成一行代码
					public Iterable<String> call(String line) throws Exception {
						// TODO Auto-generated method stub
						return Arrays.asList(line.split(" "));

					}
				});

		JavaPairRDD<String, Integer> pairs = words
				.mapToPair(new PairFunction<String, String, Integer>() {
					public Tuple2<String, Integer> call(String word)
							throws Exception {
						// TODO Auto-generated method stub
						return new Tuple2<String, Integer>(word, 1);
					}
				});

		JavaPairRDD<String, Integer> wordsCount = pairs
				.reduceByKey(new Function2<Integer, Integer, Integer>() { // 对相同的Key，进行Value的累计（包括Local和Reducer级别同时Reduce）
					public Integer call(Integer v1, Integer v2)
							throws Exception {
						// TODO Auto-generated method stub
						return v1 + v2;
					}
				});

		wordsCount.foreach(new VoidFunction<Tuple2<String, Integer>>() {
			public void call(Tuple2<String, Integer> pairs) throws Exception {
				// TODO Auto-generated method stub
				System.out.println(pairs._1 + " : " + pairs._2);
			}
		});
		sc.close();
	}
	public Double trainAndPridictByParameter(Tuple2<Parameter, Double> test) {
		
		return test._2()+0.133;
	}
	
	private void writerTxt() {
		BufferedWriter fw = null;
		try {
			File file = new File("D://text.txt");
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); // 指定编码格式，以免读取时中文字符异常
			fw.append("我写入的内容");
			fw.newLine();
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
