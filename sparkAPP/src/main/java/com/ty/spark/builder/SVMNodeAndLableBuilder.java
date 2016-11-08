package com.ty.spark.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import libsvm.svm_node;

import org.json.JSONArray;

public class SVMNodeAndLableBuilder {
	// 这个类比较核心，算法模型怎么构造，就得怎么改动。
	private JSONArray bjsonArray;// 历史节点数据b1-b4存到nodeset，第五个点作为分类标签l1存到lable数组
	private JSONArray tjsonArray;
	private JSONArray h1jsonArray;
	private JSONArray h2jsonArray;
	private List<svm_node[]> nodeSet;
	private List<Double> label;

	public SVMNodeAndLableBuilder(String jsonArrayString) {
		this.bjsonArray = new JSONArray(jsonArrayString);
		this.label = new ArrayList<Double>();
		this.nodeSet = new ArrayList<svm_node[]>();
		build(this.bjsonArray);
	}

	public SVMNodeAndLableBuilder(String jsonArrayStringB,
			String jsonArrayStringT) {
		this.bjsonArray = new JSONArray(jsonArrayStringB);
		this.tjsonArray = new JSONArray(jsonArrayStringT);
		this.label = new ArrayList<Double>();
		this.nodeSet = new ArrayList<svm_node[]>();
		build(this.bjsonArray, this.tjsonArray);
	}

	public SVMNodeAndLableBuilder(String jsonArrayStringB,
			String jsonArrayStringH1, String jsonArrayStringH2) {
		this.bjsonArray = new JSONArray(jsonArrayStringB);
		this.h1jsonArray = new JSONArray(jsonArrayStringH1);
		this.h2jsonArray = new JSONArray(jsonArrayStringH2);
		this.label = new ArrayList<Double>();
		this.nodeSet = new ArrayList<svm_node[]>();
		build(this.bjsonArray, this.h1jsonArray, this.h2jsonArray);
	}

	public SVMNodeAndLableBuilder(String jsonArrayStringB,
			String jsonArrayStringT, String jsonArrayStringH1,
			String jsonArrayStringH2) {
		this.bjsonArray = new JSONArray(jsonArrayStringB);
		this.tjsonArray = new JSONArray(jsonArrayStringT);
		this.h1jsonArray = new JSONArray(jsonArrayStringH1);
		this.h2jsonArray = new JSONArray(jsonArrayStringH2);
		this.label = new ArrayList<Double>();
		this.nodeSet = new ArrayList<svm_node[]>();
		build(this.bjsonArray, this.tjsonArray, this.h1jsonArray,
				this.h2jsonArray);
	}

	public List<svm_node[]> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(List<svm_node[]> nodeSet) {
		this.nodeSet = nodeSet;
	}

	public List<Double> getLabel() {
		return label;
	}

	public void setLabel(List<Double> label) {
		this.label = label;
	}

	private void build(JSONArray bjsonArray) {
		// build NodesAndLables
		// 根据自己算法模型的设计实现构造模型的函数
		// b1,b2,b3,b4作为之前的时间节点，l1(b5)为分类标签（回归预测值）
		int blength = 4;
		for (int i = 0; i < bjsonArray.length() - 4; i++) {
			// JSONObject b1 = jsonArray.getJSONObject(i);
			// JSONObject b2 = jsonArray.getJSONObject(i + 1);
			// JSONObject b3 = jsonArray.getJSONObject(i + 2);
			// JSONObject b4 = jsonArray.getJSONObject(i + 3);// 加到length-1
			// JSONObject lable = jsonArray.getJSONObject(i + 4);

			svm_node[] vector = new svm_node[blength];
			for (int j = 0; j < vector.length; j++) {// 0,1,2,3 node[j]
				svm_node node = new svm_node();
				node.index = j;
				node.value = Double.parseDouble(bjsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j] = node;
			}
			nodeSet.add(vector);

			double lableValue = Double.parseDouble(bjsonArray.getJSONObject(
					i + blength).getString("p"));
			label.add(lableValue);
		}
	}

