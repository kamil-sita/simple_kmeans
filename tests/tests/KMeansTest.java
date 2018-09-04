package tests;

import org.junit.jupiter.api.Test;
import pl.ksitarski.simplekmeans.KMeans;

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
    @org.junit.jupiter.api.Test
    void initializationTest() {
        KMeans<tests.ExampleData> kMeans;
        kMeans = getCorrectSample();
        assertTrue(kMeans.isInitialized());
        kMeans = getBadSample();
        assertFalse(kMeans.isInitialized());
    }

    @Test
    void areResultsProbablyCorrect() {
        KMeans<tests.ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<tests.ExampleData> data = kMeans.getResults();
        assertTrue(atLeastOneNotNullInList(data));
    }

    @Test
    void areResultsProbablyCorrectThreaded() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<ExampleData> data = kMeans.getResults();
        assertTrue(atLeastOneNotNullInList(data));
    }

    //checking whether results actually differ between iterations
    @Test
    void learningTest() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<ExampleData> dataAfterFirstIterations = kMeans.getResults();
        kMeans.iterate(1);
        List<ExampleData> dataAfterLaterIterations = kMeans.getResults();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    @Test
    void learningTestThreaded() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<ExampleData> dataAfterFirstIterations = kMeans.getResults();
        kMeans.iterateWithThreads(1, THREAD_COUNT);
        List<ExampleData> dataAfterLaterIterations = kMeans.getResults();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    @Test
    void learningTestMixed() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 5; //must be at least 1
        final int THREAD_COUNT = 30; //must be at least 1
        final int MIX_ITERATIONS = 4;
        for (int i = 0; i < MIX_ITERATIONS; i++) {
            kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
            List<ExampleData> dataAfterFirstIterations = kMeans.getResults();
            kMeans.iterate(1);
            List<ExampleData> dataAfterLaterIterations = kMeans.getResults();
            assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
        }
        for (int i = 0; i < MIX_ITERATIONS; i++) {
            kMeans.iterate(ITERATION_COUNT);
            List<ExampleData> dataAfterFirstIterations = kMeans.getResults();
            kMeans.iterateWithThreads(1, THREAD_COUNT);
            List<ExampleData> dataAfterLaterIterations = kMeans.getResults();
            assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
        }
    }

    @Test
    void longTest() {
        KMeans<ExampleData> kMeans = getBigCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<ExampleData> afterInitial = kMeans.getResults();
        kMeans.iterate(1);
        List<ExampleData> afterAdditional = kMeans.getResults();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }

    @Test
    void longTestWithThreads() {
        KMeans<ExampleData> kMeans = getBigCorrectSample();
        final int ITERATION_COUNT = 1000; //must be at least 1
        final int THREAD_COUNT = 8; //must be at least 1
        kMeans.iterateWithThreads(ITERATION_COUNT, THREAD_COUNT);
        List<ExampleData> afterInitial = kMeans.getResults();
        kMeans.iterateWithThreads(1, THREAD_COUNT);
        List<ExampleData> afterAdditional = kMeans.getResults();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }

    @Test
    void onUpdateTest() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10;
        final int[] updates = {0};
        kMeans.setOnUpdate(() -> {
            updates[0]++;
            System.out.println(kMeans.getProgress());
        }, false);
        kMeans.iterate(ITERATION_COUNT);
        assertTrue(updates[0] >= 0);

    }


    private boolean atLeastOneNotNullInList(List<ExampleData> list) {
        return notNullCount(list) > 0;
    }

    private KMeans<ExampleData> getCorrectSample() {
        List<ExampleData> data = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            data.add(new ExampleData());
        }
        return new KMeans<>(COUNT, data);
    }

    private KMeans<ExampleData> getBigCorrectSample() {
        List<ExampleData> data = new ArrayList<>();
        for (int i = 0; i < BIG_COUNT; i++) {
            data.add(new ExampleData());
        }
        return new KMeans<>(BIG_COUNT, data);
    }

    private KMeans<ExampleData> getBadSample() {
        return new KMeans<>(0, new ArrayList<>());
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