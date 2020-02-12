import pl.ksitarski.simplekmeans.DataLength;
import pl.ksitarski.simplekmeans.DataToMean;
import pl.ksitarski.simplekmeans.KMeansBuilder;

import java.util.List;

public class example {
    public static void main(String[] args) {
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
        var results = kmeans.getCalculatedMeanPoints();

        for (var result : results) {
            System.out.println(result);
        }
    }
}
