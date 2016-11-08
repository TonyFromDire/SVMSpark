package com.ty.spark.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import libsvm.svm_model;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.ty.spark.builder.SVMNodeAndLableBuilder;
import com.ty.spark.builder.SVMParamBuilder;
import com.ty.spark.builder.SVMPredictor;
import com.ty.spark.builder.SVMProblemBuilder;
import com.ty.spark.builder.SVMTrainer;


public class HttpHelper {

	public HttpHelper(String url) {
		this.url = url;
	}

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getrequest() {
		StringBuilder resultStringBuilder = new StringBuilder();
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
		// Create a method instance.
		GetMethod method = new GetMethod(this.url);
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			// Read the response body.
			InputStream responseBodyAsStream = method.getResponseBodyAsStream();
			byte[] responseBody = input2byte(responseBodyAsStream);
			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			resultStringBuilder.append(new String(responseBody));
			//System.out.println(resultStringBuilder.toString());
		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}
		return resultStringBuilder.toString();
	}

	public static void main(String[] args) {
		RequestURLBuilder requestURLBuilder = new RequestURLBuilder(
				"1800002459", "p", "2015-07-15 00:00:00", "2015-07-15 01:00:00");
		String urlString = requestURLBuilder.getURL();
		System.out.println(urlString);
		HttpHelper httpHelper = new HttpHelper(urlString);
		String jsonArrayPString = httpHelper.getrequest();

		
		
		
		
		
		SVMNodeAndLableBuilder svmNodeAndLableBuilder = new SVMNodeAndLableBuilder(
				jsonArrayPString);
		SVMProblemBuilder svmProblemBuilder = new SVMProblemBuilder(
				svmNodeAndLableBuilder.getNodeSet(),
				svmNodeAndLableBuilder.getLabel());
		SVMParamBuilder svmParamBuilder = new SVMParamBuilder();
		SVMTrainer svmTrainer = new SVMTrainer(svmProblemBuilder.getProblem(),
				svmParamBuilder.getParam());
		svm_model svmModel = svmTrainer.train();
		SVMPredictor svmPredictor = new SVMPredictor(svmModel);
	}

	public void test() {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(
				"http://10.109.247.121/electric_backend/public/transformer/ems/p?devid=1800002459&beginDateTime=2015-07-15%2000:00:00&endDateTime=2015-07-15%2001:00:00");

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
				new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			// Read the response body.
			byte[] responseBody = method.getResponseBody();

			// Deal with the response.
			// Use caution: ensure correct character encoding and is not binary
			// data
			System.out.println(new String(responseBody));

		} catch (HttpException e) {
			System.err.println("Fatal protocol violation: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal transport error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// Release the connection.
			method.releaseConnection();
		}

	}

	public static final byte[] input2byte(InputStream inStream)
			throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
}

