package com.cluster.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;

import com.cluster.model.ClusterPoint;
import com.cluster.model.DataPoint;

public class Services {

	// CLIQUE相关变量
	public static int densityThreshold = 40; // 密度阈值，聚类中每个单元含有点的最小数目
	public static double densityRatioThreshold = 0.4;// 新单元与簇的密度比最小值
	public static int cellColumns = 400;// 列数
	public static int cellRows = 400;// 行数

	public static int CLASSID = 0;

	public static double getDistance(DataPoint dp1, DataPoint dp2) {
		double distance = 0.0;
		double[] dim1 = dp1.getDimension();
		double[] dim2 = dp2.getDimension();
		if (dim1.length == dim2.length) {

			// 经纬度的欧式距离
			for (int i = 0; i < dim1.length; i++) {
				double temp = Math.pow((dim1[i] - dim2[i]), 2);
				distance = distance + temp;
			}
			distance = Math.pow(distance, 0.5);

			// 球面距离
			/*
			 * MLatA=dim1[0]*Math.PI/180; MLonA=dim1[1]*Math.PI/180;
			 * MLatB=dim2[0]*Math.PI/180; MLonB=dim2[1]*Math.PI/180;
			 */

			/*
			 * double cValue =
			 * Math.sin(dim1[0]*Math.PI/180)*Math.sin(dim2[0]*Math
			 * .PI/180)*Math.cos(dim1[1]*Math.PI/180-dim2[1]*Math.PI/180) +
			 * Math.cos(dim1[0]*Math.PI/180)*Math.cos(dim2[0]*Math.PI/180);
			 * distance=6371393*Math.acos(cValue)*Math.PI/180;
			 */

		}
		return distance;
	}

	public static double getDistance_t(DataPoint dp1, DataPoint dp2) {
		long days = 0;
		Date timestamp1 = dp1.getTimestamp();
		Date timestamp2 = dp2.getTimestamp();
		long diff = Math.abs(timestamp2.getTime() - timestamp1.getTime());
		days = diff / (1000);
		return days;
	}

	public static ArrayList<DataPoint> getMeanValue(ArrayList<DataPoint> dps_xy) {
		ArrayList<DataPoint> finalArray = new ArrayList<DataPoint>();
		double tempLat, tempLon;
		int arrayIndex = 0;
		int tempClassId;
		double cnt = 1.0;
		while (arrayIndex < dps_xy.size()) {
			tempLat = dps_xy.get(arrayIndex).getDimension()[0];
			tempLon = dps_xy.get(arrayIndex).getDimension()[1];
			tempClassId = dps_xy.get(arrayIndex).getClassId();
			while (tempClassId == dps_xy.get(arrayIndex + 1).getClassId()) {
				tempLat += dps_xy.get(arrayIndex + 1).getDimension()[0];
				tempLon += dps_xy.get(arrayIndex + 1).getDimension()[1];
				arrayIndex++;
				cnt++;
				if (arrayIndex + 1 == dps_xy.size())
					break;
			}
			tempLat = tempLat / cnt;
			tempLon = tempLon / cnt;
			finalArray.add(new DataPoint(tempLat, tempLon));
			cnt = 1;
			arrayIndex++;
		}
		return finalArray;
	}

	public static ArrayList<DataPoint> getDBScanMeanValue(
			ArrayList<DataPoint> dps_xy) {
		ArrayList<DataPoint> finalArray = new ArrayList<DataPoint>();
		LinkedHashSet<Integer> classIdSet = new LinkedHashSet<Integer>();
		double cnt, tempLat, tempLon;
		for (int i = 0; i < dps_xy.size(); i++)
			classIdSet.add(dps_xy.get(i).getClassId());
		Iterator<Integer> iterClassIdSet = classIdSet.iterator();
		while (iterClassIdSet.hasNext()) {
			cnt = 0;
			tempLat = 0;
			tempLon = 0;
			int tempIdValue = iterClassIdSet.next();
			for (int j = 0; j < dps_xy.size(); j++) {
				if (dps_xy.get(j).getClassId() == tempIdValue) {
					tempLat += dps_xy.get(j).getDimension()[0];
					tempLon += dps_xy.get(j).getDimension()[1];
					cnt++;
				}
			}
			tempLat = tempLat / cnt;
			tempLon = tempLon / cnt;
			finalArray.add(new DataPoint(tempLat, tempLon));
		}
		return finalArray;
	}

