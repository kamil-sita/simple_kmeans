package pl.ksitarski.simplekmeans;

import java.util.ArrayList;
import java.util.List;

public class KMeansCluster<T extends KMeansData> {

    private List<T> points;
    private T meanPoint;

    KMeansCluster() {
        points = new ArrayList<>();
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

    public List<T> getPoints() {
        return points;
    }


}
