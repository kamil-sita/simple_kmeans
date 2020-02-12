package pl.ksitarski.simplekmeans;
import java.util.*;
import java.util.concurrent.*;

public class KMeans<T> {

    private final KMeansBuilder.Arguments<T> arguments;

    private List<T> calculatedMeanPoints;
    private List<KMeansCluster<T>> clusters;

    private double percentProgress = 0;

    private boolean wasIterated = false;

    private ExecutorService executorService = null;

    private volatile boolean canContinue = true;

    private int estimatedCapacityPerCluster;

    private boolean firstOptimizedRun = true;
    private double[] oldLengths;
    private int[] oldIndexes;
    private double lastStdDev;
    private int notCounted = 0;
    private int counted = 0;

    private final int INPUT_POINTS_COUNT;
    private final int RESULTS_COUNT;

    KMeans(KMeansBuilder.Arguments<T> args) {
        this.arguments = args;
        this.INPUT_POINTS_COUNT = args.getInputPoints().size();
        this.RESULTS_COUNT = args.getResultCount();
        initializeRandomlyCalculatedMeanPoints();
        estimatedCapacityPerCluster = (int) (1.2 * INPUT_POINTS_COUNT/RESULTS_COUNT);
    }

    private void initializeRandomlyCalculatedMeanPoints() {
        calculatedMeanPoints = new ArrayList<>(RESULTS_COUNT);
        for (int i = 0; i < RESULTS_COUNT; i++) {
            calculatedMeanPoints.add(getNewRandomGenericInstance());
        }
    }

    /**
     * Runs <i>iterationCount</i> iterations of KMeans.
     * @param iterationCount iterations of KMeans.
     * @return returns this object for easier chaining of methods.
     */
    public KMeans<T> iterate(int iterationCount) {
        if (iterationCount <= 0) {
            throw new IllegalArgumentException("Iteration count cannot be lower or equal 0, is: " + iterationCount);
        }
        try {
            setupIteration();
            for (int i = 0; i < iterationCount; i++) {
                if (!canContinue) {
                    break;
                }
                singleIteration();
                wasIterated = true;
                updateProgress((i+1)*1.0/iterationCount*1.0);
            }
            return this;
        } finally {
            stopIteration();
        }
    }

    /**
     * Iterates until standard deviation delta is smaller than given delta. In this mode progress percentage is approximation.
     * @param delta minimum difference between the standard deviation of two consecutive iterations that causes execution to stop
     * @return this object for easier chaining of methods.
     */
    public KMeans<T> iterateUntilStandardDeviationDeltaSmallerOrEqualTo(double delta) {
        return iterateUntilStandardDeviationDeltaSmallerOrEqualTo(delta, 0);
    }

    /**
     * Iterates until standard deviation delta is smaller than given delta. In this mode progress percentage is approximation.
     * @param delta minimum difference between the standard deviation of two consecutive iterations that causes execution to stop
     * @param iterationCountSafeguard maximum number of iterations. Numbers below 1 are ignored.
     * @return this object for easier chaining of methods.
     */
    public KMeans<T> iterateUntilStandardDeviationDeltaSmallerOrEqualTo(double delta, int iterationCountSafeguard) {
        try {
            setupIteration();
            double lastStdDev = Double.MAX_VALUE;
            boolean iterate = true;
            int iterationsDone = 0;
            while (iterate) {
                if (!canContinue) {
                    break;
                }
                singleIteration();
                wasIterated = true;
                double stdDev = getStandardDeviation();
                double currentDelta = lastStdDev - stdDev;
                iterationsDone++;

                if (currentDelta < delta || (iterationsDone >= iterationCountSafeguard && iterationCountSafeguard > 0)) {
                    iterate = false;
                }

                //aproximate progress
                double invertedTargetDelta = 1/delta;
                double currentInvertedDelta = 1/currentDelta;

                double progress = currentInvertedDelta/invertedTargetDelta;
                if (progress > 1) progress = 1;

                updateProgress(progress);
                lastStdDev = stdDev;
            }
            return this;
        } finally {
            stopIteration();
        }
    }


    private void setupIteration() {
        canContinue = true;
        updateProgress(0);
        if (arguments.isMultithreaded()) {
            executorService = Executors.newFixedThreadPool(arguments.getThreadsMax());
        }
    }


    private void stopIteration() {
        if (arguments.isMultithreaded() && executorService != null) {
            executorService.shutdown();
        }
    }

    private void singleIteration() {
        if (arguments.isDontUpdateAllOptimization()) {
            if (firstOptimizedRun) {
                oldLengths = new double[INPUT_POINTS_COUNT];
                oldIndexes = new int[INPUT_POINTS_COUNT];
            } else {
                lastStdDev = getStandardDeviation();
            }
        }
        groupPointsIntoClusters();
        calculateMeanPoints();
        if (arguments.isDontUpdateAllOptimization()) {
            firstOptimizedRun = false;
        }
    }

