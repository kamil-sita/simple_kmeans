package tests;

import pl.kamilsitarski.simplekmeans.SimpleKMeansData;

import java.util.List;

public class KMeansData implements SimpleKMeansData {

    private double value;

    @Override
    public SimpleKMeansData getNewWithRandomData() {
        return new KMeansData();
    }

    @Override
    public double distanceTo(SimpleKMeansData simpleKMeansData) {
        return 0;
    }

    @Override
    public SimpleKMeansData meanOfList(List<SimpleKMeansData> data) {
        KMeansData output = new KMeansData();
        double value = 0;
        for (SimpleKMeansData datum : data) {
            value += ((KMeansData) datum).getValue();
        }
        value /= data.size();
        output.setData(value);
        return output;
    }

    public KMeansData() {
        setRandomData();
    }

    public void setRandomData() {
        value = Math.random();
    }

    public void setData(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
