package tests;

import org.junit.jupiter.api.Test;
import pl.ksitarski.simplekmeans.KMeans;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class KMeansTest {
    /*
    Passing those tests does not in any case indicate that code is working, but that it might be working, as KMeans is machine learning algorithm.
     */

    private final int COUNT = 10; //must be bigger than 1
    private final int BIG_COUNT = 20000; //should be bigger than 100
    private final int THREAD_COUNT = 4;

    ///checking whether initialization with good values works and with bad values doesn't
    @Test
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
        List<tests.ExampleData> data = kMeans.getCalculatedMeanPoints();
        assertTrue(atLeastOneNotNullInList(data));
    }


    //checking whether results actually differ between iterations
    @Test
    void learningTest() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 5; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<ExampleData> dataAfterFirstIterations = kMeans.getCalculatedMeanPoints();
        kMeans.iterate(1);
        List<ExampleData> dataAfterLaterIterations = kMeans.getCalculatedMeanPoints();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }


    @Test
    void bigSampleTest() {
        KMeans<ExampleData> kMeans = getBigCorrectSample();
        kMeans.iterate(5);
        List<ExampleData> afterInitial = kMeans.getCalculatedMeanPoints();
        kMeans.iterate(10);
        List<ExampleData> afterAdditional = kMeans.getCalculatedMeanPoints();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }


    //checking methods related to feedback
    @Test
    void onUpdateTest() {
        KMeans<ExampleData> kMeans = getCorrectSample();
        final int ITERATION_COUNT = 10;
        final int[] updates = {0};
        kMeans.setOnUpdate(() -> {
            updates[0]++;
            System.out.println(kMeans.getProgress());
        });
        kMeans.iterate(ITERATION_COUNT);
        assertTrue(updates[0] >= 0);
    }

    //test that checks correctness on  small predefined sample
    @Test
    void simplePredefinedSampleTest() {
        ArrayList<ExampleData> arrayList = new ArrayList<>();
        arrayList.add(new ExampleData(2, 3));
        arrayList.add(new ExampleData(1, -1));
        arrayList.add(new ExampleData(4, 5));
        arrayList.add(new ExampleData(-2, 0));
        arrayList.add(new ExampleData(54, -65));
        arrayList.add(new ExampleData(33, 54));
        KMeans<ExampleData> kMeans = new KMeans<>(arrayList.size(), arrayList);
        kMeans.iterate(50);
        var results = kMeans.getCalculatedMeanPoints();


        System.out.println("Input:");
        for (var input : arrayList) {
            System.out.println(input);
        }

        System.out.println("Results:");
        for (var result : results) {
            System.out.println(result);
        }


        for (var input : arrayList) {
            assertTrue(results.contains(input));
        }

    }


    @Test
    void simplePredefinedSampleTestThreads() {
        ArrayList<ExampleData> arrayList = new ArrayList<>();
        arrayList.add(new ExampleData(2, 3));
        arrayList.add(new ExampleData(1, -1));
        arrayList.add(new ExampleData(4, 5));
        arrayList.add(new ExampleData(-2, 0));
        arrayList.add(new ExampleData(54, -65));
        arrayList.add(new ExampleData(33, 54));
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        KMeans<ExampleData> kMeans = new KMeans<>(arrayList.size(), arrayList, executorService);
        kMeans.iterate(50);
        var results = kMeans.getCalculatedMeanPoints();


        System.out.println("Input:");
        for (var input : arrayList) {
            System.out.println(input);
        }

        System.out.println("Results:");
        for (var result : results) {
            System.out.println(result);
        }


        for (var input : arrayList) {
            assertTrue(results.contains(input));
        }
        executorService.shutdown();

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