    private void groupPointsIntoClusters() {
        if (executorService == null) {
            groupPointsIntoClustersNoThreads();
        } else {
            groupPointsIntoClustersThreads();
        }
    }

    private void groupPointsIntoClustersThreads() {
        final CountDownLatch countDownLatch = new CountDownLatch(arguments.getThreadsMax());
        final int threadCount = arguments.getThreadsMax();

        initializeClusters(true);

        final int workPerThread = INPUT_POINTS_COUNT/threadCount;
        final int mostThreadsWork = workPerThread * (threadCount - 1);
        final int lastThreadWork = INPUT_POINTS_COUNT - mostThreadsWork;
        int workDistributed = 0;

        for (int i = 0; i < threadCount; i++) {
            int finalWorkDistributed = workDistributed;
            if (i + 1 != threadCount) { //not last thread
                executorService.submit(() -> {
                    calculatePartial(finalWorkDistributed, finalWorkDistributed + workPerThread);
                    countDownLatch.countDown();
                });
            } else {
                executorService.submit(() -> {
                    calculatePartial(finalWorkDistributed, finalWorkDistributed + lastThreadWork);
                    countDownLatch.countDown();
                });
            }
            workDistributed += workPerThread;
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void calculatePartial(int from, int to) {
        for (int i = from; i < to; i++) {
            var point = arguments.getInputPoints().get(i);
            counted++;

            if (arguments.isDontUpdateAllOptimization() && !firstOptimizedRun) {
                if (oldLengths[i] < lastStdDev) {
                    var closestMeanPoint = oldIndexes[i];
                    double length = arguments.getDataLength().getLength(calculatedMeanPoints.get(closestMeanPoint), point);
                    clusters.get(oldIndexes[i]).addPoint(arguments.getInputPoints().get(i), length);
                    oldLengths[i] = length;
                    notCounted++;
                    continue;
                }
            }

            var pointAndLength = getClosestMeanPointTo(point);
            var idOfMeanPoint = getIdOfMeanPoint(pointAndLength.t);
            clusters.get(idOfMeanPoint).addPoint(point, pointAndLength.length);

            if (arguments.isDontUpdateAllOptimization()) {
                oldLengths[i] = pointAndLength.length;
                oldIndexes[i] = idOfMeanPoint;
            }
        }
    }

    private void groupPointsIntoClustersNoThreads() {
        initializeClusters(false);
        calculatePartial(0, INPUT_POINTS_COUNT);
    }

    private void initializeClusters(boolean threaded) {
        clusters = new ArrayList<>(RESULTS_COUNT);
        for (int i = 0; i < RESULTS_COUNT; i++) {
            clusters.add(new KMeansCluster<>(threaded, estimatedCapacityPerCluster, arguments.getDataToMean()));
        }
    }

    private void calculateMeanPoints() {
        calculatedMeanPoints = new ArrayList<>(RESULTS_COUNT);
        for (var cluster : clusters) {
            T point = cluster.getMean();
            if (point == null) {
                point = getNewRandomGenericInstance();
            }
            calculatedMeanPoints.add(point);
        }
    }

    private int getIdOfMeanPoint(T point) {
        return calculatedMeanPoints.indexOf(point);
    }

    private PointAndLength<T> getClosestMeanPointTo(T point) {
        T closest = null;
        var distanceToClosest = 0.0;
        for (T kMeanPoint : calculatedMeanPoints) {
            var distance = arguments.getDataLength().getLength(kMeanPoint, point);
            if (closest == null || distance < distanceToClosest) {
                closest = kMeanPoint;
                distanceToClosest = distance;
            }
        }
        return new PointAndLength<>(closest, distanceToClosest);
    }

    private static class PointAndLength<T> {
        T t;
        double length;

        public PointAndLength(T t, double length) {
            this.t = t;
            this.length = length;
        }
    }

    private T getNewRandomGenericInstance() {
        int random = ThreadLocalRandom.current().nextInt(0, INPUT_POINTS_COUNT);
        return arguments.getInputPoints().get(random);
    }


    private void updateProgress(double progress) {
        this.percentProgress = progress;
        if (arguments.getOnUpdate() != null) {
            arguments.getOnUpdate().onUpdate(progress);
        }
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
            throw new RuntimeException("Cannot get results before iterating");
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
     * Aborts execution of k-means algorithm after current iteration.
     */
    public void earlyStop() {
        canContinue = false;
    }

    /**
     * Calculatese deviation for current cluster
     */
    public double getStandardDeviation() {
        if (calculatedMeanPoints == null) return Double.POSITIVE_INFINITY;

        double sum = 0;
        int weight = 0;

        for (var cluster : clusters) {
            sum += cluster.getStandardDeviation() * cluster.getSize();
            weight += cluster.getSize();
        }

        return sum/weight;
    }
}
