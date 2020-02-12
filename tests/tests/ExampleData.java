package tests;

import java.util.Random;

public class ExampleData {

    private double value1;
    private double value2;

    public ExampleData() {
        setRandomData();
    }

    public ExampleData(double val1, double val2) {
        this.value1 = val1;
        this.value2 = val2;
    }

    public void setRandomData() {
        value1 = getRandomDouble();
        value2 = getRandomDouble();
    }

    public double getValue1() {
        return value1;
    }

    public double getValue2() {
        return value2;
    }

    @Override
    public String toString() {
        return "ExampleData: " + value1 + ", " + value2;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof ExampleData) {
            var o1 = (ExampleData) o;
            return this.value1 == o1.value1 && this.value2 == o1.value2;
        }
        return false;
    }

    private static Random random;

    private static double getRandomDouble() {
        if (random == null) random = new Random(32); //constant seed for consistent testing
        return random.nextDouble() * 100.0 - 50.0;
    }


}
