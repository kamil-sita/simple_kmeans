# simple_kmeans
**simple_kmeans** is easy to use Java KMeans library.

### usage
Using **simple_kmeans** library is very easy (huh): 
1. Compile source or download latest version from releases tab.
2. Put it into external libraries in your IDE of choice.
3. Create data container that implements SimpleKMeansData interface. Example of implementation (from my other project):

```
public class RgbKmeansContainer implements SimpleKMeansData {

    //RGB is a simple class with (r, g, b) fields
    private RGB rgbValue;

    public RgbKmeansContainer(RGB rgb) {
        this.rgbValue = rgb;
    }

    @Override
    //calculates distance to other object of this same type
    public double distanceTo(SimpleKMeansData KMeansData) {
        return rgbValue.getDistanceFrom(((RgbKmeansContainer) KMeansData).rgbValue);
    }

    @Override
    //return new object that is mean of the list
    public SimpleKMeansData meanOfList(List<SimpleKMeansData> list) {
        if (list == null || list.size() == 0) return null;
        RGB rgb = new RGB(0, 0 ,0);
        for (SimpleKMeansData KMeansData : list) {
            RGB rgbElement = ((RgbKmeansContainer) KMeansData).rgbValue;
            rgb.r += rgbElement.r;
            rgb.g += rgbElement.g;
            rgb.b += rgbElement.b;
        }
        rgb.r /= list.size();
        rgb.g /= list.size();
        rgb.b /= list.size();
        return new RgbKmeansContainer(rgb);
    }
}
```
4. Now all that is left to do is to create and use KMeans<T implements SimpleKMeansData> object, for example:

```
KMeans<RgbKmeansContainer> kMeans = new KMeans(32, rgbList);
kMeans.iterate(1000);
List<RgbKmeansContainer> results = kMeans.getResults();
```

5. If you need to update user with information or get constant progress updates your can use setOnUpdate() function:

```
kMeans.setOnUpdate(() ->
    GiveFeedbackToUser.updateProgress(kMeans.getProgress()));
```

GiveFeedbackToUser.updateProgress(double) will be called on every iteration of kMeans.
