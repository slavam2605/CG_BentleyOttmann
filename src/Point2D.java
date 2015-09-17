/**
 * @author Моклев Вячеслав
 */
public class Point2D {
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
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
