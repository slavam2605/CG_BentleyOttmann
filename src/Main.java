import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class Main {
    private static JFrame mainFrame;
    private static DrawingPanel panel;

    private static void createWindow(int width, int height) {
        mainFrame = new JFrame("Bentley-Ottmann algorithm");
        panel = new DrawingPanel();
        panel.setPreferredSize(new Dimension(width, height));
        mainFrame.add(panel);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static String toString(int x) {
        switch (x) {
            case CG.INTERSECTION: return "INTERSECTION";
            case CG.OVERLAY: return "OVERLAY";
            case CG.DISJOINT: return "DISJOINT";
            case CG.TOUCHING: return "TOUCHING";
            case CG.POINT_TOUCHING: return "POINT_TOUCHING";
            default: return "UNKNOWN";
        }
    }

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        createWindow(640, 480);
        List<Segment2D> segments = new ArrayList<>();
        segments.add(new Segment2D(100, 100, 200, 200));
        segments.add(new Segment2D(200, 100, 100, 200));
        segments.add(new Segment2D(170, 200, 400, 150));
        segments.add(new Segment2D(250, 100, 400, 200));
        SwingUtilities.invokeAndWait(() -> {
            segments.forEach(panel::addSegment);
            panel.repaint();
        });
    }

}
