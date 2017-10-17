package com.cluster.optics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cluster.controller.Services;
import com.cluster.model.DataPoint;

public class OPTICS_time {
	int classId_t=0;
	public ArrayList<DataPoint> analysis_t(ArrayList<DataPoint> dataPoints, double eps_t, int minPts_t){
		ArrayList<DataPoint> dpList_t = new ArrayList<DataPoint>();
		ArrayList<DataPoint> dpQue_t = new ArrayList<DataPoint>();

		int total = 0;
		while (total < dataPoints.size()) {
			if (isContainedInList(dataPoints.get(total), dpList_t) == -1) {
				ArrayList<DataPoint> tmpDpList = isKeyAndReturnObjects_t(dataPoints.get(total), dataPoints, eps_t, minPts_t);
				if (tmpDpList != null && tmpDpList.size() > 0) {
					DataPoint newDataPoint = new DataPoint(dataPoints.get(total));
					classId_t++;
					newDataPoint.setClassId_t(classId_t);
					dpQue_t.add(newDataPoint);
				}
			}
			while (!dpQue_t.isEmpty()) {
				DataPoint tempDpfromQ = dpQue_t.remove(0);
				DataPoint newDataPoint = new DataPoint(tempDpfromQ);
				dpList_t.add(newDataPoint);
				ArrayList<DataPoint> tempDpList = isKeyAndReturnObjects_t(tempDpfromQ,dataPoints, eps_t, minPts_t);
				if (tempDpList != null && tempDpList.size() > 0) {
					for (int i = 0; i < tempDpList.size(); i++) {
						DataPoint tempDpfromList = tempDpList.get(i);
						int indexInList = isContainedInList(tempDpfromList,dpList_t);
						int indexInQ = isContainedInList(tempDpfromList, dpQue_t);
						if (indexInList == -1) {
							if (indexInQ > -1) {
								int index = -1;
								for (DataPoint dataPoint : dpQue_t) {
									index++;
									if (index == indexInQ) {
										if (dataPoint.getReachableDistance_t() - tempDpfromList.getReachableDistance_t()>0) {
											dataPoint.setReachableDistance_t(tempDpfromList.getReachableDistance_t());
										}
									}
								}
							} else {
								DataPoint dpToAdd=new DataPoint(tempDpfromList);
								dpToAdd.setClassId_t(classId_t);
								dpQue_t.add(dpToAdd);
							}
						}
					}
					Collections.sort(dpQue_t, new ComparatorDp());
				}
			}
			total++;
		}
		return dpList_t;
	}

	private int isContainedInList(DataPoint dp, ArrayList<DataPoint> dpList) {
		int index = -1;
		for (DataPoint dataPoint : dpList) {
			index++;
			if (dataPoint.getId()==dp.getId()) {
				return index;
			}
		}
		return -1;
	}

	private ArrayList<DataPoint> isKeyAndReturnObjects_t(DataPoint dataPoint, ArrayList<DataPoint> dataPoints, double radius_t, int ObjectNum_t) {
		ArrayList<DataPoint> arrivableObjects = new ArrayList<DataPoint>(); // 用来存储所有直接密度可达对象
		List<Double> distances_t = new ArrayList<Double>(); // 时间距离
		double coreDistance_t; // 核心距离

		for (int i = 0; i < dataPoints.size(); i++) {
			DataPoint dp = dataPoints.get(i);
			double distance_t = Services.getDistance_t(dataPoint, dp);
			if (distance_t <= radius_t) {
				distances_t.add(distance_t);
				arrivableObjects.add(dp);
			}
		}

		if (arrivableObjects.size() >= ObjectNum_t) {
			List<Double> newDistances = new ArrayList<Double>(distances_t);
			Collections.sort(distances_t);
			coreDistance_t = distances_t.get(ObjectNum_t - 1);
			for (int j = 0; j < arrivableObjects.size(); j++) {
				if (coreDistance_t > newDistances.get(j)) {
					if (newDistances.get(j) == 0) {
						dataPoint.setReachableDistance_t(coreDistance_t);//把自己也设核心距离
					}
					arrivableObjects.get(j).setReachableDistance_t(coreDistance_t);
				} else {
					arrivableObjects.get(j).setReachableDistance_t(newDistances.get(j));
				}
			}
			return arrivableObjects;
		}
		return null;
	}
	
	
	class ComparatorDp implements Comparator<DataPoint> {
		public int compare(DataPoint arg0, DataPoint arg1) {
			double temp = arg0.getReachableDistance_t() - arg1.getReachableDistance_t();
			int a = 0;
			if (temp < 0) {
				a = -1;
			} 
			//相等时要另作处理，详见 http://blog.sina.com.cn/s/blog_8e6f1b330101h7fa.html
			else if(temp==0) {
				a=0;
			} else if(temp>0){
				a = 1;
			}
			return a;
		}
	}
}