package pl.ksitarski.simplekmeans;

import java.util.Collections;
import java.util.List;

public class KMeansBuilder<T> {

    private final List<T> INPUT_POINTS;
    private final int RESULT_COUNT;
    private final DataToMean<T> dataToMean;
    private final DataLength<T> dataLength;

    private boolean multithreaded = false;
    private int threadsMax = -1;
    private OnUpdate onUpdate;

    private boolean dontUpdateAllOptimization = false;

    /**
     * Builder constructor for KMeans
     * @param inputPoints input points for kmeans algorithm
     * @param resultCount number of result points
     * @param dataToMean function that takes a list of points and generates mean point
     * @param dataLength function that gives length between two points
     */
    public KMeansBuilder(List<T> inputPoints, int resultCount, DataToMean<T> dataToMean, DataLength<T> dataLength) {
        if (inputPoints == null || inputPoints.isEmpty()) {
            throw new IllegalArgumentException("inputPoints needs at least 1 data point");
        }
        if (resultCount < 1) {
            throw new IllegalArgumentException("resultCount needs to be at least 1");
        }
        if (dataToMean == null) {
            throw new IllegalArgumentException("dataToMean cannot be null");
        }
        if (dataLength == null) {
            throw new IllegalArgumentException("dataLength cannot be null");
        }
        this.INPUT_POINTS = Collections.unmodifiableList(inputPoints);
        this.RESULT_COUNT = resultCount;
        this.dataToMean = dataToMean;
        this.dataLength = dataLength;
    }

    /**
     * Enables multithreading with given thread count
     * @param count number of threads
     * @return this
     */
    public KMeansBuilder<T> setThreadCount(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Thread count cannot be lower than 1");
        }
        this.threadsMax = count;
        this.multithreaded = true;
        return this;
    }

    /**
     * Enables optimization that does not update points closer to result points.
     * @return this
     */
    public KMeansBuilder<T> setOptimizationSkipUpdatesBasedOnRange() {
        this.dontUpdateAllOptimization = true;
        return this;
    }

    /**
     * Adds OnUpdate function
     * @param onUpdate function that is called whenever iteration is completed
     * @return this
     */
    public KMeansBuilder<T> onUpdate(OnUpdate onUpdate) {
        if (onUpdate == null) {
            throw new IllegalArgumentException("onUpdate cannot be null");
        }
        this.onUpdate = onUpdate;
        return this;
    }

    /**
     * Finishes building arguments and returns new KMeans object
     * @return finished arguments
     */
    public KMeans<T> build() {
        return new KMeans<>(buildArgs());
    }


    private Arguments<T> buildArgs() {
        return new Arguments<>(INPUT_POINTS, RESULT_COUNT, dataToMean, dataLength, multithreaded, threadsMax, dontUpdateAllOptimization, onUpdate);
    }

    static class Arguments<T> {

        private final List<T> INPUT_POINTS;
        private final int RESULT_COUNT;
        private final DataToMean<T> dataToMean;
        private final DataLength<T> dataLength;

        private final boolean MULTITHREADED;
        private final int threadsMax;

        private final boolean dontUpdateAllOptimization;
        private final OnUpdate onUpdate;

        private Arguments(List<T> inputPoints, int resultCount, DataToMean<T> dataToMean, DataLength<T> dataLength, boolean multithreaded, int threadsMax, boolean dontUpdateAllOptimization, OnUpdate onUpdate) {
            this.INPUT_POINTS = inputPoints;
            this.RESULT_COUNT = resultCount;
            this.dataToMean = dataToMean;
            this.dataLength = dataLength;
            this.MULTITHREADED = multithreaded;
            this.threadsMax = threadsMax;
            this.dontUpdateAllOptimization = dontUpdateAllOptimization;
            this.onUpdate = onUpdate;
        }

        List<T> getInputPoints() {
            return INPUT_POINTS;
        }

        int getResultCount() {
            return RESULT_COUNT;
        }

        boolean isMultithreaded() {
            return MULTITHREADED;
        }

        int getThreadsMax() {
            return threadsMax;
        }

        boolean isDontUpdateAllOptimization() {
            return dontUpdateAllOptimization;
        }

        DataToMean<T> getDataToMean() {
            return dataToMean;
        }

        DataLength<T> getDataLength() {
            return dataLength;
        }

        OnUpdate getOnUpdate() {
            return onUpdate;
        }
    }



}
