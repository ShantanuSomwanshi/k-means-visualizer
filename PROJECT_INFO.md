# K-Means Visualizer Using Java

## Project Summary

**K-Means Visualizer Using Java** is a desktop visualization application that demonstrates the K-means clustering algorithm one iteration at a time. It is implemented with JavaFX and packaged as a Maven project.

The application displays randomly generated two-dimensional data points on a canvas. Users select the number of clusters, initialize random centroids, and then either advance the algorithm manually or run it automatically.

## Technology Stack

- **Language:** Java
- **Build system:** Apache Maven
- **User interface:** JavaFX Controls, Graphics, and Canvas
- **Theme:** AtlantaFX `PrimerDark`
- **Math dependency:** Apache Commons Math 3.6.1
- **CSV dependency:** FastCSV 3.1.0
- **Configured Java source/target:** Java 25
- **Configured JavaFX version:** 22
- **Application entry point:** `com.kmeans.Main`

The current implementation directly uses JavaFX. Apache Commons Math and FastCSV are declared in `pom.xml`, but the current source code performs its distance and averaging calculations directly and does not currently parse CSV files.

## Features

- Generates 300 random two-dimensional data points.
- Restricts generated coordinates to the range 50 through 550 on both axes.
- Selects a cluster count `K` from 2 through 10, with a default of 3.
- Initializes centroids by selecting existing data points at random.
- Assigns each data point to its nearest centroid.
- Moves each centroid to the arithmetic mean of its assigned points.
- Supports manual, step-by-step execution.
- Supports automatic playback at one iteration every 500 milliseconds.
- Allows automatic playback to be paused.
- Uses a distinct color for each cluster and centroid.
- Resets the board by removing all data points and centroids.
- Uses a fixed-size dark-themed desktop interface.

## User Interface

The window is titled **K-Means Clustering Visualizer** and is not resizable. It has two main areas:

1. **Visualization canvas:** A 600 by 600 JavaFX canvas where points and centroids are drawn.
2. **Control panel:** A right-side panel with a preferred width of 250 pixels and controls for configuring and running the algorithm.

The scene is created at 850 by 600 pixels.

### Controls

| Control                   | Behavior                                                                                                                    |
| ------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| Number of Clusters slider | Selects `K` from 2 to 10 using integer tick snapping.                                                                       |
| `1. Generate Data`        | Clears the current points and centroids, creates 300 new random points, and redraws the canvas.                             |
| `2. Initialize Centroids` | Creates `K` centroids from randomly selected data points and clears previous assignments. Does nothing when no data exists. |
| `3. Next Step`            | Performs one assignment phase followed by one centroid-update phase.                                                        |
| `4. Auto Play`            | Runs `Next Step` every 500 milliseconds. The button changes to `Pause Auto Play` while running.                             |
| `Reset Board`             | Stops playback, removes all points and centroids, and clears the canvas.                                                    |

Generating data, initializing centroids, stepping manually, and resetting the board all stop automatic playback first.

## Algorithm

The implementation uses the standard two-phase K-means iteration.

### 1. Data generation

`generateData()` creates 300 `DataPoint` objects. For each point:

```text
x = 50 + random value in [0, 500)
y = 50 + random value in [0, 500)
```

Generating new data also removes all current centroids.

### 2. Centroid initialization

`initializeCentroids()` reads `K` from the slider and chooses `K` existing data points randomly as the starting centroid positions. Each centroid receives one color from the predefined cluster color array.

All data points are then marked as unassigned with a cluster index of `-1`.

### 3. Assignment phase

For every data point, the application calculates the squared Euclidean distance to every centroid:

```text
distance = (point.x - centroid.x)^2 + (point.y - centroid.y)^2
```

The point receives the index of the centroid with the smallest distance. Squared distance is sufficient because taking a square root would not change which centroid is closest.

### 4. Update phase

For each centroid, the application calculates the average `x` and `y` coordinates of all points assigned to that centroid. If the centroid has at least one assigned point, it moves to that average position.

Empty clusters are retained at their previous positions because their centroids are not updated when their assigned-point count is zero.

### 5. Rendering

After each operation that changes the model, `draw()` clears and redraws the canvas:

- Unassigned points are white.
- Assigned points use the color of their cluster.
- Data points are rendered as 4 by 4 ovals.
- Centroids are rendered as 16 by 16 colored ovals with black outlines.
- The canvas background is `#0d1117`.

## Source Code Structure

