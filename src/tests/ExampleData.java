package tests;

import pl.ksitarski.simplekmeans.KMeansData;

import java.util.List;
import java.util.Random;

public class ExampleData implements KMeansData {

    private double value1;
    private double value2;

    @Override
    public KMeansData getNewWithRandomData() {
        ExampleData exampleData = new ExampleData();
        KMeansData KMeansData = exampleData;
        return KMeansData;
    }

    @Override
    public double distanceTo(KMeansData KMeansData) {
        ExampleData exampleData = (ExampleData) KMeansData;
        return sqrt(square(value1 - exampleData.getValue1()) + square(value2 - exampleData.getValue2()));
    }

    private double square(double value) {
        return value * value;
    }

    private double sqrt(double value) {
        return Math.sqrt(value);
    }

    @Override
    public KMeansData meanOfList(List<KMeansData> data) {
        ExampleData output = new ExampleData();
        double value = 0;
        double value2 = 0;
        for (KMeansData datum : data) {
            value += ((ExampleData) datum).getValue1();
            value2 += ((ExampleData) datum).getValue2();
        }
        value /= data.size();
        value2 /= data.size();
        output.setData(value, value2);
        return output;
    }

    public ExampleData() {
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
