import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class Main {
    private static final boolean VISUAL = false;
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

    public static void main(String[] args) throws InvocationTargetException, InterruptedException {
        List<Segment2D> segments = new ArrayList<>();
        segments.add(new Segment2D(50, 100, 220, 200));
        segments.add(new Segment2D(100, 200, 200, 100));
        segments.add(new Segment2D(170, 200, 400, 150));
        segments.add(new Segment2D(250, 100, 450, 200));
        if (VISUAL) {
            createWindow(640, 480);
            SwingUtilities.invokeAndWait(() -> {
                segments.forEach(panel::addSegment);
                panel.repaint();
            });
        }
        System.out.println(BentleyOttmann.findIntersections(segments));
    }

}