```text
visualizer/
├── pom.xml
├── PROJECT_INFO.md
├── src/
│   ├── main/
│   │   ├── java/com/kmeans/
│   │   │   ├── Centroid.java
│   │   │   ├── DataPoint.java
│   │   │   └── Main.java
│   │   └── resources/
│   └── test/
│       └── java/
└── target/
    ├── classes/com/kmeans/
    ├── generated-sources/annotations/
    └── maven-status/
```

### `Main.java`

Defines the JavaFX `Application`, creates the window and controls, manages points and centroids, runs the K-means phases, handles auto-play timing, and renders the visualization.

Important state includes:

- `Canvas canvas` and `GraphicsContext gc` for drawing.
- `List<DataPoint> dataPoints` for the generated observations.
- `List<Centroid> centroids` for the current cluster centers.
- `Slider kSlider` for selecting the cluster count.
- `Timeline autoPlayTimeline` for repeated execution.
- `boolean isPlaying` for auto-play state.
- A ten-color `CLUSTER_COLORS` palette.

### `DataPoint.java`

Represents one two-dimensional point with:

- `x` coordinate.
- `y` coordinate.
- `clusterIndex`, initialized to `-1` until assignment.

It provides getters for coordinates and getters/setters for the cluster index.

### `Centroid.java`

Represents one cluster center with:

- Mutable `x` coordinate.
- Mutable `y` coordinate.
- A JavaFX `Color` used for rendering.

It provides getters and setters for coordinates and a getter for the color.

## Maven Configuration

The Maven coordinates are:

- **Group ID:** `com.kmeans`
- **Artifact ID:** `visualizer`
- **Version:** `1.0-SNAPSHOT`

The configured plugins are:

- `maven-compiler-plugin` version `3.13.0` for compilation.
- `javafx-maven-plugin` version `0.0.8` with `com.kmeans.Main` as the main class.

## How to Build and Run

Run these commands from the directory containing `pom.xml`:

```bash
mvn clean compile
mvn javafx:run
```

To run the test lifecycle, use:

```bash
mvn test
```

A JDK capable of compiling Java 25 source is required by the current Maven configuration. Maven must also be able to resolve the declared dependencies from its configured repositories.

## Typical Usage Workflow

1. Start the application.
2. Choose the desired value of `K` with the slider.
3. Select `1. Generate Data`.
4. Select `2. Initialize Centroids`.
5. Select `3. Next Step` to observe individual iterations, or select `4. Auto Play` to run continuously.
6. Pause auto-play when needed, or use `Reset Board` to start over.

The application does not currently provide a convergence indicator, iteration counter, inertia score, custom point input, data import, or export functionality.

## Current Limitations and Implementation Notes

- Random data and centroid initialization use `java.util.Random` without a fixed seed, so each run produces different results.
- Centroids may be initialized at the same data point because initialization samples points independently and does not enforce uniqueness.
- The algorithm has no explicit convergence test or maximum iteration count. Auto-play continues until the user pauses it; once the centroids stop moving, later iterations produce the same result.
- The UI does not expose the current iteration number or cluster sizes.
- Data points cannot currently be added or moved with the mouse.
- CSV import is not implemented even though FastCSV is declared as a dependency.
- No project-specific test classes are currently present under `src/test/java`.
- `target/` contains generated compilation output and should be treated as build output rather than source code.
- The color array contains ten colors, matching the maximum slider value of 10.

## Directory Inventory

### Handwritten project files

- `pom.xml`: Maven project metadata, dependencies, Java version, and JavaFX run configuration.
- `src/main/java/com/kmeans/Main.java`: JavaFX application, controls, algorithm, animation, and rendering.
- `src/main/java/com/kmeans/DataPoint.java`: Data point model.
- `src/main/java/com/kmeans/Centroid.java`: Centroid model.

### Empty source directories

- `src/main/resources/`: No resource files are currently present.
- `src/test/java/`: No test source files are currently present.
- `target/generated-sources/annotations/`: No generated annotation sources are currently present.

### Generated build files

- `target/classes/com/kmeans/*.class`: Compiled classes for the three Java source files.
- `target/maven-status/maven-compiler-plugin/compile/default-compile/createdFiles.lst`: List of compiled class files.
- `target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst`: List of Java source files used by the compiler.

## Future Improvements

Potential extensions include:

- Add a convergence check and a maximum iteration limit.
- Prevent duplicate centroid initialization.
- Add an iteration counter and convergence status.
- Allow users to create points interactively.
- Add CSV import and export using the existing FastCSV dependency.
- Extract the clustering algorithm from the JavaFX controller into a testable service class.
- Add unit tests for point assignment, centroid updates, empty clusters, and reset behavior.
- Add controls for animation speed and random seed.
- Display cluster statistics and a visual legend.
