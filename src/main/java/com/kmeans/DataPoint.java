package com.kmeans;

public class DataPoint {
    private double x;
    private double y;
    private int clusterIndex; // -1 means it hasn't been assigned to a cluster yet

    public DataPoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.clusterIndex = -1;
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    
    public int getClusterIndex() { return clusterIndex; }
    public void setClusterIndex(int clusterIndex) { this.clusterIndex = clusterIndex; }
}