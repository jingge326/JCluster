package com.cluster.controller;

import java.util.ArrayList;

import com.cluster.model.DataPoint;
import com.cluster.optics.OPTICS_spatial;
import com.cluster.optics.OPTICS_time;

public class Analysis {
	public final static String inputPath = "F:\\ClusterAnalysis\\data\\lichong\\1.txt";
	public final static String spatialOutputPath = "F:\\ClusterAnalysis\\data\\lichong\\spatialResult.txt";
	public final static String timeOutputPath = "F:\\ClusterAnalysis\\data\\lichong\\timeResult.txt";
	public static final String spatialTimeOutputPath = "F:\\ClusterAnalysis\\data\\lichong\\spatialTimeResult.txt";
	
	private final static double eps_xy = 0.0003;
	private final static int MinPts_xy = 300;
	ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();
	
	private final static double eps_t = 300;
	private final static int MinPts_t = 100;
	
	public static void main(String[] args) {	
		Services.showTime();
		Analysis acer=new Analysis();
		acer.run();
		Services.showTime();
	}
	
	public void run(){
		
		//读入原始数据
		dpoints=Services.produceDataPoints();
		
		//进行空间聚类
		OPTICS_spatial optics_spatial = new OPTICS_spatial();
		ArrayList<DataPoint> dps_xy = optics_spatial.analysis_xy(dpoints, eps_xy, MinPts_xy);
		System.out.println("分析已完成！ ");
		Services.outputClusterofSpatial(dps_xy);
		System.out.println("已完成空间聚类，结果输出至  "+spatialOutputPath);
		
		//进行时间聚类
		OPTICS_time optics_time = new OPTICS_time();
		ArrayList<DataPoint> dps_t = optics_time.analysis_t(Services.produceTimeAnaSource(), eps_t, MinPts_t);
		Services.outputClusterofTime(dps_t);
		System.out.println("已完成时间聚类，结果输出至  "+timeOutputPath);
		
		//输出聚类特征
		Services.generateSpaceTimeResult(Services.produceFinalSource());
		System.out.println("已完成聚类结果整理，输出至  "+spatialTimeOutputPath);
	} 
}
