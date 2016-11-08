package com.ty.spark.sparkAPP;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;

import scala.Function;
import scala.Tuple2;

public class TestUse {
	// a wordCount DEMO
	public static void main(String[] args) {
		SparkConf conf = new SparkConf().setAppName(
				"Spark WordCount written by java").setMaster("local");

		JavaSparkContext sc = new JavaSparkContext(conf); // 其底层就是scala的sparkcontext

		JavaRDD<String> lines = sc
				.textFile("D://readme.txt");

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
}
