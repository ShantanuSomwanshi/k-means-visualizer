package com.kmeans;

import atlantafx.base.theme.PrimerDark;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private Canvas canvas;
    private GraphicsContext gc;
    
    private List<DataPoint> dataPoints = new ArrayList<>();
    private List<Centroid> centroids = new ArrayList<>();
    
    private final Color[] CLUSTER_COLORS = {
        Color.web("#ff7b72"), Color.web("#79c0ff"), Color.web("#7ee787"), 
        Color.web("#f2cc60"), Color.web("#ffa657"), Color.web("#d2a8ff"), 
        Color.web("#56d4dd"), Color.web("#ff80eb"), Color.web("#a5d6ff"), Color.web("#c9d1d9")
    };

    private Random random = new Random();
    private Slider kSlider;

    // --- NEW: Animation Engine ---
    private Timeline autoPlayTimeline;
    private boolean isPlaying = false;
    private Button btnAutoPlay;

    @Override
    public void start(Stage primaryStage) {
        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        BorderPane root = new BorderPane();

        canvas = new Canvas(600, 600);
        gc = canvas.getGraphicsContext2D();
        clearCanvas(); 
        root.setCenter(canvas);

        VBox controls = createControlPanel();
        root.setRight(controls);

        Scene scene = new Scene(root, 850, 600);
        primaryStage.setTitle("K-Means Clustering Visualizer");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); 
        primaryStage.show();
    }

    private VBox createControlPanel() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setPrefWidth(250);
        vbox.setStyle("-fx-background-color: #161b22;"); 

        Label title = new Label("Algorithm Controls");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label kLabel = new Label("Number of Clusters (K): 3");
        kSlider = new Slider(2, 10, 3);
        kSlider.setShowTickMarks(true);
        kSlider.setShowTickLabels(true);
        kSlider.setMajorTickUnit(1);
        kSlider.setSnapToTicks(true);
        kSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            kLabel.setText("Number of Clusters (K): " + newVal.intValue())
        );

        // Setup the Timeline (Runs nextStep() every 500ms)
        autoPlayTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> nextStep()));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);

        Button btnGenerate = new Button("1. Generate Data");
        btnGenerate.setMaxWidth(Double.MAX_VALUE);
        btnGenerate.setOnAction(e -> {
            stopAutoPlay();
            generateData();
        });

        Button btnInit = new Button("2. Initialize Centroids");
        btnInit.setMaxWidth(Double.MAX_VALUE);
        btnInit.setOnAction(e -> {
            stopAutoPlay();
            initializeCentroids();
        });

        Button btnStep = new Button("3. Next Step");
        btnStep.setMaxWidth(Double.MAX_VALUE);
        btnStep.setOnAction(e -> {
            stopAutoPlay(); // Pause automation if user manually clicks step
            nextStep();
        });

        // --- NEW: Auto Play Button ---
        btnAutoPlay = new Button("4. Auto Play");
        btnAutoPlay.setMaxWidth(Double.MAX_VALUE);
        btnAutoPlay.setStyle("-fx-background-color: #238636; -fx-text-fill: white;"); 
        btnAutoPlay.setOnAction(e -> toggleAutoPlay());

        Button btnReset = new Button("Reset Board");
        btnReset.setMaxWidth(Double.MAX_VALUE);
        btnReset.setOnAction(e -> {
            stopAutoPlay();
            dataPoints.clear();
            centroids.clear();
            clearCanvas();
        });

        vbox.getChildren().addAll(title, kLabel, kSlider, btnGenerate, btnInit, btnStep, btnAutoPlay, btnReset);
        return vbox;
    }

    // --- NEW: Auto Play Logic ---
    private void toggleAutoPlay() {
        if (centroids.isEmpty() || dataPoints.isEmpty()) return; // Don't play if board is empty
        
        if (isPlaying) {
            stopAutoPlay();
        } else {
            isPlaying = true;
            btnAutoPlay.setText("Pause Auto Play");
            btnAutoPlay.setStyle("-fx-background-color: #da3633; -fx-text-fill: white;"); // Turn red when playing
            autoPlayTimeline.play();
        }
    }

    private void stopAutoPlay() {
        isPlaying = false;
        autoPlayTimeline.stop();
        btnAutoPlay.setText("4. Auto Play");
        btnAutoPlay.setStyle("-fx-background-color: #238636; -fx-text-fill: white;"); // Turn back to green
    }

    private void generateData() {
        dataPoints.clear();
        centroids.clear(); 
        for (int i = 0; i < 300; i++) {
            double x = 50 + random.nextDouble() * 500;
            double y = 50 + random.nextDouble() * 500;
            dataPoints.add(new DataPoint(x, y));
        }
        draw(); 
    }

    private void initializeCentroids() {
        if (dataPoints.isEmpty()) return; 
        
        centroids.clear();
        int k = (int) kSlider.getValue();
        
        for (int i = 0; i < k; i++) {
            DataPoint randomStartingPoint = dataPoints.get(random.nextInt(dataPoints.size()));
            centroids.add(new Centroid(randomStartingPoint.getX(), randomStartingPoint.getY(), CLUSTER_COLORS[i]));
        }
        
        for (DataPoint dp : dataPoints) {
            dp.setClusterIndex(-1);
        }
        draw();
    }

    private void nextStep() {
        if (centroids.isEmpty() || dataPoints.isEmpty()) return;

        // PHASE 1: Assign each point to the closest centroid
        for (DataPoint dp : dataPoints) {
            double minDistance = Double.MAX_VALUE;
            int closestCentroidIndex = -1;

            for (int i = 0; i < centroids.size(); i++) {
                Centroid c = centroids.get(i);
                double distance = Math.pow(dp.getX() - c.getX(), 2) + Math.pow(dp.getY() - c.getY(), 2);
                
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCentroidIndex = i;
                }
            }
            dp.setClusterIndex(closestCentroidIndex);
        }

        // PHASE 2: Move centroids to the mean of their assigned points
        for (int i = 0; i < centroids.size(); i++) {
            double sumX = 0;
            double sumY = 0;
            int count = 0;

            for (DataPoint dp : dataPoints) {
                if (dp.getClusterIndex() == i) {
                    sumX += dp.getX();
                    sumY += dp.getY();
                    count++;
                }
            }

            if (count > 0) {
                centroids.get(i).setX(sumX / count);
                centroids.get(i).setY(sumY / count);
            }
        }
        draw();
    }

    private void draw() {
        clearCanvas(); 
        
        for (DataPoint dp : dataPoints) {
            if (dp.getClusterIndex() == -1) {
                gc.setFill(Color.WHITE); 
            } else {
                gc.setFill(CLUSTER_COLORS[dp.getClusterIndex()]); 
            }
            gc.fillOval(dp.getX() - 2, dp.getY() - 2, 4, 4);
        }
        
        for (Centroid c : centroids) {
            gc.setFill(c.getColor());
            gc.fillOval(c.getX() - 8, c.getY() - 8, 16, 16); 
            
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.strokeOval(c.getX() - 8, c.getY() - 8, 16, 16);
        }
    }

    private void clearCanvas() {
        gc.setFill(Color.web("#0d1117")); 
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public static void main(String[] args) {
        launch(args);
    }
}