package pl.ksitarski.simplekmeans;

import java.util.List;

public interface KMeansData {
    KMeansData getNewWithRandomData();
    double distanceTo(KMeansData KMeansData);
    KMeansData meanOfList(List<KMeansData> data);
}
