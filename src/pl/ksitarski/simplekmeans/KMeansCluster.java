package pl.ksitarski.simplekmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KMeansCluster<T extends KMeansData> {

    private List<T> points;
    private T meanPoint;

    KMeansCluster(boolean threaded) {
        if (threaded) {
            points = Collections.synchronizedList(new ArrayList<>());
        } else {
            points = new ArrayList<>();
        }

    }

    void addPoint(T point) {
        points.add(point);
    }

    public T getMean() {
        if (meanPoint != null) return meanPoint;
        if (points.size() == 0) {
            return null;
        }
        meanPoint =  (T) points.get(0).meanOfList((List<KMeansData>) points);
        return meanPoint;
    }

    double getDeviation() {
        double sum = 0;
        for (var point : points) {
            sum += meanPoint.distanceTo(point);
        }
        return sum/getSize();
    }

    public List<T> getPoints() {
        return points;
    }

    int getSize() {
        return points.size();
    }


}
