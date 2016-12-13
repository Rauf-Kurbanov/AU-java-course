import java.util.function.Supplier;

public class SingleLazyImpl<T> implements Lazy<T> {

    private final Supplier<T> supplier;
    private T cached;
    private boolean called;

    public SingleLazyImpl(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (!called) {
            cached = supplier.get();
            called = true;
        }
        return cached;
    }
}
