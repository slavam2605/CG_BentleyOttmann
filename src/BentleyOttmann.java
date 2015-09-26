import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author Моклев Вячеслав
 */
public class BentleyOttmann {

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

    public static List<Pair<Integer, Integer>> findIntersections(final List<Segment2D> segments) {
        List<Pair<Integer, Integer>> result = new ArrayList<>();
        final Set<Pair<Integer, Integer>> done = new HashSet<>();
        NavigableSet<Integer> status = new TreeSet<>((Integer a, Integer b) -> {
            Segment2D as = segments.get(a);
            Segment2D bs = segments.get(b);
            if (done.contains(new Pair<>(a, b))) {
                return localize(as.getEnd(), bs);
            } else {
                return localize(as.getStart(), bs);
            }
        });
        PriorityQueue<Event> events = new PriorityQueue<>();
        for (int i = 0; i < segments.size(); i++) {
            events.add(Event.startEvent(i, segments.get(i).getStart()));
            events.add(Event.endEvent(i, segments.get(i).getEnd()));
        }
        while (!events.isEmpty()) {
            Event event = events.poll();
            switch (event.mode) {
                case Event.START:
                    Integer lower = status.lower(event.i);
                    Integer higher = status.higher(event.i);
                    status.add(event.i);
                    if (lower != null) {
                        events.add(Event.intersectionEvent(
                                        lower,
                                        event.i,
                                        CG.intersectionPoint(segments.get(lower), segments.get(event.i)))
                        );
                    }
                    if (higher != null) {
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
                    if (lower != null && higher != null) {
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
                    result.add(new Pair<>(i, j));
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
                    if (lower != null) {
                        events.add(Event.intersectionEvent(
                                        lower,
                                        j,
                                        CG.intersectionPoint(segments.get(lower), segments.get(j)))
                        );
                    }
                    if (higher != null) {
                        events.add(Event.intersectionEvent(
                                        higher,
                                        i,
                                        CG.intersectionPoint(segments.get(higher), segments.get(i)))
                        );
                    }
                    break;
                default:
                    throw new RuntimeException("Unknown event: mode = " + event.mode);
            }
        }
        return result;
    }

}
