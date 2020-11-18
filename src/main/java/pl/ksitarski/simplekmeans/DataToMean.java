package pl.ksitarski.simplekmeans;

import java.util.List;

@FunctionalInterface
public interface DataToMean<T> {
    /**
     * Returns point generated as a mean of given list.
     * @return mean point of the list
     */
    T getMean(List<T> input);
}
