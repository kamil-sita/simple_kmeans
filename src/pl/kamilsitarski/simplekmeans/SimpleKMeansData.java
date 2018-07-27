package pl.kamilsitarski.simplekmeans;

import java.util.List;

public interface SimpleKMeansData {
    SimpleKMeansData getNewWithRandomData();
    double distanceTo(SimpleKMeansData simpleKMeansData);
    SimpleKMeansData meanOfList(List<SimpleKMeansData> data);
}