	public static ArrayList<DataPoint> produceDataPoints() {
		ArrayList<DataPoint> DB = new ArrayList<DataPoint>();
		int cntGPS = 0, cntWiFi = 0, cntCell = 0;
		try {
			String tempLine = "";
			File file = new File(Analysis.inputPath);
			if (!file.exists()) {
				System.out.println("Data File Not Exists.");
				System.exit(2);
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			tempLine = br.readLine();
			int cnt = 0;
			while (tempLine != null) {

				String bdStr = tempLine.substring(0, 2);
				if (bdStr.equals("bd")) {
					int statusValue = Integer.parseInt(Services.getSubStringbyDouhao(tempLine, 2));
					switch (statusValue) {
					case 61:
						cntGPS += 1;
						break;
					case 161:
						switch (Services.getSubStringbyDouhao(tempLine, 7)) {
						case "wf":
							cntWiFi += 1;
							break;
						case "cl":
							cntCell += 1;
							break;
						default:
							cntCell += 1;
							break;
						}
						break;

					default:
						break;
					}
					if (statusValue == 161 || statusValue == 61) {
						cnt++;
						DataPoint bp = buildPoint(cnt, tempLine);
						if (bp != null)
							DB.add(bp);
					}
				}

				tempLine = br.readLine();
			}
			if (br != null) {
				br.close();
				br = null;
			}
			if (fr != null) {
				fr.close();
				fr = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("有效记录总条数为：" + DB.size());
		System.out.println("GPS定位条数为：" + cntGPS);
		System.out.println("Wi-Fi定位条数为：" + cntWiFi);
		System.out.println("Cell定位条数为：" + cntCell + "\n");
		return DB;
	}

	public static DataPoint buildPoint(int id, String tempLine) {
		double lat, lon, accuracyRadius;
		Date timestamp = null;

		lat = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 4));
		lon = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 5));
		accuracyRadius = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 6));

		double[] myPoint = { lat, lon };

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			timestamp = sdf.parse(Services.getSubStringbyDouhao(tempLine, 3));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		DataPoint bp = new DataPoint(id, myPoint, timestamp, accuracyRadius, 0);
		return bp;
	}

	public static DataPoint buildPoint(int id, String tempLine, int classId) {
		double lat, lon, accuracyRadius;
		Date timestamp = null;

		lat = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 2));
		lon = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 3));
		accuracyRadius = Double.parseDouble(Services.getSubStringbyDouhao(tempLine, 5));

		if (lat < 10.0 || lat > 55.0 || lon < 75.0 || lon > 135.0)
			return null;

		double[] myPoint = { lat, lon };

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		try {
			timestamp = sdf.parse(Services.getSubStringbyDouhao(tempLine, 4));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		DataPoint tempDataPoint = new DataPoint(id, myPoint, timestamp,
				accuracyRadius, classId);
		return tempDataPoint;
	}

	public static String getSubStringbyDouhao(String TargetStr, int cnt) {
		String tempString = "";

		// 自己的方法
		// 制表符
		// int asciiCode=9;
		// 逗号
		/*
		 * int asciiCode=44; int tab=-1; int priorTab=0; for(int i=0;i<cnt;i++){
		 * priorTab=tab+1; tab = TargetStr.indexOf(asciiCode,priorTab); }
		 * tempString=TargetStr.substring(priorTab, tab);
		 */

		// 简便方法
		// String[] cont=TargetStr.split("[,|\\s+]");
		String[] strings = TargetStr.split(",");
		tempString = strings[cnt - 1];

		return tempString;
	}
	
	public static String getSubStringbyTab(String TargetStr, int cnt) {
		String tempString = "";
		String[] strings = TargetStr.split("\t");
		tempString = strings[cnt - 1];
		return tempString;
	}

	public static ArrayList<DataPoint> getWeightedMeanValue(
			ArrayList<DataPoint> dps_xy) {
		ArrayList<DataPoint> tempClassArray = new ArrayList<DataPoint>();
		ArrayList<DataPoint> finalArray = new ArrayList<DataPoint>();
		double tempLat = 0, tempLon = 0, totalWeights = 0;
		int arrayIndex = 0, tempClassId;
		while (arrayIndex < dps_xy.size()) {
			tempClassId = dps_xy.get(arrayIndex).getClassId();
			while (tempClassId == dps_xy.get(arrayIndex + 1).getClassId()) {
				tempClassArray.add(dps_xy.get(arrayIndex));
				arrayIndex++;
				if (arrayIndex + 1 == dps_xy.size())
					break;
			}
			for (int i = 0; i < tempClassArray.size(); i++)
				totalWeights += 1.0 / tempClassArray.get(i).getAccuracyRadius();
			DataPoint tempPoint;
			for (int j = 0; j < tempClassArray.size(); j++) {
				tempPoint = tempClassArray.get(j);
				tempLat += tempPoint.getDimension()[0]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
				tempLon += tempPoint.getDimension()[1]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
			}
			finalArray.add(new DataPoint(tempLat, tempLon));
			tempLat = 0;
			tempLon = 0;
			totalWeights = 0;
			tempClassArray.clear();
			arrayIndex++;
		}
		return finalArray;
	}

	public static ArrayList<DataPoint> getDBScanWeightedMeanValue(
			ArrayList<DataPoint> dps_xy) {
		ArrayList<DataPoint> finalArray = new ArrayList<DataPoint>();
		ArrayList<DataPoint> tempClassArray = new ArrayList<DataPoint>();
		LinkedHashSet<Integer> classIdSet = new LinkedHashSet<Integer>();
		double tempLat, tempLon, totalWeights = 0;
		for (int i = 0; i < dps_xy.size(); i++)
			classIdSet.add(dps_xy.get(i).getClassId());
		Iterator<Integer> iterClassIdSet = classIdSet.iterator();
		while (iterClassIdSet.hasNext()) {
			tempLat = 0;
			tempLon = 0;
			int tempIdValue = iterClassIdSet.next();
			for (int j = 0; j < dps_xy.size(); j++) {
				if (dps_xy.get(j).getClassId() == tempIdValue) {
					tempClassArray.add(dps_xy.get(j));
				}
			}
			for (int i = 0; i < tempClassArray.size(); i++)
				totalWeights += 1.0 / tempClassArray.get(i).getAccuracyRadius();
			DataPoint tempPoint;
			for (int j = 0; j < tempClassArray.size(); j++) {
				tempPoint = tempClassArray.get(j);
				tempLat += tempPoint.getDimension()[0]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
				tempLon += tempPoint.getDimension()[1]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
			}
			finalArray.add(new DataPoint(tempLat, tempLon));
			tempLat = 0;
			tempLon = 0;
			totalWeights = 0;
			tempClassArray.clear();
		}
		return finalArray;
	}

	public static void showTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(date));
	}


	public static void outputClusterofSpatial(ArrayList<DataPoint> dps_xy) {
		try {
			FileWriter fw = new FileWriter(Analysis.spatialOutputPath);
			PrintWriter pw = new PrintWriter(fw);
			Iterator<DataPoint> dpIterator = dps_xy.iterator();
			DataPoint dptemp = null;
			pw.println("id\t纬度\t经度\t时间\t误差半径\t空间可达距离\t空间聚类编号");
			while (dpIterator.hasNext()) {
				dptemp = dpIterator.next();
				pw.println(dptemp.getId()+"\t"+dptemp.getDimension()[0] + "\t" + dptemp.getDimension()[1] + "\t" + 
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dptemp.getTimestamp())+ "\t"+ dptemp.getAccuracyRadius() 
							+ "\t" + dptemp.getReachableDistance() + "\t" + dptemp.getClassId());
			}
			if (pw != null) {
				pw.close();
				pw = null;
			}
			if (fw != null) {
				fw.close();
				fw = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void outputClusterofTime(ArrayList<DataPoint> dps_t) {
		try {
			FileWriter fw = new FileWriter(Analysis.timeOutputPath);
			PrintWriter pw = new PrintWriter(fw);
			Iterator<DataPoint> dpIterator = dps_t.iterator();
			DataPoint dptemp = null;
			pw.println("id\t纬度\t经度\t时间\t误差半径\t空间可达距离\t时间可达距离\t空间聚类编号\t时间聚类编号");
			while (dpIterator.hasNext()) {
				dptemp = dpIterator.next();
				pw.println(dptemp.getId()+"\t"+dptemp.getDimension()[0] + "\t" + dptemp.getDimension()[1] + "\t" + 
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dptemp.getTimestamp())+"\t" + dptemp.getAccuracyRadius()
						+ "\t" + dptemp.getReachableDistance()+ "\t" + dptemp.getReachableDistance_t()
						+ "\t" + dptemp.getClassId()+"\t" + dptemp.getClassId_t());
			}
			if (pw != null) {
				pw.close();
				pw = null;
			}
			if (fw != null) {
				fw.close();
				fw = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void generateSpaceTimeResult(ArrayList<DataPoint> dps_t) {
		ArrayList<DataPoint> tempClassArray = new ArrayList<DataPoint>();
		ArrayList<ClusterPoint> finalArray = new ArrayList<ClusterPoint>();
		double tempLat = 0, tempLon = 0, totalWeights = 0;
		int arrayIndex = 0, tempTimeClassId;
		Date arriveTime=null,leaveTime=null;
		
		while (arrayIndex < dps_t.size()) {
			
			try {
				arriveTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2016-1-1 1:1:1");
				leaveTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2015-1-1 1:1:1");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			
			tempTimeClassId = dps_t.get(arrayIndex).getClassId_t();
			while (tempTimeClassId == dps_t.get(arrayIndex + 1).getClassId_t()) {
				tempClassArray.add(dps_t.get(arrayIndex));
				arrayIndex++;
				if (arrayIndex + 1 == dps_t.size())
					break;
			}
			for (int i = 0; i < tempClassArray.size(); i++){
				totalWeights += 1.0 / tempClassArray.get(i).getAccuracyRadius();
				if(arriveTime.after(tempClassArray.get(i).getTimestamp()))
					arriveTime=tempClassArray.get(i).getTimestamp();
				if(leaveTime.before(tempClassArray.get(i).getTimestamp()))
					leaveTime=tempClassArray.get(i).getTimestamp();	
			}
			DataPoint tempPoint;
			for (int j = 0; j < tempClassArray.size(); j++) {
				tempPoint = tempClassArray.get(j);
				tempLat += tempPoint.getDimension()[0]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
				tempLon += tempPoint.getDimension()[1]
						* ((1.0 / tempPoint.getAccuracyRadius()) / totalWeights);
			}
			finalArray.add(new ClusterPoint(tempLat, tempLon,arriveTime,leaveTime,tempClassArray.get(0).getClassId(),tempClassArray.get(0).getClassId_t()));
			tempLat = 0;
			tempLon = 0;
			totalWeights = 0;
			tempClassArray.clear();
			arrayIndex++;
		}

		Collections.sort(finalArray, new ComparatorClusterPoints());
		for(int f=1;f<=finalArray.size();f++){
			finalArray.get(f-1).setTimeClassId(f);
		}
		
		try {
			FileWriter fw = new FileWriter(Analysis.spatialTimeOutputPath);
			PrintWriter pw = new PrintWriter(fw);
			Iterator<ClusterPoint> dpIterator = finalArray.iterator();
			ClusterPoint dptemp = null;
			pw.println("纬度\t经度\t到达时间\t离开时间\t空间聚类编号\t时间聚类编号");
			while (dpIterator.hasNext()) {
				dptemp = dpIterator.next();
				pw.println(dptemp.getLat() + "\t" + dptemp.getLon() + "\t" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dptemp.getArriveTime()) 
						+ "\t"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dptemp.getLeaveTime()) + "\t" + dptemp.getSpatialClassId()+"\t" + dptemp.getTimeClassId());
			}
			if (pw != null) {
				pw.close();
				pw = null;
			}
			if (fw != null) {
				fw.close();
				fw = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<DataPoint> produceTimeAnaSource() {
		
		ArrayList<DataPoint> DB = new ArrayList<DataPoint>();
		try {
			String tempLine = "";
			File file = new File(Analysis.spatialOutputPath);
			if (!file.exists()) {
				System.out.println("Data File Not Exists.");
				System.exit(2);
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			tempLine = br.readLine();
			tempLine = br.readLine();
			int cnt = 0;
			while (tempLine != null) {
						DataPoint bp = buildPointforTime(cnt, tempLine);
						if (bp != null)
							DB.add(bp);
				tempLine = br.readLine();
			}
			if (br != null) {
				br.close();
				br = null;
			}
			if (fr != null) {
				fr.close();
				fr = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return DB;
	}

	private static DataPoint buildPointforTime(int cnt, String tempLine) {
		int id,spaClassId;
		double lat, lon,reachDis,accRedius;
		Date timestamp = null;

		id=Integer.parseInt(Services.getSubStringbyTab(tempLine, 1));
		lat = Double.parseDouble(Services.getSubStringbyTab(tempLine, 2));
		lon = Double.parseDouble(Services.getSubStringbyTab(tempLine, 3));
		double[] myPoint = { lat, lon };
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			timestamp = sdf.parse(Services.getSubStringbyTab(tempLine, 4));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		accRedius=Double.parseDouble(Services.getSubStringbyTab(tempLine, 5));
		reachDis=Double.parseDouble(Services.getSubStringbyTab(tempLine, 6));
		spaClassId=Integer.parseInt(Services.getSubStringbyTab(tempLine, 7));
		
		DataPoint bp = new DataPoint(id, myPoint, timestamp, reachDis, spaClassId,accRedius);
		return bp;
	}

	public static ArrayList<DataPoint> produceFinalSource() {

		ArrayList<DataPoint> DB = new ArrayList<DataPoint>();
		try {
			String tempLine = "";
			File file = new File(Analysis.timeOutputPath);
			if (!file.exists()) {
				System.out.println("Data File Not Exists.");
				System.exit(2);
			}
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			tempLine = br.readLine();
			tempLine = br.readLine();
			int cnt = 0;
			while (tempLine != null) {
						DataPoint bp = buildPointforFinal(cnt, tempLine);
						if (bp != null)
							DB.add(bp);
				tempLine = br.readLine();
			}
			if (br != null) {
				br.close();
				br = null;
			}
			if (fr != null) {
				fr.close();
				fr = null;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return DB;
	}

	private static DataPoint buildPointforFinal(int cnt, String tempLine) {
		//"id\t纬度\t经度\t时间\t误差半径\t空间可达距离\t时间可达距离\t空间聚类编号\t时间聚类编号"
		int id,spaClassId,timeClassId;
		double lat, lon,reachDis_spa,reachDis_time,accRedius;
		Date timestamp = null;

		id=Integer.parseInt(Services.getSubStringbyTab(tempLine, 1));
		lat = Double.parseDouble(Services.getSubStringbyTab(tempLine, 2));
		lon = Double.parseDouble(Services.getSubStringbyTab(tempLine, 3));
		double[] myPoint = { lat, lon };
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			timestamp = sdf.parse(Services.getSubStringbyTab(tempLine, 4));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		accRedius=Double.parseDouble(Services.getSubStringbyTab(tempLine, 5));
		reachDis_spa=Double.parseDouble(Services.getSubStringbyTab(tempLine, 6));
		reachDis_time=Double.parseDouble(Services.getSubStringbyTab(tempLine, 7));
		spaClassId=Integer.parseInt(Services.getSubStringbyTab(tempLine, 8));
		timeClassId=Integer.parseInt(Services.getSubStringbyTab(tempLine, 9));
		
		DataPoint bp = new DataPoint(id, myPoint, timestamp, accRedius,reachDis_spa,reachDis_time, spaClassId,timeClassId);
		return bp;
	}
	
	
}
class ComparatorClusterPoints implements Comparator<ClusterPoint> {
	int a=0;
	@Override
	public int compare(ClusterPoint arg0, ClusterPoint arg1) {
		if(arg0.getArriveTime().before(arg1.getArriveTime())){
			a=-1;
		}
		if(arg0.getArriveTime().after(arg1.getArriveTime())){
			a=1;
		}
		if(arg0.getArriveTime().equals(arg1.getArriveTime())){
			a=0;
		}
		return a;
	}
}
