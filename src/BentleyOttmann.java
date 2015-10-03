import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author Моклев Вячеслав
 */
public class BentleyOttmann implements Iterator<Pair<Integer, Integer>>, Iterable<Pair<Integer, Integer>> {

    private static class Event implements Comparable<Event> {
        public static final int START = 0;
        public static final int END = 1;
        public static final int INTERSECTION = 2;

        private int i;
        private int j;
        private double x;
        private double y;
        private int mode;

        private Event(int i, int j, double x, double y, int mode) {
            this.i = i;
            this.j = j;
            this.x = x;
            this.y = y;
            this.mode = mode;
        }

        public static Event startEvent(int i, Point2D p) {
            return new Event(i, 0, p.x, p.y, START);
        }

        public static Event endEvent(int i, Point2D p) {
            return new Event(i, 0, p.x, p.y, END);
        }

        public static Event intersectionEvent(int i, int j, Point2D p) {
            return new Event(i, j, p.x, p.y, INTERSECTION);
        }

        private static String str(int mode) {
            switch (mode) {
                case START: return "START";
                case END: return "END";
                case INTERSECTION: return "INTERSECTION";
                default: return "UNKNOWN";
            }
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ", " + x + ", " + y + ", " + str(mode) + ")";
        }

        @Override
        public int compareTo(@NotNull Event o) {
            if (x != o.x) {
                return Double.compare(x, o.x);
            }
            return Double.compare(y, o.y);
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj instanceof Event && compareTo((Event) obj) == 0;
        }
    }

    private static int localize(Point2D p, Segment2D s) {
        return CG.leftTurn(p, s.getEnd(), s.getStart());
    }

    // ------------------------------------------------------------------------------------------------------------

    final List<Segment2D> segments;
    final Set<Pair<Integer, Integer>> done;
    NavigableSet<Integer> status;
    PriorityQueue<Event> events;
    Pair<Integer, Integer> cached;

    public BentleyOttmann(List<Segment2D> segments) {
        this.segments = segments;
        done = new HashSet<>();
        init();
    }

    private void init() {
        // Arrange ends of segment in ascending order
        segments.stream().filter(segment -> segment.getStart().compareTo(segment.getEnd()) > 0).forEach(Segment2D::swap);
        status = new SkipList<>((Integer a, Integer b) -> {
            Segment2D as = segments.get(a);
            Segment2D bs = segments.get(b);
            if (done.contains(new Pair<>(a, b))) {
                return localize(as.getEnd(), bs);
            } else {
                return localize(as.getStart(), bs);
            }
        });
        events = new PriorityQueue<>();
        for (int i = 0; i < segments.size(); i++) {
            events.add(Event.startEvent(i, segments.get(i).getStart()));
            events.add(Event.endEvent(i, segments.get(i).getEnd()));
        }
        cached = null;
    }

    @Override
    public boolean hasNext() {
        cached = next();
        return cached != null;
    }

    @Override
    public Pair<Integer, Integer> next() {
        if (cached != null) {
            Pair<Integer, Integer> result = cached;
            cached = null;
            return result;
        }
        while (!events.isEmpty()) {
            Event event = events.poll();
            switch (event.mode) {
                case Event.START:
                    Integer lower = status.lower(event.i);
                    Integer higher = status.higher(event.i);
                    status.add(event.i);
                    if (lower != null && CG.intersects(segments.get(lower), segments.get(event.i))) {
                        events.add(Event.intersectionEvent(
                                        lower,
                                        event.i,
                                        CG.intersectionPoint(segments.get(lower), segments.get(event.i)))
                        );
                    }
                    if (higher != null && CG.intersects(segments.get(higher), segments.get(event.i))) {
                        events.add(Event.intersectionEvent(
                                        higher,
                                        event.i,
                                        CG.intersectionPoint(segments.get(higher), segments.get(event.i)))
                        );
                    }
                    break;
                case Event.END:
                    lower = status.lower(event.i);
                    higher = status.higher(event.i);
                    status.remove(event.i);
                    if (lower != null && higher != null && CG.intersects(segments.get(lower), segments.get(higher))) {
                        events.add(Event.intersectionEvent(
                                        lower,
                                        higher,
                                        CG.intersectionPoint(segments.get(lower), segments.get(higher)))
                        );
                    }
                    break;
                case Event.INTERSECTION:
                    int i = event.i;
                    int j = event.j;
                    //noinspection ConstantConditions
                    if (status.comparator().compare(i, j) > 0) {
                        int t = i;
                        i = j;
                        j = t;
                    }
                    status.remove(i);
                    status.remove(j);
                    done.add(new Pair<>(i, j));
                    done.add(new Pair<>(j, i));
                    status.add(i);
                    status.add(j);
                    lower = status.lower(j);
                    higher = status.higher(i);
                    if (lower != null && CG.intersects(segments.get(lower), segments.get(j))) {
                        events.add(Event.intersectionEvent(
                                        lower,
                                        j,
                                        CG.intersectionPoint(segments.get(lower), segments.get(j)))
                        );
                    }
                    if (higher != null && CG.intersects(segments.get(higher), segments.get(i))) {
                        events.add(Event.intersectionEvent(
                                        higher,
                                        i,
                                        CG.intersectionPoint(segments.get(higher), segments.get(i)))
                        );
                    }
                    return new Pair<>(i, j);
                default:
                    throw new RuntimeException("Unknown event: mode = " + event.mode);
            }
        }
        return null;
    }

    @Override
    public Iterator<Pair<Integer, Integer>> iterator() {
        return this;
    }

}
