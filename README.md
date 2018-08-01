#simple_kmeans
**simple_kmeans** is easy to use Java KMeans library.

###usage
Using **simple_kmeans** library is very easy: 
1. Compile source or download latest version from releases tab.
2. Put it into external libraries in your IDE of choice.
3. You need to create data container that implements SimpleKMeansData interface. Example of implementation (from my other project):

```
public class RgbKmeansContainer implements SimpleKMeansData {

    //RGB is a simple class with (r, g, b) fields
    private RGB rgbValue;

    public RgbKmeansContainer(RGB rgb) {
        this.rgbValue = rgb;
    }

    @Override
    //returns new random rgb
    public SimpleKMeansData getNewWithRandomData() {
        return new RgbKmeansContainer(new RGB(
                (int) (Math.random() * 255),
                (int) (Math.random() * 255),
                (int) (Math.random() * 255)
        ));
    }

    @Override
    //calculates distance to other object of this same type
    public double distanceTo(SimpleKMeansData simpleKMeansData) {
        return rgbValue.getDistanceFrom(((RgbKmeansContainer) simpleKMeansData).rgbValue);
    }

    @Override
    //return new object that is mean of the list
    public SimpleKMeansData meanOfList(List<SimpleKMeansData> list) {
        if (list == null || list.size() == 0) return null;
        RGB rgb = new RGB(0, 0 ,0);
        for (SimpleKMeansData simpleKMeansData : list) {
            RGB rgbElement = ((RgbKmeansContainer) simpleKMeansData).rgbValue;
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