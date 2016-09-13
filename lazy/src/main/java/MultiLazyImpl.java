import java.util.function.Supplier;

public class MultiLazyImpl<T> implements Lazy<T> {

    private final Supplier<T> supplier;
    private T cached;
    private boolean called;

    public MultiLazyImpl(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public synchronized T get() {
        if (!called) {
            called = true;
            cached = supplier.get();
        }
        return cached;
    }
}
