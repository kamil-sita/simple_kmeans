package pl.ksitarski.simplekmeans;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KMeans<T extends KMeansData> {

    private static KMeansLogger kmeansLogger;

    private final int RESULTS_COUNT; //number of expected results
    private final List<T> INPUT_POINTS; //points given by user

    private List<T> calculatedMeanPoints;
    private T genericInstanceCreator; //new instances of T will be created with this instance

    private Runnable onUpdate = null;
    private double iterationProgress = 0;

    private boolean isInitialized = false;
    private boolean wasIterated = false;

    /**
     * Constructor of KMeans object
     * @param resultsCount expected number of results. Must be higher or equal to 1.
     * @param inputPoints input points in list. Cannot be null or empty.
     */
    public KMeans(int resultsCount, List<T> inputPoints) {
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
     */
    public void iterate(int iterationCount) {
        if (!isInitialized) {
            log("Was not initialized!");
            return;
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
    }

    private void singleNonThreadedIteration() {
        var pointsClosestToMeanPoints = getPointsClosestToLastCalculatedMeanPoints();
        calculatedMeanPoints = new ArrayList<>();
        for (int pointId = 0; pointId < RESULTS_COUNT; pointId++) {
            T point = getMeanOfListRelatedToPoint(pointId, pointsClosestToMeanPoints);
            if (point == null) {
                point = getNewRandomGenericInstance();
            }
            calculatedMeanPoints.add(point);
        }
    }

    private static void log(String msg) {
        if (kmeansLogger == null) {
            kmeansLogger = msg1 -> System.out.println("KMeans " + new SimpleDateFormat("(HH:mm:ss)") + ": " + msg1);
            kmeansLogger.log("Logger not set, will use default");
        }
        kmeansLogger.log(msg);
    }

    private List<T>[] getPointsClosestToLastCalculatedMeanPoints() {
        List<T>[] pointsClosestToKMeansPoints = initializeEmptyListArrayOfSize(RESULTS_COUNT);

        for (int i = 0; i < INPUT_POINTS.size(); i++) {
            var closestKMeanPoint = getClosestCalculatedMeanPointTo(INPUT_POINTS.get(i));
            int idOfKMeanPoint = getIdOfCalculatedKMeanPoint(closestKMeanPoint);
            pointsClosestToKMeansPoints[idOfKMeanPoint].add(INPUT_POINTS.get(i));
        }

        return pointsClosestToKMeansPoints;
    }

    private List<T>[] initializeEmptyListArrayOfSize(final int SIZE) {
        List<T>[] lists = new ArrayList[SIZE];
        for (int i = 0; i < SIZE; i++) {
            lists[i] = new ArrayList<>();
        }
        return lists;
    }

    private T getMeanOfListRelatedToPoint(int iteration, List<T>[] lists) {
        return (T) getNewRandomGenericInstance().meanOfList(
                (List<KMeansData>) lists[iteration]
        );
    }

    private int getIdOfCalculatedKMeanPoint(T point) {
        for (int i = 0; i < calculatedMeanPoints.size(); i++) {
            if (calculatedMeanPoints.get(i).equals(point)) {
                return i;
            }
        }
        return -1;
    }

    private T getClosestCalculatedMeanPointTo(T point) {
        T closest = null;
        double distanceToClosest = 0;
        for (T kMeanPoint : calculatedMeanPoints) {
            double distance = kMeanPoint.distanceTo(point);
            if (closest == null || kMeanPoint.distanceTo(point) < distanceToClosest) {
                closest = kMeanPoint;
                distanceToClosest = distance;
            }
        }
        return closest;
    }

    private T getNewRandomGenericInstance() {
        return (T) genericInstanceCreator.getNewWithRandomData();
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
        this.iterationProgress = progress;
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    /**
     * Sets runnable that is called every time iteration is completed. Example case could be reporting iterationProgress on user interface
     * @param runnable runnable that should be called on completion of the iteration
     */
    public void setOnUpdate(Runnable runnable) {
        this.onUpdate = runnable;
    }

    /**
     * Gets iterationProgress as a double between 0.0 and 1.0
     * @return percent iterationProgress
     */
    public double getProgress() {
        return iterationProgress;
    }

    /**
     * Returns calculated KMeans in form of a list. Some results may be null, especially after low amount of iterations.
     * @return list with calculated results.
     */
    public List<T> getResults() {
        if (!wasIterated) {
            log("Cannot retrieve results before singleIteration!");
            return null;
        }
        return calculatedMeanPoints;
    }

    /**
     * Sets logger that implements KMeansLogger interface
     * @param logger logger
     */
    public static void setLogger(KMeansLogger logger) {
        kmeansLogger = logger;
    }
}
