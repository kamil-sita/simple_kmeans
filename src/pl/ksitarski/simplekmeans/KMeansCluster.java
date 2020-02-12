package pl.ksitarski.simplekmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class KMeansCluster<T> {

    private List<T> points;
    private List<Double> lengths;
    private T meanPoint;
    private DataToMean<T> dataToMean;
    private Semaphore accessSemaphore = new Semaphore(1);

    KMeansCluster(boolean threaded, int initialCapacity, DataToMean<T> dataToMean) {
        if (threaded) {
            points = Collections.synchronizedList(new ArrayList<>(initialCapacity));
            lengths = Collections.synchronizedList(new ArrayList<>(initialCapacity));
        } else {
            points = new ArrayList<>(initialCapacity);
            lengths = new ArrayList<>(initialCapacity);
        }
        this.dataToMean = dataToMean;
    }

    public T getMean() {
        if (meanPoint != null) return meanPoint;
        if (points.size() == 0) {
            return null;
        }
        meanPoint = dataToMean.getMean(points);
        return meanPoint;
    }

    synchronized void addPoint(T point, double length) {
        accessSemaphore.acquireUninterruptibly();
        points.add(point);
        lengths.add(length);
        accessSemaphore.release();
    }

    public double getStandardDeviation() {
        accessSemaphore.acquireUninterruptibly();
        double sum = 0;
        for (var length : lengths) {
            sum += length;
        }
        double stdDev = sum/getSize();
        accessSemaphore.release();
        return stdDev;
    }

    public List<T> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public int getSize() {
        return points.size();
    }


}
