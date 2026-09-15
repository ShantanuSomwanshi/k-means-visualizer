package com.kmeans;

import javafx.scene.paint.Color;

public class Centroid {
    private double x;
    private double y;
    private Color color;

    public Centroid(double x, double y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    // Getters and Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public Color getColor() { return color; }
}