package com.cluster.optics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cluster.controller.Services;
import com.cluster.model.DataPoint;

public class OPTICS_spatial {
	int countBug=0,classId=0;
	public ArrayList<DataPoint> analysis_xy(ArrayList<DataPoint> dataPoints, double eps, int minPts){
		ArrayList<DataPoint> dpList = new ArrayList<DataPoint>();
		//dpQue贯穿一个簇的处理始终
		ArrayList<DataPoint> dpQue = new ArrayList<DataPoint>();

		int total = 0;
		while (total < dataPoints.size()) {
			if (isContainedInList(dataPoints.get(total), dpList) == -1) {
				ArrayList<DataPoint> tmpDpList = isKeyAndReturnObjects(dataPoints.get(total), dataPoints, eps, minPts);
				if (tmpDpList != null && tmpDpList.size() > 0) {
					DataPoint newDataPoint = new DataPoint(dataPoints.get(total));
					classId++;
					newDataPoint.setClassId(classId);
					dpQue.add(newDataPoint);
				}
			}
			while (!dpQue.isEmpty()) {
				DataPoint tempDpfromQ = dpQue.remove(0);
				DataPoint newDataPoint = new DataPoint(tempDpfromQ);
				dpList.add(newDataPoint);
				ArrayList<DataPoint> tempDpList = isKeyAndReturnObjects(tempDpfromQ,dataPoints, eps, minPts);
				if (tempDpList != null && tempDpList.size() > 0) {
					for (int i = 0; i < tempDpList.size(); i++) {
						DataPoint tempDpfromList = tempDpList.get(i);
						int indexInList = isContainedInList(tempDpfromList,dpList);
						int indexInQ = isContainedInList(tempDpfromList, dpQue);
						if (indexInList == -1) {
							if (indexInQ > -1) {
								int index = -1;
								for (DataPoint dataPoint : dpQue) {
									index++;
									if (index == indexInQ) {
										if (dataPoint.getReachableDistance() > tempDpfromList.getReachableDistance()) {
											dataPoint.setReachableDistance(tempDpfromList.getReachableDistance());
										}
									}
								}
							} else {
								DataPoint dpToAdd=new DataPoint(tempDpfromList);
								dpToAdd.setClassId(classId);
								dpQue.add(dpToAdd);
							}
						}
					}
					try {
						Collections.sort(dpQue, new ComparatorDp());
					} catch (IllegalArgumentException e) {
						System.out.println(++countBug);		
					}
				}
			}
			
			total++;
		}
		return dpList;
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

	private ArrayList<DataPoint> isKeyAndReturnObjects(DataPoint dataPoint, ArrayList<DataPoint> dataPoints, double radius, int ObjectNum) {
		ArrayList<DataPoint> arrivableObjects = new ArrayList<DataPoint>(); // 用来存储所有直接密度可达对象
		List<Double> distances = new ArrayList<Double>(); // 欧几里得距离
		double coreDistance; // 核心距离

		for (int i = 0; i < dataPoints.size(); i++) {
			DataPoint dp = dataPoints.get(i);
			double distance = Services.getDistance(dataPoint, dp);
			if (distance <= radius) {
				distances.add(distance);
				arrivableObjects.add(dp);
			}
		}

		if (arrivableObjects.size() >= ObjectNum) {
			List<Double> newDistances = new ArrayList<Double>(distances);
			Collections.sort(distances);
			coreDistance = distances.get(ObjectNum - 1);

			for (int j = 0; j < arrivableObjects.size(); j++) {
				if (coreDistance > newDistances.get(j)) {
					if (newDistances.get(j) == 0) {
						dataPoint.setReachableDistance(coreDistance);//把自己也设核心距离
					}
					arrivableObjects.get(j).setReachableDistance(coreDistance);
				} else {
					arrivableObjects.get(j).setReachableDistance(newDistances.get(j));
				}
			}
			return arrivableObjects;
		}
		return null;
	}
	
	class ComparatorDp implements Comparator<DataPoint> {
		public int compare(DataPoint arg0, DataPoint arg1) {
			double temp = arg0.getReachableDistance() - arg1.getReachableDistance();
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