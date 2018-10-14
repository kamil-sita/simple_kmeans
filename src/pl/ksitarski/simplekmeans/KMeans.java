package pl.ksitarski.simplekmeans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class KMeans<T extends KMeansData> {

    private static KMeansLogger kmeansLogger;

    private final int RESULTS_COUNT; //number of expected results
    private final List<T> INPUT_POINTS; //points given by user

    private List<T> calculatedMeanPoints;
    private List<KMeansCluster<T>> clusters;

    private T genericInstanceCreator; //new instances of T will be created with this instance

    private Runnable onUpdate = null; //runnable run after every iteration
    private double percentProgress = 0;

    private boolean isInitialized = false;
    private boolean wasIterated = false;

    /**
     * Constructor of KMeans object
     * @param resultsCount expected number of results. Must be higher or equal to 1.
     * @param inputPoints input points in list. Cannot be null or empty.
     */
    public KMeans(int resultsCount, List<T> inputPoints) {
        if (resultsCount > inputPoints.size()) {
            log("Results count is bigger than inputPoints size. This might cause some problems with calculations.");
        }
        this.RESULTS_COUNT = resultsCount;
        if (inputPoints == null) {
            log("inputPoints cannot be null!");
            this.INPUT_POINTS = null;
            return;
        }
        this.INPUT_POINTS = Collections.unmodifiableList(inputPoints);
        try {
            checkIsDataCorrect();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        initializeData();
    }
    
    private void initializeData() {
        genericInstanceCreator = INPUT_POINTS.get(0);
        genericInstanceCreator = getNewRandomGenericInstance(); //genericInstanceCreator should not be dependant on given data, so it is instead generated from it.
        initializeRandomlyCalculatedMeanPoints();
        isInitialized = true;
    }

    private void initializeRandomlyCalculatedMeanPoints() {
        calculatedMeanPoints = new ArrayList<>();
        for (int i = 0; i < RESULTS_COUNT; i++) {
            calculatedMeanPoints.add(getNewRandomGenericInstance());
        }
    }

    /**
     * Runs <i>iterationCount</i> iterations of KMeans.
     * @param iterationCount iterations of KMeans.
     * @return returns this object for easier chaining of methods. Or null if was not initialized properly.
     */
    public KMeans iterate(int iterationCount) {
        if (!isInitialized) {
            log("Was not initialized!");
            return null;
        }
        if (iterationCount <= 0) {
            log("Iteration count cannot be lower or equal 0");
            throw new IllegalArgumentException("Iteration count cannot be lower or equal 0, is: " + iterationCount);
        }
        updateProgress(0);
        for (int i = 0; i < iterationCount; i++) {
            singleNonThreadedIteration();
            updateProgress((i+1)*1.0/iterationCount*1.0);
        }
        wasIterated = true;
        return this;
    }

    private void singleNonThreadedIteration() {
        groupPointsIntoClusters();
        calculateMeanPoints();
    }

    private void groupPointsIntoClusters() {
        initializeClusters();
        for (T point : INPUT_POINTS) {
            var closestMeanPoint = getClosestMeanPointTo(point);
            var idOfMeanPoint = getIdOfMeanPoint(closestMeanPoint);
            clusters.get(idOfMeanPoint).addPoint(point);
        }
    }

    private void initializeClusters() {
        clusters = new ArrayList<>();
        for (int i = 0; i < RESULTS_COUNT; i++) {
            clusters.add(new KMeansCluster<>());
        }
    }

    private void calculateMeanPoints() {
        calculatedMeanPoints = new ArrayList<>();
        for (var cluster : clusters) {
            T point = cluster.getMean();
            if (point == null) {
                point = getNewRandomGenericInstance();
            }
            calculatedMeanPoints.add(point);
        }
    }

    private int getIdOfMeanPoint(T point) {
        for (int i = 0; i < calculatedMeanPoints.size(); i++) {
            if (calculatedMeanPoints.get(i).equals(point)) {
                return i;
            }
        }
        return -1;
    }

    private T getClosestMeanPointTo(T point) {
        T closest = null;
        var distanceToClosest = 0.0;
        for (T kMeanPoint : calculatedMeanPoints) {
            var distance = kMeanPoint.distanceTo(point);
            if (closest == null || kMeanPoint.distanceTo(point) < distanceToClosest) {
                closest = kMeanPoint;
                distanceToClosest = distance;
            }
        }
        return closest;
    }

    private T getNewRandomGenericInstance() {
        int random = ThreadLocalRandom.current().nextInt(0, INPUT_POINTS.size());
        return INPUT_POINTS.get(random);
    }

    private void checkIsDataCorrect() {
        if (RESULTS_COUNT <= 0) {
            throw new IllegalArgumentException("resultsCount cannot be lower or equal to zero");
        }
        if (INPUT_POINTS == null) {
            throw new IllegalArgumentException("Data points list cannot be null");
        }
        if (INPUT_POINTS.size() == 0) {
            throw new IllegalArgumentException("Data points list cannot be empty");
        }
        for (T point : INPUT_POINTS) {
            if (point == null) {
                throw new IllegalArgumentException("Data point cannot be null");
            }
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private void updateProgress(double progress) {
        this.percentProgress = progress;
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    /**
     * Sets runnable that is called every time iteration is completed. Example case could be reporting percentProgress on user interface
     * @param runnable runnable that should be called on completion of the iteration
     * @return this, to make chaining methods easier
     */
    public KMeans setOnUpdate(Runnable runnable) {
        this.onUpdate = runnable;
        return this;
    }

    /**
     * Gets progress as a double between 0.0 and 1.0
     * @return percentProgress
     */
    public double getProgress() {
        return percentProgress;
    }

    /**
     * Returns calculated k-means points in form of a list. Some results may be null, especially after low amount of iterations.
     * @return list with calculated results.
     */
    public List<T> getCalculatedMeanPoints() {
        if (!wasIterated) {
            log("Cannot retrieve results before singleIteration!");
            return null;
        }
        return calculatedMeanPoints;
    }

    /**
     * Returns calculated k-means points in form of a clusters. Some results may be null, especially after low amount of iterations.
     * @return clusters with calculated results.
     */
    public List<KMeansCluster<T>> getClusters() {
        return clusters;
    }

    /**
     * Sets logger that implements KMeansLogger interface
     * @param logger logger
     */
    public static void setLogger(KMeansLogger logger) {
        kmeansLogger = logger;
    }

    private static void log(String msg) {
        if (kmeansLogger == null) {
            kmeansLogger = msg1 -> System.err.println("KMeans " + new SimpleDateFormat("(HH:mm:ss)").format(new Date()) + ": " + msg1);
            kmeansLogger.log("Logger not set, will use default");
        }
        kmeansLogger.log(msg);
    }
}
