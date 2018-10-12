package pl.ksitarski.simplekmeans;

import java.util.List;

public interface KMeansData {
    /**
     * This method should return new instance of KMeansData initialized with random values. Will be used to generate
     * @return new instance of KMeansData initialized with random values
     */
    KMeansData getNewWithRandomData();

    /**
     * Should return distance between this and kMeansData object, following rules of metric space (preferably euclidan space: sqrt(a^2+b^2+...z^2))
     * @param kMeansData other kMeansData object
     * @return distance
     */
    double distanceTo(KMeansData kMeansData);

    /**
     * Should return new object based on data objects. Preferably mean of them (x = (x1 + x2 + x3 + ... + xn) / n, ... z = (z1 + z2 + z3 + ... + zn) / n)
     * @param data list of objects for creation of new one to be based on
     * @return new object bad on data objects
     */
    KMeansData meanOfList(List<KMeansData> data);
}
