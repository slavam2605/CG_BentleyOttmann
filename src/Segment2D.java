/**
 * @author Моклев Вячеслав
 */
public class Segment2D {
    private Point2D start;
    private Point2D end;

    public Segment2D(Point2D start, Point2D end) {
        this.start = start;
        this.end = end;
    }

    public Segment2D(double x1, double y1, double x2, double y2) {
        this(new Point2D(x1, y1), new Point2D(x2, y2));
    }

    public void swap() {
        Point2D t = start;
        start = end;
        end = t;
    }

    public Point2D getStart() {
        return start;
    }

    public Point2D getEnd() {
        return end;
    }
}
