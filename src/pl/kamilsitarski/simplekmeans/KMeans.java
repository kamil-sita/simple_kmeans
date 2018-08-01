package pl.kamilsitarski.simplekmeans;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class KMeans<T extends SimpleKMeansData> {

    private final int KMEANS_RESULTS_POINTS_COUNT;
    private final List<T> INPUT_POINTS;

    private List<T> calculatedKMeansPoints;
    private T tInstanceCreator;
    private boolean wasInitialized = false;
    private boolean wasIterated = false;

    /**
     * Constructor of KMeans object
     * @param resultsCount expected number of results. Must be higher or equal to 1.
     * @param inputPoints input points in list. Cannot be null or empty.
     */
    public KMeans(int resultsCount, List<T> inputPoints) {
        this.KMEANS_RESULTS_POINTS_COUNT = resultsCount;
        this.INPUT_POINTS = inputPoints;
        try {
            checkIsDataCorrect();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        initialize();
    }

    /**
     * Runs <i>iterationCount</i> iterations of KMeans.
     * @param iterationCount iterations of KMeans.
     */
    public void iterate(int iterationCount) {
        if (!wasInitialized) {
            log("Was not initialized!");
            return;
        }
        if (iterationCount <= 0) {
            log("Iteration count cannot be lower or equal 0");
            return;
        }
        for (int i = 0; i < iterationCount; i++) {
            singleNonThreadedIteration();
        }
        wasIterated = true;
    }

    private void singleNonThreadedIteration() {
        List<T>[] pointsClosestToKMeansPoints = findPointsClosestToLastCalculatedKMeansPoints();
        calculatedKMeansPoints = new ArrayList<>();
        for (int i = 0; i < KMEANS_RESULTS_POINTS_COUNT; i++) {
            T point = getMeanOfListRelatedToIteration(i, pointsClosestToKMeansPoints);
            if (point == null) {
                point = getNewRandomTInstance();
            }
            calculatedKMeansPoints.add(point);
        }

    }


    /**
     * Runs <i>iterationCount</i> iterations of KMeans with <i>threadCount</i> threads.
     * @param iterationCount number of iterations
     * @param threadsCount maximum number of threads to use
     */
    public void iterateWithThreads(int iterationCount, int threadsCount) {
        if (!wasInitialized) {
            log("Was not initialized!");
            return;
        }
        if (iterationCount <= 0) {
            log("Iteration count cannot be lower or equal 0");
            return;
        }
        if (threadsCount < 1) {
            log("Thread count cannot be lower than 1");
            return;
        }
        int realThreadCount;
        if (KMEANS_RESULTS_POINTS_COUNT < threadsCount) {
            realThreadCount = KMEANS_RESULTS_POINTS_COUNT;
        } else {
            realThreadCount = threadsCount;
        }
        runThreadedIterations(iterationCount, realThreadCount);
        wasIterated = true;
    }

    /**
     * Returns calculated KMeans in form of a list
     * @return list with calculated results.
     */
    public List<T> getResults() {
        if (!wasIterated) {
            log("Cannot retrieve results before singleIteration!");
            return null;
        }
        return calculatedKMeansPoints;
    }

    private void runThreadedIterations(int iterations, int threadCount) {
        Semaphore iterationCompletedSemaphore = new Semaphore(0);
        Thread[] threads = new Thread[threadCount];
        List<ThreadedKMeans> threadedKMeans = new ArrayList<>(threadCount);

        for (int i = 0; i < threadCount; i++) {
            threadedKMeans.add(new ThreadedKMeans(iterationCompletedSemaphore, i, threadCount));
            threads[i] = new Thread(threadedKMeans.get(i));
        }

        for (int i = 0; i < iterations; i++) {
            List<T>[] pointsClosestToKMeansPoints = findPointsClosestToLastCalculatedKMeansPoints();
            calculatedKMeansPoints = new ArrayList<>();
            for (int threadId = 0; threadId < threadCount; threadId++) {
                threadedKMeans.get(threadId).setPointsClosestToKMeansPoints(pointsClosestToKMeansPoints);
                threads[threadId].run();
            }
            try {
                //all threads completed their iteration
                iterationCompletedSemaphore.acquire(threadCount);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
        wasIterated = true;
    }

    private class ThreadedKMeans implements Runnable {

        private Semaphore isFinishedSemaphore;
        private int threadId;
        private int allThreadCount;
        private List<T>[] pointsClosestToKMeansPoints;

        public ThreadedKMeans(Semaphore isFinishedSemaphore, int threadId, int allThreadCount) {
            this.isFinishedSemaphore = isFinishedSemaphore;
            this.threadId = threadId;
            this.allThreadCount = allThreadCount;
        }

        public void setPointsClosestToKMeansPoints(List<T>[] pointsClosestToKMeansPoints) {
            this.pointsClosestToKMeansPoints = pointsClosestToKMeansPoints;
        }

        @Override
        public void run() {
            if (pointsClosestToKMeansPoints == null) return;
            iterateMainLoopThreadedInThread(allThreadCount, threadId, pointsClosestToKMeansPoints);
            isFinishedSemaphore.release();
        }

        private void iterateMainLoopThreadedInThread(int allThreadsCount, int threadId, List<T>[] pointsClosestToKMeansPoints) {
            for (int i = 0; i < KMEANS_RESULTS_POINTS_COUNT; i++) {
                if (i % allThreadsCount != threadId) continue;
                T point = getMeanOfListRelatedToIteration(i, pointsClosestToKMeansPoints);
                if (point == null) {
                    point = getNewRandomTInstance();
                }
                calculatedKMeansPoints.add(point);
            }
        }
    }

    private List<T>[] findPointsClosestToLastCalculatedKMeansPoints() {
        List<T>[] pointsClosestToKMeansPoints = initializeEmptyListArrayOfSize(KMEANS_RESULTS_POINTS_COUNT);

        for (int i = 0; i < INPUT_POINTS.size(); i++) {
            T closestKMeanPoint = getClosestCalculatedKMeanPointTo(INPUT_POINTS.get(i));
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

    private T getMeanOfListRelatedToIteration(int iteration, List<T>[] lists) {
        return (T) getNewRandomTInstance().meanOfList(
                (List<SimpleKMeansData>) lists[iteration]
        );
    }

    private void initialize() {
        tInstanceCreator = INPUT_POINTS.get(0);
        tInstanceCreator = getNewRandomTInstance();
        calculatedKMeansPoints = new ArrayList<>();
        for (int i = 0; i < KMEANS_RESULTS_POINTS_COUNT; i++) {
            calculatedKMeansPoints.add(getNewRandomTInstance());
        }
        wasInitialized = true;
    }

    private int getIdOfCalculatedKMeanPoint(T point) {
        for (int i = 0; i < calculatedKMeansPoints.size(); i++) {
            if (calculatedKMeansPoints.get(i).equals(point)) {
                return i;
            }
        }
        return -1;
    }

    private T getClosestCalculatedKMeanPointTo(T point) {
        T closest = null;
        double distanceToClosest = 0;
        for (T kMeanPoint : calculatedKMeansPoints) {
            double distance = kMeanPoint.distanceTo(point);
            if (closest == null || kMeanPoint.distanceTo(point) < distanceToClosest) {
                closest = kMeanPoint;
                distanceToClosest = distance;
            }
        }
        return closest;
    }

    private T getNewRandomTInstance() {
        return (T) tInstanceCreator.getNewWithRandomData();
    }

    private void checkIsDataCorrect() throws Exception {
        if (KMEANS_RESULTS_POINTS_COUNT <= 0) {
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
        return wasInitialized;
    }

    private void log(String msg) {
        System.out.println("KMeans: " + msg);
    }

}
