import org.jetbrains.annotations.NotNull;

/**
 * @author Моклев Вячеслав
 */
public class Point2D implements Comparable<Point2D> {
    public double x;
    public double y;

    public Point2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point2D() {
        this(0, 0);
    }

    @Override
    public int compareTo(@NotNull Point2D o) {
        if (x != o.x) {
            return Double.compare(x, o.x);
        }
        return Double.compare(y, o.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
