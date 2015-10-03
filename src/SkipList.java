import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Моклев Вячеслав
 */
public class SkipList<T> extends AbstractSet<T> implements NavigableSet<T> {
    private final int maxLevel;
    private final Entry<T> start;
    private int size;
    private final Comparator<? super T> comparator;
    private final Comparator<? super T> comparableLambda;

    @SuppressWarnings("unchecked")
    protected SkipList(int maxLevel, Comparator<? super T> comparator) {
        this.maxLevel = maxLevel;
        this.comparator = comparator;
        comparableLambda = (T a, T b) -> ((Comparable<? super T>) a).compareTo(b);
        start = new Entry<>(null, maxLevel);
        size = 0;
    }

    protected SkipList(Comparator<? super T> comparator) {
        this(16, comparator);
    }

    protected SkipList() {
        this(null);
    }

    @Override
    public int size() {
        return size;
    }

    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return comparator;
    }

    private Comparator<? super T> getComparator() {
        Comparator<? super T> rawComparator = comparator == null ? comparableLambda : comparator;
        return (T a, T b) -> {
            if (a == null) {
                return -1;
            }
            if (b == null) {
                return 1;
            }
            return rawComparator.compare(a, b);
        };
    }

    @Override
    public boolean add(@NotNull T t) {
        Comparator<? super T> comp = getComparator();
        Entry<T>[] insertPlace = Entry.newArray(maxLevel);
        Entry<T> current = start;
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && comp.compare(current.next[i].value, t) <= 0) {
                current = current.next[i];
            }
            insertPlace[i] = current;
        }
        if (comp.compare(current.value, t) == 0) {
            return false;
        }
        int level = 1;
        Random random = new Random();
        while (level < maxLevel && random.nextBoolean()) {
            level++;
        }
        Entry<T> newEntry = new Entry<>(t, level);
        for (int i = 0; i < level; i++) {
            Entry<T> prev = insertPlace[i];
            Entry<T> next = insertPlace[i].next[i];
            prev.next[i] = newEntry;
            newEntry.prev[i] = prev;
            newEntry.next[i] = next;
            if (next != null) {
                next.prev[i] = newEntry;
            }
        }
        size++;
        return true;
    }

    @Override
    public boolean contains(Object o) {
        try {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            Comparator<? super T> comp = getComparator();
            Entry<T> current = start;
            for (int i = maxLevel - 1; i >= 0; i--) {
                while (current.next[i] != null && comp.compare(current.next[i].value, t) <= 0) {
                    current = current.next[i];
                }
            }
            return comp.compare(current.value, t) == 0;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        try {
            @SuppressWarnings("unchecked")
            T t = (T) o;
            Comparator<? super T> comp = getComparator();
            Entry<T>[] insertPlace = Entry.newArray(maxLevel);
            Entry<T> current = start;
            for (int i = maxLevel - 1; i >= 0; i--) {
                while (current.next[i] != null && comp.compare(current.next[i].value, t) < 0) {
                    current = current.next[i];
                }
                insertPlace[i] = current;
            }
            current = current.next[0];
            if (comp.compare(current.value, t) == 0) {
                for (int i = 0; i < current.next.length; i++) {
                    insertPlace[i].next[i] = current.next[i];
                    if (current.next[i] != null) {
                        current.next[i].prev[i] = insertPlace[i];
                    }
                }
                return true;
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }

    private static class Entry<T> {
        private T value;
        private Entry<T>[] next;
        private Entry<T>[] prev;

        @SafeVarargs
        private static <E> E[] newArray(int length, E... array) {
            return Arrays.copyOf(array, length);
        }

        public Entry(T value, int height) {
            this.value = value;
            next = newArray(height);
            prev = newArray(height);
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Entry<T> current = start;

            @Override
            public boolean hasNext() {
                return current.next[0] != null;
            }

            @Override
            public T next() {
                if (current != null) {
                    current = current.next[0];
                    return current.value;
                }
                return null;
            }
        };
    }

    @Override
    public T lower(T t) {
        Comparator<? super T> comp = getComparator();
        Entry<T> current = start;
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && comp.compare(current.next[i].value, t) < 0) {
                current = current.next[i];
            }
        }
        return current.value;
    }

    @Override
    public T higher(T t) {
        Comparator<? super T> comp = getComparator();
        Entry<T> current = start;
        for (int i = maxLevel - 1; i >= 0; i--) {
            while (current.next[i] != null && comp.compare(current.next[i].value, t) <= 0) {
                current = current.next[i];
            }
        }
        return current.next[0] == null ? null : current.next[0].value;
    }

    // --------------------- TODO implement ------------------------

    @Override
    public T floor(T t) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @Override
    public T ceiling(T t) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @Override
    public T first() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @Override
    public T last() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }


    // ----------------- unsupported operations --------------------

    private static final String UNSUPPORTED_MSG = "Operation is not supported";

    @NotNull
    @Override
    public NavigableSet<T> descendingSet() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public Iterator<T> descendingIterator() {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public NavigableSet<T> subSet(T fromElement, boolean fromInclusive, T toElement, boolean toInclusive) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public NavigableSet<T> headSet(T toElement, boolean inclusive) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public NavigableSet<T> tailSet(T fromElement, boolean inclusive) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }

    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        throw new UnsupportedOperationException(UNSUPPORTED_MSG);
    }
}
