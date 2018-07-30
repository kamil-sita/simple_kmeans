package tests;

import org.junit.jupiter.api.Test;
import pl.kamilsitarski.simplekmeans.KMeans;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KMeansTest {

    /*
    Passing those tests does not in any case indicate that code is working, but that it might be working, as KMeans is machine learning algorithm.
     */

    private final int COUNT = 10; //must be bigger than 1

    ///checking whether initialization with good values works and with bad values doesn't
    @Test
    void initializationTest() {
        KMeans<KMeansData> kMeans;
        kMeans = getCorrectSample();
        assertTrue(kMeans.isInitialized());
        kMeans = getBadSample();
        assertFalse(kMeans.isInitialized());
    }

    @Test
    void areResultsProbablyCorrect() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        for (int i = 0; i < ITERATION_COUNT; i++) {
            kMeans.iterate();
        }

        List<KMeansData> data = kMeans.getResults();
        assertTrue(atLeastOneNotNull(data));
    }

    //checking whether results actually differ between iterations
    @Test
    void learningTest() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10; //must be at least 1
        for (int i = 0; i < ITERATION_COUNT; i++) {
            kMeans.iterate();
        }
        List<KMeansData> dataAfterFirstIterations = kMeans.getResults();
        kMeans.iterate();
        List<KMeansData> dataAfterLaterIterations = kMeans.getResults();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    private boolean atLeastOneNotNull(List<KMeansData> list) {
        return notNullCount(list) > 0;
    }

    private KMeans<KMeansData> getCorrectSample() {
        List<KMeansData> data = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            data.add(new KMeansData());
        }
        return new KMeans<>(COUNT, data);
    }

    private KMeans<KMeansData> getBadSample() {
        return new KMeans<>(0, null);
    }

    private static int notNullCount(List list) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != null) count++;
        }
        return count;
    }

    private static boolean doListsDiffer(List list1, List list2) {
        if (list1.size() != list2.size()) return true;
        for (int i = 0; i < list1.size(); i++) {
            if (!list1.get(i).equals(list2.get(i))) return true;
        }
        return false;
    }

}