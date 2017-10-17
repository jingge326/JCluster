package com.cluster.dbscan;
 
import java.util.ArrayList;
import java.util.Iterator;

import com.cluster.controller.Services;
import com.cluster.model.DataPoint;
 
public class DBScan {
	private double Eps;
	private int MinPts;
    private ArrayList<DataPoint> dPointList=new ArrayList<DataPoint>();
	
    public DBScan(double eps, int minPts, ArrayList<DataPoint> dPointList) {
		super();
		Eps = eps;
		MinPts = minPts;
		this.dPointList = dPointList;
	}

	public ArrayList<DataPoint> analysis(){
        int clusterID=0;        
        Iterator<DataPoint> iter=dPointList.iterator();
        while(iter.hasNext()){
        	DataPoint dPoint=iter.next();
            if(dPoint.isVisited())
                continue;
            dPoint.setVisited(true);
            ArrayList<DataPoint> neighborPoints=getneighborPoints(dPoint,dPointList);
            if(neighborPoints.size()>=MinPts){
            	if(dPoint.getClassId()==0){
                    clusterID++;
                    expandCluster(dPoint,neighborPoints,clusterID,dPointList);
                }else{
                    int corePointid=dPoint.getClassId();
                    expandCluster(dPoint,neighborPoints,corePointid,dPointList);
                }
            }   
        }
        
        return new ArrayList<DataPoint>(this.simplifyResult());
    }
    
	private ArrayList<DataPoint> simplifyResult() {
		ArrayList<DataPoint> resultaArrayList=new ArrayList<DataPoint>();
		for(int i=0;i<dPointList.size();i++){
			if(dPointList.get(i).getClassId()!=0)
				resultaArrayList.add(new DataPoint(dPointList.get(i)));
		}
		return resultaArrayList;
	}

    public ArrayList<DataPoint> getneighborPoints(DataPoint dPoint,ArrayList<DataPoint> dPointList){
        ArrayList<DataPoint> neighborPoints=new ArrayList<DataPoint>();
        Iterator<DataPoint> iter=dPointList.iterator();
        while(iter.hasNext()){
            DataPoint tempPoint=iter.next();
            if(Services.getDistance(dPoint, tempPoint)<=Eps){
                neighborPoints.add(tempPoint);
            }
        }
        return neighborPoints;
    }
 
    private void expandCluster(DataPoint dPoint, ArrayList<DataPoint> neighborPoints, int clusterID, ArrayList<DataPoint> dPointList) {
        dPoint.setClassId(clusterID);
        Iterator<DataPoint> iter=neighborPoints.iterator();
        while(iter.hasNext()){
            DataPoint tempPoint=iter.next();
            if(!tempPoint.isVisited()){
                tempPoint.setVisited(true);
                ArrayList<DataPoint> tempNeighborPoints=getneighborPoints(tempPoint,dPointList);
                if(tempNeighborPoints.size()>=MinPts){
                    Iterator<DataPoint> it=tempNeighborPoints.iterator();
                    while(it.hasNext()){
                        DataPoint no=it.next();
                        if(no.getClassId()==0)
                            no.setClassId(clusterID);
                    }
                }
            }
            if(tempPoint.getClassId()==0){       //tempPoint不是任何簇的成员
                tempPoint.setClassId(clusterID);
            }
        }
    }
}