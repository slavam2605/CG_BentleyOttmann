import org.apache.commons.math3.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Моклев Вячеслав
 */
public class Main {
    private static final boolean VISUAL = true;
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
        //segments.add(new Segment2D(50, 100, 220, 200));
        //segments.add(new Segment2D(200, 100, 100, 200));
        //segments.add(new Segment2D(170, 200, 400, 150));
        //segments.add(new Segment2D(250, 100, 450, 200));
        for (int i = 0; i < 10; i++) {
            segments.add(new Segment2D(100 + 20 * i, 80, 100 + 20 * i, 300));
        }
        for (int i = 0; i < 10; i++) {
            segments.add(new Segment2D(80, 100 + 20 * i, 300, 100 + 20 * i));
        }
        if (VISUAL) {
            createWindow(640, 480);
            SwingUtilities.invokeAndWait(() -> {
                segments.forEach(panel::addSegment);
                panel.repaint();
            });
        }
        BentleyOttmann bo = new BentleyOttmann(segments);
        for (Pair<Integer, Integer> pair: bo) {
            System.out.println(pair);
        }
    }

}
