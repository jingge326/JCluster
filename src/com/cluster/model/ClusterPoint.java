package com.cluster.model;

import java.util.Date;

public class ClusterPoint {
	//"纬度\t经度\t到达时间\t离开时间\t空间聚类编号\t时间聚类编号"
	private double lat;
	private double lon;
	private Date arriveTime;
	private Date leaveTime;
	private int spatialClassId;
	private int timeClassId;
	
	public ClusterPoint(ClusterPoint cp){
		this.lat=cp.lat;
		this.lon=cp.lon;
		this.arriveTime=cp.arriveTime;
		this.leaveTime=cp.leaveTime;
		this.spatialClassId=cp.spatialClassId;
		this.timeClassId=cp.timeClassId;
	}
	
	public ClusterPoint(double lat, double lon, Date arriveTime,
			Date leaveTime, int spatialClassId, int timeClassId) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.arriveTime = arriveTime;
		this.leaveTime = leaveTime;
		this.spatialClassId = spatialClassId;
		this.timeClassId = timeClassId;
	}

	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLon() {
		return lon;
	}
	public void setLon(double lon) {
		this.lon = lon;
	}
	public Date getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}
	public Date getLeaveTime() {
		return leaveTime;
	}
	public void setLeaveTime(Date leaveTime) {
		this.leaveTime = leaveTime;
	}
	public int getSpatialClassId() {
		return spatialClassId;
	}
	public void setSpatialClassId(int spatialClassId) {
		this.spatialClassId = spatialClassId;
	}
	public int getTimeClassId() {
		return timeClassId;
	}
	public void setTimeClassId(int timeClassId) {
		this.timeClassId = timeClassId;
	}
}
