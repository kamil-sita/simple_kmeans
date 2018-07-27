package pl.kamilsitarski.simplekmeans;


import java.util.ArrayList;
import java.util.List;

public class KMeans<T extends SimpleKMeansData> {

    private int kMeansPointCount;

    private List<T> kMeansPoints;
    private List<T> allDataPoints;
    private T tInstanceCreator;
    private boolean wasInitialized = false;
    private boolean wasIterated = false;

    public KMeans(int resultsCount, List<T> allDataPoints) {
        this.kMeansPointCount = resultsCount;
        this.allDataPoints = allDataPoints;
        try {
            checkIsDataCorrect();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        initialize();
    }

    public void iterate() {
        if (!wasInitialized) {
            log("Was not initialized!");
            return;
        }
        List<T>[] pointsClosestToKMeansPoints = new ArrayList[kMeansPointCount];
        for (int i = 0; i < kMeansPointCount; i++) {
            pointsClosestToKMeansPoints[i] = new ArrayList<>();
        }

        for (int i = 0; i < allDataPoints.size(); i++) {
            T closestKMeanPoint = getClosestKMeanPointTo(allDataPoints.get(i));
            int idOfKMeanPoint = getIdOfKMeanPoint(closestKMeanPoint);
            pointsClosestToKMeansPoints[idOfKMeanPoint].add(allDataPoints.get(i));
        }

        kMeansPoints = new ArrayList<>();

        for (int i = 0; i < kMeansPointCount; i++) {
            T point = (T) getTInstance().meanOfList(
                    (List<SimpleKMeansData>) pointsClosestToKMeansPoints[i]
            );
            if (point == null) {
                point = getTInstance();
            }
            kMeansPoints.add(point);
        }
        wasIterated = true;
    }

    public List<T> getResults() {
        if (!wasIterated) {
            log("Cannot retrieve results before iterate!");
            return null;
        }
        return kMeansPoints;
    }

    private void initialize() {
        tInstanceCreator = allDataPoints.get(0);
        tInstanceCreator = getTInstance();

        kMeansPoints = new ArrayList<>();
        for (int i = 0; i < kMeansPointCount; i++) {
            kMeansPoints.add(getTInstance());
        }
        wasInitialized = true;
    }

    private int getIdOfKMeanPoint(T point) {
        for (int i = 0; i < kMeansPoints.size(); i++) {
            if (kMeansPoints.get(i).equals(point)) {
                return i;
            }
        }
        return -1;
    }

    private T getClosestKMeanPointTo(T point) {
        T closest = null;
        double distanceToClosest = 0;
        for (T kMeanPoint : kMeansPoints) {
            double distance = kMeanPoint.distanceTo(point);
            if (closest == null || kMeanPoint.distanceTo(point) < distanceToClosest) {
                closest = kMeanPoint;
                distanceToClosest = distance;
            }
        }
        return closest;
    }

    private T getTInstance() {
        return (T) tInstanceCreator.getNewWithRandomData();
    }

    private void checkIsDataCorrect() throws Exception {
        if (kMeansPointCount <= 0) {
            throw new Exception("resultsCount cannot be lower or equal to zero");
        }
        if (allDataPoints == null) {
            throw new Exception("Data points list cannot be null");
        }
        if (allDataPoints.size() == 0) {
            throw new Exception("Data points list cannot be empty");
        }
        for (T point : allDataPoints) {
            if (point == null) {
                throw new Exception("Data point cannot be null");
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
