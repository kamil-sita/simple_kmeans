package tests;


import org.junit.jupiter.api.Test;
import pl.kamilsitarski.simplekmeans.KMeans;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class KMeansTest {

    @Test
    void initializationTest() {
        KMeans<KMeansData> kMeans = getSampleKMeans();
        assertTrue(kMeans.isInitialized());
    }

    @Test
    void getResults() {
        KMeans<KMeansData> kMeans = getSampleKMeans();
        for (int i = 0; i < 10; i++) {
            kMeans.iterate();
        }
        List<KMeansData> data = kMeans.getResults();
        assertTrue(isDataCorrect(data));
    }

    private KMeans<KMeansData> getSampleKMeans() {
        List<KMeansData> data = new ArrayList<>();
        KMeansData kMeansData1 = new KMeansData();
        kMeansData1.setData(1.0);
        KMeansData kMeansData2 = new KMeansData();
        kMeansData2.setData(3.0);
        KMeansData kMeansData3 = new KMeansData();
        kMeansData3.setData(5.0);
        data.add(kMeansData1);
        data.add(kMeansData2);
        data.add(kMeansData3);
        return new KMeans<>(1, data);
    }

    private boolean isDataCorrect(List<KMeansData> data) {
        return data.get(0).getValue() == 3.0;
    }
}