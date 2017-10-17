package com.cluster.clique;

public class Cell {

	//private int x;
	//private int y;
	private int pointsNumber;//点的数目
	private int qualified;//密度是否达到阈值，0,1
	private int checked;//是否检查过 ,0,1 
	private int clusterNo;//所属类别,0表示离散点

/*	public void setX(int i) {
		x = i;
	}

	public void setY(int j) {
		y = j;
	}*/
	public Cell(){
		pointsNumber = 0;
		qualified = 0;
		checked = 0;
		clusterNo = 0;
	}

	public void setNumberPoints(int i) {
		pointsNumber = i;
	}
	public void setQualified(int i){
		qualified = i;
	}
	public void setChecked(int i){
		checked = i;
	}
	public void setClusterNo(int i){
		clusterNo = i;
	}
	
/*	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}*/
	public int getpointsNumber(){
		return pointsNumber;
	}
	public int getChecked(){
		return checked;
	}
	public int getQualified(){
		return qualified;
	}
	public int getClusterNo(){
		return clusterNo;
	}
	public void addpointsNumber(){
		pointsNumber += 1;
	}	
}
