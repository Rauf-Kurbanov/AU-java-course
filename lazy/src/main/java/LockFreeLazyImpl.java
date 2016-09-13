import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Supplier;

public class LockFreeLazyImpl<T> implements Lazy<T> {
    private static AtomicReferenceFieldUpdater<LockFreeLazyImpl, Object> valueUpdater =
            AtomicReferenceFieldUpdater.newUpdater(LockFreeLazyImpl.class, Object.class, "cached");

    private volatile T cached;
    private Supplier<T> supplier;

    public LockFreeLazyImpl(Supplier<T> supp) {
        supplier = supp;
    }

    @Override
    public T get() {
        Supplier<T> tmpSupplier = supplier;
        while (supplier != null) {
            if (valueUpdater.compareAndSet(this, null, tmpSupplier.get())) {
                supplier = null;
            }
        }
        return cached;
    }
}