	private void build(JSONArray bJsonArray, JSONArray tJsonArray) {
		// build NodesAndLables
		// 根据自己算法模型的设计实现构造模型的函数
		// b1,b2,b3,b4作为之前的时间节点，l1(b5)为分类标签（回归预测值）
		// t1为距离预测时间最近的的温度，也就是b4的温度
		int blength = 4;
		for (int i = 0; i < bJsonArray.length() - 4; i++) {
			// JSONObject b1 = jsonArray.getJSONObject(i);
			// JSONObject b2 = jsonArray.getJSONObject(i + 1);
			// JSONObject b3 = jsonArray.getJSONObject(i + 2);
			// JSONObject b4 = jsonArray.getJSONObject(i + 3);// 加到length-1
			// JSONObject t1 = tjsonArray.getJSONObject(i + 3);//
			// JSONObject lable = jsonArray.getJSONObject(i + 4);
			svm_node[] vector = new svm_node[blength + 1];// 多了温度t1
			svm_node nodeT = new svm_node();
			nodeT.index = 0;
			nodeT.value = Double.parseDouble(tJsonArray.getJSONObject(i + 3)
					.getString("t"));
			vector[0] = nodeT;
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j + 1;
				node.value = Double.parseDouble(bJsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j + 1] = node;
			}
			nodeSet.add(vector);

			double lableValue = Double.parseDouble(bJsonArray.getJSONObject(
					i + blength).getString("p"));
			label.add(lableValue);
		}
	}

	private void build(JSONArray bJsonArray, JSONArray h1JsonArray,
			JSONArray h2JsonArray) {
		// build NodesAndLables
		// 根据自己算法模型的设计实现构造模型的函数
		// b1,b2,b3,b4作为之前的时间节点，l1(b5)为分类标签（回归预测值）
		// h1，h2为预测时间b5的历史数据，上周，昨天
		int blength = 4;
		for (int i = 0; i < bJsonArray.length() - 4; i++) {
			// JSONObject b1 = jsonArray.getJSONObject(i);
			// JSONObject b2 = jsonArray.getJSONObject(i + 1);
			// JSONObject b3 = jsonArray.getJSONObject(i + 2);
			// JSONObject b4 = jsonArray.getJSONObject(i + 3);// 加到length-1
			// JSONObject h1 = h1jsonArray.getJSONObject(i + 4);//
			// JSONObject h2 = h2jsonArray.getJSONObject(i + 4);//
			// JSONObject lable = jsonArray.getJSONObject(i + 4);
			svm_node[] vector = new svm_node[blength + 2];// 多了历史h1 h2
			svm_node nodeH1 = new svm_node();
			nodeH1.index = 0;
			nodeH1.value = Double.parseDouble(h1JsonArray.getJSONObject(i + 4)
					.getString("p"));
			vector[0] = nodeH1;
			svm_node nodeH2 = new svm_node();
			nodeH2.index = 0;
			nodeH2.value = Double.parseDouble(h1JsonArray.getJSONObject(i + 4)
					.getString("p"));
			vector[1] = nodeH2;
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j + 2;
				node.value = Double.parseDouble(bJsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j + 2] = node;
			}
			nodeSet.add(vector);

			double lableValue = Double.parseDouble(bJsonArray.getJSONObject(
					i + blength).getString("p"));
			label.add(lableValue);
		}
	}

	private void build(JSONArray bJsonArray, JSONArray tJsonArray,
			JSONArray h1JsonArray, JSONArray h2JsonArray) {
		// build NodesAndLables
		// 根据自己算法模型的设计实现构造模型的函数
		// b1,b2,b3,b4作为之前的时间节点，l1(b5)为分类标签（回归预测值）
		// h1，h2为预测时间b5的历史数据，上周，昨天
		// t1为距离预测时间最近的的温度，也就是b4的温度
		int blength = 4;
		//write file
		StringBuilder stringBuilder=new StringBuilder();
		File file = new File("trainTest.txt");
		
		for (int i = 0; i < bJsonArray.length() - 4; i++) {
			// JSONObject b1 = jsonArray.getJSONObject(i);
			// JSONObject b2 = jsonArray.getJSONObject(i + 1);
			// JSONObject b3 = jsonArray.getJSONObject(i + 2);
			// JSONObject b4 = jsonArray.getJSONObject(i + 3);// 加到length-1
			// JSONObject t1 = tjsonArray.getJSONObject(i + 3);//
			// JSONObject h1 = h1jsonArray.getJSONObject(i + 4);//各四个
			// JSONObject h2 = h2jsonArray.getJSONObject(i + 4);//各四个
			// JSONObject lable = jsonArray.getJSONObject(i + 4);
			svm_node[] vector = new svm_node[blength + 12];// 共16个
			
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j;
				node.value = Double.parseDouble(tJsonArray.getJSONObject(i + j)
						.getString("t"));
				vector[j] = node;
			}
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j + 4;
				node.value = Double.parseDouble(h1JsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j + 4] = node;
			}
			
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j + 8;
				node.value = Double.parseDouble(h2JsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j + 8] = node;
			}
			
			for (int j = 0; j < 4; j++) {// 0,1,2,3 node[j] length-1=4
				svm_node node = new svm_node();
				node.index = j + 12;
				node.value = Double.parseDouble(bJsonArray.getJSONObject(i + j)
						.getString("p"));
				vector[j + 12] = node;
			}
			nodeSet.add(vector);
			
			double lableValue = Double.parseDouble(bJsonArray.getJSONObject(
					i + blength).getString("p"));
			label.add(lableValue);
			stringBuilder.append(lableValue+" ");
			for (int j = 0; j < vector.length; j++) {
				stringBuilder.append((j+1)+":"+vector[j].value);
				stringBuilder.append(j==vector.length-1?"\r\n":" ");
			}
		}
//		  try {
//
//			   // if file doesnt exists, then create it
//			   if (!file.exists()) {
//			    file.createNewFile();
//			   }
//
//			   FileWriter fw = new FileWriter(file.getAbsoluteFile());
//			   BufferedWriter bw = new BufferedWriter(fw);
//			   bw.write(stringBuilder.toString());
//			   bw.close();
//
//			   System.out.println("Done");
//
//			  } catch (IOException e) {
//			   e.printStackTrace();
//			  }
		
		
	}
}
