
# simple_kmeans  
**simple_kmeans** is easy to use Java KMeans library.  
  
## Usage  
Using **simple_kmeans** library is very easy:   
1. Compile source or download latest version from releases tab.  
2. Put it into external libraries in your IDE of choice.  
3. Build KMeans object with KMeansBuilder, iterate over it and get results:
  
```java
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
```  

Results:
```
25.0
4.0
10.0
```

## Advanced usage

Apart from simply calculating mean points you can also possibly accelerate calculations with following builder functions:
* setOptimizationSkipUpdatesBasedOnRange - does not update points that are close to any mean point already. Might need more iterations to reach same standard deviation, but should be faster
* setThreadCount - calculation is done using multiple threads


There is also option to give updates after every successful iteration using onUpdate function

