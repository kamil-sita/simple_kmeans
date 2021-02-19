package tests;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;
import org.junit.jupiter.api.Test;
import pl.ksitarski.simplekmeans.DataLength;
import pl.ksitarski.simplekmeans.KMeansBuilder;
import pl.ksitarski.simplekmeans.KMeansDefaults;

import java.util.List;

public class ReadmeTests {
    @Test
    void defaultArguments() {
        var kmeans = new KMeansBuilder<>(
                List.of(2.0, 4.0, 6.0, 9.0, 10.0, 11.0, 24.0, 25.0, 26.0), //input points
                3, //result count
                input -> { //function that generates mean from given list of points
                    double result = 0;
                    for (var number : input) {
                        result += number;
                    }
                    return result / input.size();
                },
                (obj1, obj2) -> Math.abs(obj1 - obj2) //function that returns distance between two points
        ).build();

        kmeans.iterate(20); //calculating

        //printing out results
        List<Double> results = kmeans.getCalculatedMeanPoints();
        for (var result : results) {
            System.out.println(result);
        }
    }

    @Test
    void defaultArguments2() {
        var kmeans = new KMeansBuilder<>(
                List.of(2.0, 4.0, 6.0, 9.0, 10.0, 11.0, 24.0, 25.0, 26.0), //input points
                3, //result count
                KMeansDefaults.getDefaultDataToMean(),
                (obj1, obj2) -> Math.abs(obj1 - obj2) //function that returns distance between two points
        ).build();

        kmeans.iterate(20); //calculating

        //printing out results
        List<Double> results = kmeans.getCalculatedMeanPoints();
        for (var result : results) {
            System.out.println(result);
        }
    }

    @Test
    void defaultArguments3() {
        var kmeans = new KMeansBuilder<>(
                List.of(2.0, 4.0, 6.0, 9.0, 10.0, 11.0, 24.0, 25.0, 26.0), //input points
                3, //result count
                input -> { //function that generates mean from given list of points
                    double result = 0;
                    for (var number : input) {
                        result += number;
                    }
                    return result / input.size();
                },
                new DataLength<Double>() {
                    @Override
                    public double getLength(Double obj1, Double obj2) {
                        final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_MAX;
                        System.out.println(SPECIES.elementSize());
                        System.out.println(SPECIES.vectorBitSize());
                        System.out.println(SPECIES.vectorByteSize());

                        var m = SPECIES.indexInRange(0, 1);
                        var va = DoubleVector.fromArray(SPECIES, new double[]{obj1}, 0, m);
                        var vb = DoubleVector.fromArray(SPECIES, new double[]{obj2}, 0, m);
                        var vc = va.sub(vb).abs();

                        double[] out = new double[1];
                        vc.intoArray(out, 0, m);

                        return out[0];
                    }
                } //function that returns distance between two points
        ).build();

        kmeans.iterate(20); //calculating

        //printing out results
        List<Double> results = kmeans.getCalculatedMeanPoints();
        for (var result : results) {
            System.out.println(result);
        }
    }
}
