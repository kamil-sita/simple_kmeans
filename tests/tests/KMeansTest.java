package tests;

import org.junit.jupiter.api.Test;
import pl.ksitarski.simplekmeans.*;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;


class KMeansTest {
    /*
    Passing those tests does not in any case indicate that code is working, but that it might be working, as KMeans is machine learning algorithm.
     */

    private final int COUNT = 10; //must be bigger than 1
    private final int BIG_COUNT = 20000; //should be bigger than 100
    private final int THREAD_COUNT = 4;

    @Test
    void areResultsProbablyCorrect() {
        var kMeans = new KMeansBuilder<>(getBigCorrectSample(), COUNT, getDataToMean(), getDataLength()).build();
        final int ITERATION_COUNT = 1000; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<tests.ExampleData> data = kMeans.getCalculatedMeanPoints();
        assertTrue(atLeastOneNotNullInList(data));
    }


    //checking whether results actually differ between iterations
    @Test
    void learningTest() {
        var kMeans = new KMeansBuilder<>(getCorrectSample(), COUNT, getDataToMean(), getDataLength()).build();
        final int ITERATION_COUNT = 5; //must be at least 1
        kMeans.iterate(ITERATION_COUNT);
        List<ExampleData> dataAfterFirstIterations = kMeans.getCalculatedMeanPoints();
        kMeans.iterate(1);
        List<ExampleData> dataAfterLaterIterations = kMeans.getCalculatedMeanPoints();
        assertTrue(doListsDiffer(dataAfterFirstIterations, dataAfterLaterIterations));
    }

    @Test
    void bigSampleTest() {
        var kMeans = new KMeansBuilder<>(getBigCorrectSample(), COUNT, getDataToMean(), getDataLength()).build();
        kMeans.iterate(5);
        List<ExampleData> afterInitial = kMeans.getCalculatedMeanPoints();
        kMeans.iterate(10);
        List<ExampleData> afterAdditional = kMeans.getCalculatedMeanPoints();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
    }


    //checking methods related to feedback
    @Test
    void onUpdateTest() {
        final int[] updates = {0};
        var kMeans = new KMeansBuilder<>(getCorrectSample(), COUNT, getDataToMean(), getDataLength())
                .onUpdate(percentComplete -> {
                    updates[0]++;
                    System.out.println(percentComplete);
                })
                .build();
        final int ITERATION_COUNT = 10;
        kMeans.iterate(ITERATION_COUNT);
        assertTrue(updates[0] >= 0);
    }

    @Test
    void optimizationSkipUpdatesBasedOnRangeTest() {
        var kMeans = new KMeansBuilder<>(getBigCorrectSample(), COUNT, getDataToMean(), getDataLength()).setOptimizationSkipUpdatesBasedOnRange().build();
        kMeans.iterate(5);
        List<ExampleData> afterInitial = kMeans.getCalculatedMeanPoints();
        double stdDevBefore = kMeans.getStandardDeviation();
        kMeans.iterate(10);
        List<ExampleData> afterAdditional = kMeans.getCalculatedMeanPoints();
        double stdDevAfter = kMeans.getStandardDeviation();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
        assertTrue(stdDevBefore > stdDevAfter);
    }

    @Test
    void multithreadedTest() {
        var kMeans = new KMeansBuilder<>(getBigCorrectSample(), COUNT, getDataToMean(), getDataLength()).setThreadCount(THREAD_COUNT).build();
        kMeans.iterate(5);
        List<ExampleData> afterInitial = kMeans.getCalculatedMeanPoints();
        double stdDevBefore = kMeans.getStandardDeviation();
        kMeans.iterate(10);
        List<ExampleData> afterAdditional = kMeans.getCalculatedMeanPoints();
        double stdDevAfter = kMeans.getStandardDeviation();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
        assertTrue(stdDevBefore > stdDevAfter);
    }

    @Test
    void multithreadedOptimizationSkipUpdatesBasedOnRangeTest() {
        var kMeans = new KMeansBuilder<>(getBigCorrectSample(), COUNT, getDataToMean(), getDataLength())
                .setThreadCount(THREAD_COUNT)
                .setOptimizationSkipUpdatesBasedOnRange()
                .build();
        kMeans.iterate(5);
        List<ExampleData> afterInitial = kMeans.getCalculatedMeanPoints();
        double stdDevBefore = kMeans.getStandardDeviation();
        kMeans.iterate(10);
        List<ExampleData> afterAdditional = kMeans.getCalculatedMeanPoints();
        double stdDevAfter = kMeans.getStandardDeviation();
        assertTrue(doListsDiffer(afterInitial, afterAdditional));
        assertTrue(stdDevBefore > stdDevAfter);
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
        var kMeans = new KMeansBuilder<>(arrayList, arrayList.size(), getDataToMean(), getDataLength()).build();
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
    void iterateUntilTest() {
        ArrayList<ExampleData> arrayList = new ArrayList<>();
        arrayList.add(new ExampleData(2, 3));
        arrayList.add(new ExampleData(1, -1));
        arrayList.add(new ExampleData(4, 5));
        arrayList.add(new ExampleData(-2, 0));
        arrayList.add(new ExampleData(54, -65));
        arrayList.add(new ExampleData(33, 54));
        var kMeans = new KMeansBuilder<>(arrayList, arrayList.size(), getDataToMean(), getDataLength()).build();
        kMeans.iterateUntilStandardDeviationDeltaSmallerOrEqualTo(0.01);
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

    private boolean atLeastOneNotNullInList(List<ExampleData> list) {
        return notNullCount(list) > 0;
    }

    private List<ExampleData> getCorrectSample() {
        List<ExampleData> data = new ArrayList<>();
        for (int i = 0; i < COUNT; i++) {
            data.add(new ExampleData());
        }
        return data;
    }

    private List<ExampleData> getBigCorrectSample() {
        List<ExampleData> data = new ArrayList<>();
        for (int i = 0; i < BIG_COUNT; i++) {
            data.add(new ExampleData());
        }
        return data;
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

    private static DataLength<ExampleData> getDataLength() {
        return (obj1, obj2) -> Math.abs(square(obj1.getValue1() - obj2.getValue1()) + square(obj1.getValue2() - obj2.getValue2()));
    }

    private static DataToMean<ExampleData> getDataToMean() {
        return input -> {
            double sumValue1 = 0;
            double sumValue2 = 0;

            for (var point : input) {
                sumValue1 += point.getValue1();
                sumValue2 += point.getValue2();
            }

            return new ExampleData(sumValue1/input.size(), sumValue2/input.size());
        };
    }

    private static double square(double v) {
        return v * v;
    }

}