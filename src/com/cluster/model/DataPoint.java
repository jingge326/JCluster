package com.cluster.model;

import java.util.Date;

public class DataPoint {
    private int id; // 样本点名
    private double dimension[]; // 样本点的维度
    private Date timestamp;
    private double coreDistance; //核心距离，如果该点不是核心对象，则距离为-1
    private double coreDistance_t;
    private double reachableDistance; //可达距离
    private double reachableDistance_t;
    private int classId,classId_t;
    private boolean visited;
	private double accuracyRadius;
	private int orginClassId;

    public DataPoint(){
    }

    public DataPoint(DataPoint e){
        this.id=e.id;
        this.dimension=e.dimension;
        this.timestamp=e.timestamp;
        this.coreDistance=e.coreDistance;
        this.coreDistance_t=e.coreDistance_t;
        this.reachableDistance=e.reachableDistance;
        this.reachableDistance_t=e.reachableDistance_t;
        this.classId=e.classId;
        this.accuracyRadius=e.accuracyRadius;
        this.classId_t=e.classId_t;
    }

	public DataPoint(double tempLat, double tempLon) {
		double[] tempDimension={tempLat,tempLon};
		this.dimension=tempDimension;
	}

	public DataPoint(int id, double[] xyPoint, Date timestamp) {
		this.id=id;
		this.dimension=xyPoint;
		this.timestamp=timestamp;
	}
	
	public DataPoint(int id, double[] xyPoint, Date timestamp, double accuracyRadius,int classId) {
		this.id=id;
		this.dimension=xyPoint;
		this.timestamp=timestamp;
		this.accuracyRadius=accuracyRadius;
		this.classId=classId;
	}
	
	public DataPoint(int id, double[] xyPoint, Date timestamp, double reachDis,int spatialClassId,double accRedius) {
		this.id=id;
		this.dimension=xyPoint;
		this.timestamp=timestamp;
		this.reachableDistance=reachDis;
		this.classId=spatialClassId;
		this.accuracyRadius=accRedius;
	}

	public DataPoint(int id, double[] myPoint, Date timestamp,
			double accRedius, double reachDis_spa, double reachDis_time,
			int spaClassId, int timeClassId) {
		this.id=id;
		this.dimension=myPoint;
		this.timestamp=timestamp;
		this.accuracyRadius=accRedius;
		this.reachableDistance=reachDis_spa;
		this.reachableDistance_t=reachDis_time;
		this.classId=spaClassId;
		this.classId_t=timeClassId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double[] getDimension() {
		return dimension;
	}

	public void setDimension(double[] dimension) {
		this.dimension = dimension;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public double getCoreDistance() {
		return coreDistance;
	}

	public void setCoreDistance(double coreDistance) {
		this.coreDistance = coreDistance;
	}

	public double getCoreDistance_t() {
		return coreDistance_t;
	}

	public void setCoreDistance_t(double coreDistance_t) {
		this.coreDistance_t = coreDistance_t;
	}

	public double getReachableDistance() {
		return reachableDistance;
	}

	public void setReachableDistance(double reachableDistance) {
		this.reachableDistance = reachableDistance;
	}

	public double getReachableDistance_t() {
		return reachableDistance_t;
	}

	public void setReachableDistance_t(double reachableDistance_t) {
		this.reachableDistance_t = reachableDistance_t;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public double getAccuracyRadius() {
		return accuracyRadius;
	}

	public void setAccuracyRadius(double accuracyRadius) {
		this.accuracyRadius = accuracyRadius;
	}

	public void setOriginClassId(int orginClassId) {
		this.orginClassId=orginClassId;
	}

	public int getOriginClassId() {
		return orginClassId;
	}

	public int getClassId_t() {
		return classId_t;
	}

	public void setClassId_t(int classId_t) {
		this.classId_t = classId_t;
	} 
}
