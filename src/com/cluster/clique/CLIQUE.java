package com.cluster.clique;

import java.util.ArrayList;
import java.util.List;

import com.cluster.controller.Services;
import com.cluster.model.DataPoint;

public class CLIQUE {
	private static int cellRows;
	private static int cellColumns;
	private static int clustersNumber;
	private double intervalx = 0;
	private double intervaly = 0;
	private static Cell[][] cellsArray;
	private static List<Cluster> clustersList;
	private double xmin = Double.MAX_VALUE;
	private double xmax = 0;
	private double ymin = Double.MAX_VALUE;
	private double ymax = 0;
	private ArrayList<DataPoint> sourceArrayList;
	
	
	public CLIQUE() {
		sourceArrayList = Services.produceDataPoints();
	}


	public ArrayList<DataPoint> analyze(double interval,double radio) {
		ArrayList<DataPoint> resultList = new ArrayList<DataPoint>();
		initialize(interval);
		constructCellsArray();
		cluster(radio);
		resultList = generateResultList();
		return resultList;
	}

	
	private void initialize(double interval) {
		
		cellRows = Services.cellRows;
		cellColumns = Services.cellColumns;
		clustersNumber = 0;
		cellsArray = new Cell[cellRows + 1][cellColumns + 1];
		clustersList = new ArrayList<Cluster>();
		clustersList.add(new Cluster(clustersNumber));
		int i, j;
		for (i = 1; i <= Services.cellColumns; i++) {
			for (j = 1; j <= Services.cellRows; j++) {
				cellsArray[i][j] = new Cell();
			}
		}
		
		
		xmin = Double.MAX_VALUE;
		ymin = Double.MAX_VALUE;
		for (int h = 0; h < sourceArrayList.size(); h++) {
			if (xmin > sourceArrayList.get(h).getDimension()[1])
				xmin = sourceArrayList.get(h).getDimension()[1];
			/*if (xmax < sourceArrayList.get(h).getDimension()[1])
				xmax = sourceArrayList.get(h).getDimension()[1];*/
			if (ymin > sourceArrayList.get(h).getDimension()[0])
				ymin = sourceArrayList.get(h).getDimension()[0];
			/*if (ymax < sourceArrayList.get(h).getDimension()[0])
				ymax = sourceArrayList.get(h).getDimension()[0];*/
		}
		xmin -= 0.005;
		//xmax += 0.005;
		ymin -= 0.005;
		//ymax += 0.005;
		
		/*intervalx=(xmax-xmin)/Services.cellColumns;
		intervaly=(ymax-ymin)/Services.cellRows;*/
		
		/*intervalx=0.00038;
		intervaly=0.00038;*/
		
		intervalx=interval;
		intervaly=interval;
		
		
		//System.out.println("宽："+intervalx+"高："+intervaly);
	}

	public void constructCellsArray() {
		int cellx,celly;
		for (int j = 0; j < sourceArrayList.size(); j++) {
			cellx = (int) Math.ceil((sourceArrayList.get(j).getDimension()[1] - this.xmin) / intervalx);
			celly = (int) Math.ceil((sourceArrayList.get(j).getDimension()[0] - this.ymin) / intervaly);
			cellsArray[celly][cellx].addpointsNumber();
			if (cellsArray[celly][cellx].getpointsNumber() >= Services.densityThreshold)
				cellsArray[celly][cellx].setQualified(1);
		}
	}

	private static void cluster(double radio) {
		int i, j;
		for (i = 1; i <= Services.cellColumns; i++) {
			for (j = 1; j <= Services.cellRows; j++) {
				if (cellsArray[i][j].getClusterNo() == 0 && cellsArray[i][j].getChecked() != 1) {
					if (cellsArray[i][j].getQualified() > 0) {
						clustersNumber += 1;
						clustersList.add(new Cluster(clustersNumber));
						retrieve(clustersNumber, i, j,radio);
					} else {
						cellsArray[i][j].setChecked(1);
						cellsArray[i][j].setClusterNo(0);
					}
				}
			}
		}
	}

	private static void retrieve(int k, int i, int j, double radio) {
		int l, m;
		if (cellsArray[i][j].getChecked() != 1 && cellsArray[i][j].getClusterNo() == 0) {
			if (cellsArray[i][j].getQualified() > 0 && cellsArray[i][j].getpointsNumber() >= (int) ((Cluster) clustersList.get(k)).getDensity()* radio) {
				cellsArray[i][j].setChecked(1);
				cellsArray[i][j].setClusterNo(k);
				((Cluster) clustersList.get(k)).addCell(cellsArray[i][j].getpointsNumber());

				// cluster the neighbors of cells[i][j]
				if (i != 1) {
					l = i - 1;
					retrieve(k, l, j,radio);
				}
				if (i != cellColumns) {
					l = i + 1;
					retrieve(k, l, j,radio);
				}
				if (j != 1) {
					m = j - 1;
					retrieve(k, i, m,radio);
				}
				if (j != cellRows) {
					m = j + 1;
					retrieve(k, i, m,radio);
				}
			} else {
				cellsArray[i][j].setChecked(1);
				cellsArray[i][j].setClusterNo(0);
			}
		}
	}

	private ArrayList<DataPoint> generateResultList() {
		ArrayList<DataPoint> classifiedList = new ArrayList<DataPoint>(sourceArrayList);
		int cellx,celly;
		for (int j = 0; j < classifiedList.size(); j++) {
			cellx = (int) Math.ceil((classifiedList.get(j).getDimension()[1] - xmin) / intervalx);
			celly = (int) Math.ceil((classifiedList.get(j).getDimension()[0] - ymin) / intervaly);

			classifiedList.get(j).setClassId(cellsArray[celly][cellx].getClusterNo());
		}
		
		for(int i=0;i<classifiedList.size();i++){
			if(classifiedList.get(i).getClassId()==0)
				classifiedList.remove(i);			
		}
		
		return classifiedList;
	}
}
