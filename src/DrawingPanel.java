import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class DrawingPanel extends JPanel {

    private static class Segment {
        private int x1;
        private int y1;
        private int x2;
        private int y2;

        public Segment(Point2D start, Point2D end) {
            x1 = (int) Math.round(start.x);
            y1 = (int) Math.round(start.y);
            x2 = (int) Math.round(end.x);
            y2 = (int) Math.round(end.y);
        }
    }

    private List<Segment> segments;

    public DrawingPanel() {
        super();
        segments = new ArrayList<>();
    }

    public void addSegment(Point2D start, Point2D end) {
        segments.add(new Segment(start, end));
    }

    public void addSegment(Segment2D segment) {
        addSegment(segment.getStart(), segment.getEnd());
    }

    @Override
    protected void paintComponent(Graphics g) {
        int R = 3;
        g.clearRect(0, 0, getWidth(), getHeight());
        for (Segment segment: segments) {
            g.drawLine(segment.x1, getHeight() - segment.y1 - 1, segment.x2, getHeight() - segment.y2 - 1);
            g.drawOval(segment.x1 - R, getHeight() - segment.y1 - R - 1, 2 * R, 2 * R);
            g.drawOval(segment.x2 - R, getHeight() - segment.y2 - R - 1, 2 * R, 2 * R);
        }
    }
}
