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
        if (Math.abs(det) >= EPS * t) {
            return det < 0 ? -1 : det > 0 ? 1 : 0 ;
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

    public static final int INTERSECTION = 1;
    public static final int DISJOINT = -1;
    public static final int OVERLAY = 0;
    public static final int TOUCHING = 2;
    public static final int POINT_TOUCHING = 3;

    public static int intersects(Segment2D a, Segment2D b) {
        int t1 = leftTurn(a.getStart(), a.getEnd(), b.getStart());
        int t2 = leftTurn(a.getStart(), a.getEnd(), b.getEnd());
        int t3 = leftTurn(b.getStart(), b.getEnd(), a.getStart());
        int t4 = leftTurn(b.getStart(), b.getEnd(), a.getEnd());
        if (t1 * t2 < 0) {
            if (t3 * t4 < 0) {
                return INTERSECTION;
            }
            if (t3 * t4 > 0) {
                return DISJOINT;
            }
            if (t3 + t4 != 0) {
                return TOUCHING;
            }
        }
        if (t1 * t2 > 0) {
            return DISJOINT;
        }
        if (t1 + t2 != 0) {
            if (t3 * t4 < 0) {
                return TOUCHING;
            }
            if (t3 * t4 > 0) {
                return DISJOINT;
            }
            if (t3 + t4 != 0) {
                return POINT_TOUCHING;
            }
        }
        return OVERLAY;
    }

}
