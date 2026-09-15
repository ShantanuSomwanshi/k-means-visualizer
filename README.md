# K-Means Visualizer Using Java

A desktop JavaFX application for learning and demonstrating the K-means clustering algorithm interactively.

The visualizer creates 300 random two-dimensional data points, places randomly selected centroids on a canvas, and shows how the centroids move as points are assigned to clusters. You can run the algorithm one iteration at a time or watch it automatically.

## Features

- Generate 300 random data points.
- Choose the number of clusters, `K`, from 2 to 10.
- Initialize centroids from randomly selected data points.
- Visualize nearest-centroid assignment using cluster colors.
- Move centroids to the mean of their assigned points.
- Run individual K-means iterations manually.
- Run iterations automatically every 500 milliseconds.
- Pause automatic playback.
- Reset the board and start a new experiment.
- Use a dark JavaFX interface with a 600 x 600 visualization canvas.

## Preview

When the application starts, it displays an empty dark canvas with an **Algorithm Controls** panel. Use the controls in order:

1. Generate data.
2. Initialize centroids.
3. Advance one step or start Auto Play.

Unassigned points are white. Assigned points use the color of their cluster. Centroids are larger colored circles with black outlines.

## How K-means Works

Each iteration has two phases.

### 1. Assignment

Every point is assigned to the closest centroid using squared Euclidean distance:

```text
distance = (point.x - centroid.x)^2 + (point.y - centroid.y)^2
```

### 2. Update

Each centroid moves to the average position of all points assigned to it:

```text
newX = sum(point.x) / numberOfAssignedPoints
newY = sum(point.y) / numberOfAssignedPoints
```

If a cluster has no assigned points, its centroid remains at its previous position.

## Requirements

- Java Development Kit capable of compiling Java 25 source code.
- Apache Maven.
- A desktop environment that supports JavaFX.
- Internet access on the first Maven build so dependencies can be downloaded.

The project is currently configured with JavaFX 22 and Maven.

## Run the Application

Clone the repository and enter the project directory:

```bash
git clone https://github.com/ShantanuSomwanshi/k-means-visualizer.git
cd k-means-visualizer
```

The Maven project is located in the repository root. Compile it with:

```bash
mvn clean compile
```

Run the JavaFX application with:

```bash
mvn javafx:run
```

Run the test lifecycle with:

```bash
mvn test
```

There are currently no project-specific test classes, but the command remains useful for checking the Maven lifecycle.

## Using the Visualizer

1. Start the application with `mvn javafx:run`.
2. Set `K` with the **Number of Clusters** slider.
3. Click **1. Generate Data**.
4. Click **2. Initialize Centroids**.
5. Click **3. Next Step** to inspect one iteration at a time.
6. Click **4. Auto Play** to run iterations every 500 milliseconds.
7. Click **Pause Auto Play** to stop automatic execution.
8. Click **Reset Board** to clear the visualization.

Generating data, initializing centroids, stepping manually, and resetting the board automatically stop playback.

## Project Structure

```text
k-means-visualizer/
├── pom.xml
├── README.md
├── PROJECT_INFO.md
├── .gitignore
└── src/
    ├── main/
    │   ├── java/com/kmeans/
    │   │   ├── Main.java
    │   │   ├── DataPoint.java
    │   │   └── Centroid.java
    │   └── resources/
    └── test/
        └── java/
```

### Main Classes

- `Main.java`: JavaFX application, controls, rendering, animation, and K-means execution.
- `DataPoint.java`: Stores a point's `x` and `y` coordinates and its cluster index.
- `Centroid.java`: Stores a centroid's coordinates and display color.

## Technology Stack

| Technology          | Version or Purpose              |
| ------------------- | ------------------------------- |
| Java                | Source and target level 25      |
| Maven               | Build and dependency management |
| JavaFX Controls     | User interface controls         |
| JavaFX Graphics     | Canvas and graphics rendering   |
| AtlantaFX           | `PrimerDark` application theme  |
| Apache Commons Math | Declared math dependency        |
| FastCSV             | Declared CSV parsing dependency |

The current implementation calculates distances and means directly in the application code. Apache Commons Math and FastCSV are declared in `pom.xml`, but CSV import and Commons Math APIs are not currently used by the visualizer.

## Implementation Details

- Data coordinates are generated between 50 and 550 on both axes.
- The canvas is 600 x 600 pixels.
- Data points are rendered as 4 x 4 ovals.
- Centroids are rendered as 16 x 16 ovals.
- The application uses ten predefined cluster colors, matching the maximum `K` value of 10.
- Centroid initialization is random and may select the same data point more than once.
- Auto Play uses a JavaFX `Timeline` with a 500 millisecond key frame.
- The application window is fixed at 850 x 600 pixels and cannot be resized.

## Limitations

- There is no convergence indicator or iteration counter.
- Auto Play has no automatic stopping condition or maximum iteration limit.
- Data points cannot be added or moved with the mouse.
- Results are not reproducible because random generation does not use a fixed seed.
- Duplicate initial centroid positions are possible.
- CSV import and export are not implemented.
- Cluster sizes, inertia, and other statistics are not displayed.
- No automated project-specific tests are currently included.

## Possible Improvements

- Add a convergence check and maximum iteration limit.
- Prevent duplicate centroid initialization.
- Display iteration count, cluster sizes, and inertia.
- Add adjustable animation speed and random seed controls.
- Support interactive point creation and dragging.
- Implement CSV import and export.
- Extract the clustering logic into a separate testable service.
- Add unit tests for assignment, centroid updates, empty clusters, and reset behavior.

## Git Notes

Generated files are excluded by `.gitignore`, including Maven's `target/` directory, compiled `.class` files, IDE metadata, operating-system files, logs, and temporary files. Source code, Maven configuration, and project documentation are tracked.

## License

No license has been added to this repository yet. Add a license file before distributing the project if a specific reuse policy is required.

## Author

**Shantanu Somwanshi**

Repository: <https://github.com/ShantanuSomwanshi/k-means-visualizer>
