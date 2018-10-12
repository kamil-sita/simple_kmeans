package pl.ksitarski.simplekmeans;

import java.util.ArrayList;
import java.util.List;

public class KMeansCluster<T extends KMeansData> {

    private List<T> points;
    private T meanPoint;

    public KMeansCluster(T meanPoint) {
        this.meanPoint = meanPoint;
        points = new ArrayList<>();
    }

    public void addPoint(T point) {
        points.add(point);
    }

    public T getMean() {
        if (points.size() == 0) {
            return null;
        }
        return (T) points.get(0).meanOfList((List<KMeansData>) points);
    }



}
