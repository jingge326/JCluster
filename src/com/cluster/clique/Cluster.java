package com.cluster.clique;

public class Cluster {
	private int clusterNo ;//c0 means outlier;
	private int cellsNumber;//该聚类中有点的数目 ;
	private int density;//density>= densityThreshold;
	
	public Cluster(int i){
		clusterNo = i;
		density = 0;
		cellsNumber = 0;
	}
	
	public void addCell(int numberPoints){
		density = (cellsNumber*density + numberPoints)/(cellsNumber + 1);
		cellsNumber +=1;
	}

	public void setclusterNo(int i){
		clusterNo = i;
	}
	public void setNumberCells(int i){
		cellsNumber = i;
	}
	public void setDensity(int i){
		density = i;
	}
	public int getClusterNo(){
		return clusterNo;
	}
	public int getNumberCells(){
		return cellsNumber;
	}
	public int getDensity(){
		return density;
	}
}
