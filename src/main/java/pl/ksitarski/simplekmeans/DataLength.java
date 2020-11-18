package pl.ksitarski.simplekmeans;

@FunctionalInterface
public interface DataLength<T> {
    /**
     * Returns length between two objects. Using Math.abs is faster than Math.sqrt  and is effectively the same.
     * @return length between two objects
     */
    double getLength(T obj1, T obj2);
}
