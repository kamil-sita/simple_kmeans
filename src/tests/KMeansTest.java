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
    private final int BIG_COUNT = 2000; //should be bigger than 100

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
        kMeans.iterate(ITERATION_COUNT);
        List<KMeansData> data = kMeans.getResults();
        assertTrue(atLeastOneNotNullInList(data));
    }

    @Test
    void areResultsProbablyCorrectThreaded() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<KMeansData> data = kMeans.getResults();
        assertTrue(atLeastOneNotNullInList(data));
    }

    //checking whether results actually differ between iterations
    @Test
    void learningTest() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<KMeansData> dataAfterFirstIterations = kMeans.getResults();
        kMeans.iterate(1);
        List<KMeansData> dataAfterLaterIterations = kMeans.getResults();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    @Test
    void learningTestThreaded() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<KMeansData> dataAfterFirstIterations = kMeans.getResults();
        kMeans.iterateWithThreads(1, THREAD_COUNT);
        List<KMeansData> dataAfterLaterIterations = kMeans.getResults();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    @Test
    void learningTestMixed() {
        KMeans<KMeansData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 5; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        final int MIX_ITERATIONS = 4;
        for (int i = 0; i < MIX_ITERATIONS; i++) {
            kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
            List<KMeansData> dataAfterFirstIterations = kMeans.getResults();
            kMeans.iterate(1);
            List<KMeansData> dataAfterLaterIterations = kMeans.getResults();
            assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
        }
        for (int i = 0; i < MIX_ITERATIONS; i++) {
            kMeans.iterate(ITERATION_COUNT);
            List<KMeansData> dataAfterFirstIterations = kMeans.getResults();
            kMeans.iterateWithThreads(1, THREAD_COUNT);
            List<KMeansData> dataAfterLaterIterations = kMeans.getResults();
            assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
        }
    }

    @Test
    void longTest() {
        KMeans<KMeansData> kMeans = getBigCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<KMeansData> afterInitial = kMeans.getResults();
        kMeans.iterate(1);
        List<KMeansData> afterAdditional = kMeans.getResults();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }

    @Test
    void longTestWithThreads() {
        KMeans<KMeansData> kMeans = getBigCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        final int THREAD_COUNT = 8; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<KMeansData> afterInitial = kMeans.getResults();
        kMeans.iterateWithThreads(1, THREAD_COUNT);
        List<KMeansData> afterAdditional = kMeans.getResults();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }


    private boolean atLeastOneNotNullInList(List<KMeansData> list) {
        return notNullCount(list) > 0;
    }

    private KMeans<KMeansData> getCorrectSample() {
        List<KMeansData> data = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            data.add(new KMeansData());
        }
        return new KMeans<>(COUNT, data);
    }

    private KMeans<KMeansData> getBigCorrectSample() {
        List<KMeansData> data = new ArrayList<>();
        for (int i = 0; i < BIG_COUNT; i++) {
            data.add(new KMeansData());
        }
        return new KMeans<>(BIG_COUNT, data);
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