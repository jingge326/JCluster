package com.cluster.birch;
 
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.cluster.controller.Services;
import com.cluster.model.DataPoint;
 
public class BIRCH {
 
    public static final int dimen=2;
    LeafNode leafNodeHead=new LeafNode();
    int point_num=0;        //point instance计数
    
    
   public ArrayList<DataPoint> analyze() {
       TreeNode root=this.buildBTree();
       this.point_num=0;
       ArrayList<DataPoint> birchArrayList=new ArrayList<DataPoint>();
       this.turnTreeToArray(root,birchArrayList);
       System.out.println("结果数组中有点："+this.point_num);
       //birch.printLeaf(birch.leafNodeHead);
       return birchArrayList;
   }
     
    //逐条扫描数据库，建立B-树
    public TreeNode buildBTree(){
        //先建立一个叶子节点
        LeafNode leaf=new LeafNode();
        TreeNode root=leaf;
 
        //把叶子节点加入存储叶子节点的双向链表
        leafNodeHead.setNext(leaf);
        leaf.setPre(leafNodeHead);
        
        ArrayList<DataPoint> dPointList=new ArrayList<DataPoint>();
        dPointList=Services.produceDataPoints();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        
        DataPoint tempdpoint;
        for(int i=0;i<dPointList.size();i++){
        	tempdpoint=dPointList.get(i);
        	String mark=tempdpoint.getId()+","+tempdpoint.getDimension()[0]+","+tempdpoint.getDimension()[1]+","
        				+sdf.format(tempdpoint.getTimestamp())+","+tempdpoint.getAccuracyRadius()+","+tempdpoint.getClassId();
            //根据一个point instance创建一个MinCluster
            CF cf=new CF(tempdpoint.getDimension());
            MinCluster subCluster=new MinCluster();
            subCluster.setCf(cf);
            subCluster.getInst_marks().add(mark);
            //把新到的MinCluster插入树中
            root.absorbSubCluster(subCluster);
            //要始终保证root是树的根节点
            while(root.getParent()!=null){
                root=root.getParent();
            }
        }
        return root;
    }
     
    //打印B-树的所有叶子节点
    public void printLeaf(LeafNode header){
        //point_num清0
        point_num=0;
        while(header.getNext()!=null){
            System.out.println("\n一个叶子节点:");
            header=header.getNext();
            for(MinCluster cluster:header.getChildMinClusters()){
                System.out.println("\n一个最小簇:");
                for(String mark:cluster.getInst_marks()){
                    point_num++;
                    System.out.print(mark+"\t");
                }
            }
        }
    }
     
    //打印指定根节点的子树
    public void turnTreeToArray(TreeNode root,ArrayList<DataPoint> birchArrayList){
        
        if(!root.getClass().getName().equals("com.cluster.birch.LeafNode")){
            NonLeafNode nonleaf=(NonLeafNode)root;
            for(TreeNode child:nonleaf.getChildren()){
            	turnTreeToArray(child,birchArrayList);
            }
        }
        else{
            LeafNode leaf=(LeafNode)root;
            for(MinCluster cluster:leaf.getChildMinClusters()){
            	
            	//聚类中不足60点的直接滤掉
            	if(cluster.getCf().getN()<50)
            		continue;
            	
                Services.CLASSID++;
                for(String mark:cluster.getInst_marks()){
                    point_num++;
                    birchArrayList.add(Services.buildPoint(point_num, mark, Services.CLASSID));
                }
            }
        }
    System.out.println(Services.CLASSID);
    }
}