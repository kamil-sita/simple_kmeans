package tests;

import pl.kamilsitarski.simplekmeans.SimpleKMeansData;

import java.util.List;
import java.util.Random;

public class KMeansData implements SimpleKMeansData {

    private double value1;
    private double value2;

    @Override
    public SimpleKMeansData getNewWithRandomData() {
        KMeansData kMeansData = new KMeansData();
        SimpleKMeansData simpleKMeansData = kMeansData;
        return simpleKMeansData;
    }

    @Override
    public double distanceTo(SimpleKMeansData simpleKMeansData) {
        KMeansData kMeansData = (KMeansData) simpleKMeansData;
        return sqrt(square(value1 - kMeansData.getValue1()) + square(value2 - kMeansData.getValue2()));
    }

    private double square(double value) {
        return value * value;
    }

    private double sqrt(double value) {
        return Math.sqrt(value);
    }

    @Override
    public SimpleKMeansData meanOfList(List<SimpleKMeansData> data) {
        KMeansData output = new KMeansData();
        double value = 0;
        double value2 = 0;
        for (SimpleKMeansData datum : data) {
            value += ((KMeansData) datum).getValue1();
            value2 += ((KMeansData) datum).getValue2();
        }
        value /= data.size();
        value2 /= data.size();
        output.setData(value, value2);
        return output;
    }

    public KMeansData() {
        setRandomData();
    }

    public void setRandomData() {
        value1 = getRandomDouble();
        value2 = getRandomDouble();
    }

    public void setData(double value, double value2) {
        this.value1 = value;
        this.value2 = value2;
    }

    public double getValue1() {
        return value1;
    }

    public double getValue2() {
        return value1;
    }


    private static Random random;
    private static double getRandomDouble() {
        if (random == null) random = new Random(32); //constant seed for consistent testing
        return random.nextDouble() * 10.0;
    }


}
