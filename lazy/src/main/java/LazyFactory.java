import java.util.function.Supplier;

public class LazyFactory<T> {

    public static <T> Lazy<T> singleThreaded(Supplier<T> supplier) { return new SingleLazyImpl<>(supplier); }

    public static <T> Lazy<T> multiThreaded(Supplier<T> supplier) {
        return new MultiLazyImpl<>(supplier);
    }

    public static <T> Lazy<T> lockFree(Supplier<T> supplier) {
        return new LockFreeLazyImpl<>(supplier);
    }

}
