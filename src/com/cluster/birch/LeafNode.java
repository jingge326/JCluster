package com.cluster.birch;
 
import java.util.ArrayList;
 
public class LeafNode extends TreeNode {
	
	//Leaf最多只能有L个MinCluster
	private int L=20;
	//一个MinCluster的直径不能超过T
    private double T=0.0012;
    
    private ArrayList<MinCluster> childMinClusters;
    private LeafNode pre;
    private LeafNode next;
 
    public LeafNode() {
    	childMinClusters=new ArrayList<MinCluster>();
    }
 
    public LeafNode(double[] data) {
        super(data);
    }
 
    // 叶子节点分裂
    public void split() {
        // 找到距离最远的两个MinCluster
        int c1 = 0;
        int c2 = 0;
        double maxDist = 0;
        int len = this.getChildMinClusters().size();
        for (int i = 0; i < len - 1; i++) {
            for (int j = i + 1; j < len; j++) {
                double dist = this.getChildMinClusters().get(i).getCf().getDistanceTo(this.getChildMinClusters().get(j).getCf());
                if (dist > maxDist) {
                    maxDist = dist;
                    c1 = i;
                    c2 = j;
                }
            }
        }
        // 根据距离最远，再生出一个叶节点，把原来多个MinCluster分到两个节点下。
        LeafNode newNode = new LeafNode();
        newNode.addChildMinClusters(this.getChildMinClusters().get(c2));
        // 如果本原叶节点已经是Root节点，则需要创建一个新的Root节点
        if (this.getParent() == null) {
            NonLeafNode root = new NonLeafNode();
            root.setN(this.getN());
            root.setLS(this.getLS());
            root.setSS(this.getSS());
            this.setParent(root);
            root.addChild(this);
        }
        //建立新节点和本节点的父节点的父子关系
        newNode.setParent(this.getParent());
        ((NonLeafNode)this.getParent()).addChild(newNode);
        //把离newNode近的MinCluster归到newNode所在节点下
        for (int i = 0; i < len; i++) {
            if (i != c1 && i != c2) {
                if (this.getChildMinClusters().get(i).getCf()
                		.getDistanceTo(this.getChildMinClusters().get(c2).getCf()) < this
                		.getChildMinClusters().get(i).getCf()
                		.getDistanceTo(this.getChildMinClusters().get(c1).getCf())) {
                    newNode.addChildMinClusters(this.getChildMinClusters().get(i));
                }
            }
        }
        //把离newNode近的孩子节点从本节点中删除
        for (MinCluster cluster : newNode.getChildMinClusters()) {
            newNode.addCF(cluster.getCf(), true);
            this.deleteChildMinClusters(cluster);
            this.addCF(cluster.getCf(), false);
        }
        // 把新增加的LeafNode添加到LeafNode双向链表中
        if (this.getNext() != null) {
            newNode.setNext(this.getNext());
            this.getNext().setPre(newNode);
        }
        this.setNext(newNode);
        newNode.setPre(this);
        // 如果本节点分裂导致父节点的孩子数超过了分枝因子，引发父节点分裂
        NonLeafNode pn = (NonLeafNode) this.getParent();
        if (pn.getChildren().size() > pn.getB()) {
            this.getParent().split();
        }
    }
 
    @Override
    public void absorbSubCluster(MinCluster cluster) {
        // 先试图找到子MinCluster中与cluster最近的簇
        CF cf = cluster.getCf();
        int nearIndex = 0;
        double minDist = Double.MAX_VALUE;
        int len = this.getChildMinClusters().size();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                double dist = cf.getDistanceTo(this.getChildMinClusters().get(i).getCf());
                if (dist < minDist) {
                    nearIndex = i;
                    
                    //同样的源代码错误，见NonLeafNode对应部分
                    minDist=dist;
                }
            }
            // 计算两个簇合并后的直径
            double mergeDiameter = MinCluster.getDiameter(cluster, this.getChildMinClusters().get(nearIndex));
            // 如果合并后发现簇的直径超过了阈值，则把cluster作为一个单独的cluster插入本叶子节点下
            if (mergeDiameter > T) {
                this.addChildMinClusters(cluster);
                if (this.getChildMinClusters().size() > L) {
                    this.split();
                }
            }
            // 如果不超过阈值，则直接合并两个簇
            else {
                this.getChildMinClusters().get(nearIndex).mergeCluster(cluster);
            }
        }
        // 创建B树之初，叶子节点还没有子MinCluster
        else {
            this.addChildMinClusters(cluster);
        }
        this.addCFUpToRoot(cluster.getCf());
    }
 
    public ArrayList<MinCluster> getChildMinClusters() {
        return childMinClusters;
    }
 
    public void setChildMinClusters(ArrayList<MinCluster> childMinClusters) {
        this.childMinClusters = childMinClusters;
    }
 
    public void addChildMinClusters(MinCluster child) {
        this.childMinClusters.add(child);
    }
 
    public void deleteChildMinClusters(MinCluster child) {
        this.childMinClusters.remove(childMinClusters.indexOf(child));
    }
 
    public LeafNode getPre() {
        return pre;
    }
 
    public void setPre(LeafNode pre) {
        this.pre = pre;
    }
 
    public LeafNode getNext() {
        return next;
    }
 
    public void setNext(LeafNode next) {
        this.next = next;
    }
 
    public int getL() {
        return L;
    }
 
    public void setL(int l) {
        L = l;
    }
 
    public double getT() {
        return T;
    }
 
    public void setT(double t) {
        T = t;
    }
}