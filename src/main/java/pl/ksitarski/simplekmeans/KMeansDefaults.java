package pl.ksitarski.simplekmeans;


import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.List;

public class KMeansDefaults {
    public static DataToMean<Double> getDefaultDataToMean() {
        return input -> {
            double result = 0;
            for (var number : input) {
                result += number;
            }
            return result / input.size();
        };
    }

    public static DataToMean<Double> simdDataToMean() {
        return new DataToMean<Double>() {
            @Override
            public Double getMean(List<Double> input) {
                final VectorSpecies<Double> SPECIES = DoubleVector.SPECIES_MAX;
                Double[] arr = new Double[input.size()];
                input.toArray(arr);

                return null;

            }
        };
    }
}
