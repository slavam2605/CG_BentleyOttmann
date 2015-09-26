import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigDecimal;

/**
 * @author Моклев Вячеслав
 */
public class CG {

    public static double EPS = 1e-51; // 8 * EPS_m = 8 * 2^(-54) = 2^(-51)

    public static int leftTurn(Point2D a, Point2D b, Point2D c) {
        double det = (c.x - a.x) * (b.y - a.y) - (c.y - a.y) * (b.x - a.x);
        double t = Math.abs((c.x - a.x) * (b.y - a.y)) + Math.abs((c.y - a.y) * (b.x - a.x));
        if (Math.abs(det) > EPS * t) {
            return det < 0 ? -1 : 1 ;
        }
        BigFraction ax = new BigFraction(a.x);
        BigFraction ay = new BigFraction(a.y);
        BigFraction bx = new BigFraction(b.x);
        BigFraction by = new BigFraction(b.y);
        BigFraction cx = new BigFraction(c.x);
        BigFraction cy = new BigFraction(c.y);
        BigFraction bdet =
                        cx.subtract(ax).multiply(by.subtract(ay))
                .subtract(
                        cy.subtract(ay).multiply(bx.subtract(ax))
                );
        return bdet.compareTo(BigFraction.ZERO);
    }

    public static boolean intersects(Segment2D a, Segment2D b) {
        // check if AABBs are not overlapping
        if (Math.max(a.getStart().x, a.getEnd().x) < Math.min(b.getStart().x, b.getEnd().x) ||
            Math.max(a.getStart().y, a.getEnd().y) < Math.min(b.getStart().y, b.getEnd().y) ||
            Math.max(b.getStart().x, b.getEnd().x) < Math.min(a.getStart().x, a.getEnd().x) ||
            Math.max(b.getStart().y, b.getEnd().y) < Math.min(a.getStart().y, a.getEnd().y)) {
            return false;
        }
        int t1 = leftTurn(a.getStart(), a.getEnd(), b.getStart());
        int t2 = leftTurn(a.getStart(), a.getEnd(), b.getEnd());
        int t3 = leftTurn(b.getStart(), b.getEnd(), a.getStart());
        int t4 = leftTurn(b.getStart(), b.getEnd(), a.getEnd());
        return (t1 * t2 <= 0) && (t3 * t4 <= 0);
    }

    public static Point2D intersectionPoint(Segment2D a, Segment2D b) {
        double x1 = a.getStart().x;
        double x2 = a.getEnd().x;
        double x3 = b.getStart().x;
        double x4 = b.getEnd().x;
        double y1 = a.getStart().y;
        double y2 = a.getEnd().y;
        double y3 = b.getStart().y;
        double y4 = b.getEnd().y;
        return new Point2D(
                ((y1 * x2 - x1 * y2) * (x4 - x3) + (y3 * x4 - x3 * y4) * (x1 - x2)) / ((y1 - y2) * (x4 - x3) + (x1 - x2) * (y3 - y4)),
                ((y1 * x2 - x1 * y2) * (y4 - y3) + (y3 * x4 - x3 * y4) * (y1 - y2)) / ((y1 - y2) * (x4 - x3) + (x1 - x2) * (y3 - y4))
        );
    }

}
