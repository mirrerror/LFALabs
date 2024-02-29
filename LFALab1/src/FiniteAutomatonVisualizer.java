import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FiniteAutomatonVisualizer extends JFrame {
    private final Map<String, Point> statePositions;

    public FiniteAutomatonVisualizer(FiniteAutomaton automaton) {
        super("Finite Automaton Visualization");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statePositions = new AutomatonLayout().generateLayout(automaton.getStates());

        AutomatonPanel automatonPanel = new AutomatonPanel(automaton);
        add(automatonPanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class AutomatonPanel extends JPanel {
        private final FiniteAutomaton automaton;

        public AutomatonPanel(FiniteAutomaton automaton) {
            this.automaton = automaton;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (Map.Entry<String, Map<String, Set<String>>> entry : automaton.getTransitions().entrySet()) {
                String fromState = entry.getKey();
                Map<String, Set<String>> toStatesMap = entry.getValue();

                for (Map.Entry<String, Set<String>> toStateEntry : toStatesMap.entrySet()) {
                    String delta = toStateEntry.getKey();
                    Set<String> toStates = toStateEntry.getValue();

                    for(String toState : toStates) {
                        drawTransition(g, fromState, toState, delta);
                    }
                }
            }

            drawStartingTransition(g, automaton.getStartState());

            for (Map.Entry<String, Point> entry : statePositions.entrySet()) {
                drawState(g, entry.getKey(), entry.getValue());
            }

            for(String state : automaton.getAcceptStates()) {
                drawFinal(g, state);
            }
        }


        private void drawState(Graphics g, String state, Point position) {
            g.setColor(Color.PINK);
            g.fillOval(position.x - 20, position.y - 20, 40, 40);
            g.setColor(Color.BLACK);
            g.drawOval(position.x - 20, position.y - 20, 40, 40);
            g.drawString(state, position.x - 5, position.y + 5);
        }

        private void drawTransition(Graphics g, String fromState, String toState, String delta) {
            Point fromPosition = statePositions.get(fromState);
            Point toPosition = statePositions.get(toState);

            Point intersection = calculateIntersectionPoint(fromPosition, toPosition, 10);

            int labelX = (fromPosition.x + toPosition.x) / 2;
            int labelY = (fromPosition.y + toPosition.y) / 2 - 5;

            if (fromState.equals(toState)) {
                drawSelfLoop(g, fromPosition.x, fromPosition.y, delta);
            } else {
                drawArrow(g, fromPosition.x, fromPosition.y, intersection.x, intersection.y);
            }

            g.drawString(delta, labelX, labelY);
        }

        private void drawFinal(Graphics g, String state) {
            int offset = 15;

            Point position = statePositions.get(state);
            g.setColor(Color.PINK);
            g.fillOval(position.x - offset, position.y - offset, 30, 30);
            g.setColor(Color.BLACK);
            g.drawOval(position.x - offset, position.y - offset, 30, 30);
            g.drawString(state, position.x - 5, position.y + 5);
        }

        private void drawStartingTransition(Graphics g, String state) {
            Point toPosition = statePositions.get(state);
            Point fromPosition = new Point(toPosition.x + 80, toPosition.y);

            Point intersection = calculateIntersectionPoint(fromPosition, toPosition, 10);

            drawArrow(g, fromPosition.x, fromPosition.y, intersection.x, intersection.y);
        }

        private void drawSelfLoop(Graphics g, int x, int y, String withSymbol) {
            int loopRadius = 20;
            int arrowSize = 5;

            g.drawOval(x - loopRadius, y - loopRadius * 2, loopRadius * 2, loopRadius * 2);

            int arrowX1 = x + (int) (arrowSize * Math.cos(Math.PI / 6));
            int arrowY1 = y - loopRadius * 2 + (int) (arrowSize * Math.sin(Math.PI / 6));
            int arrowX2 = x + (int) (arrowSize * Math.cos(-Math.PI / 6));
            int arrowY2 = y - loopRadius * 2 + (int) (arrowSize * Math.sin(-Math.PI / 6));

            g.drawLine(x, y - loopRadius * 2, arrowX1, arrowY1);
            g.drawLine(x, y - loopRadius * 2, arrowX2, arrowY2);

            int labelX = x - g.getFontMetrics().stringWidth(withSymbol) / 2;
            int labelY = y - loopRadius * 2 - 10;
            g.drawString(withSymbol, labelX, labelY);
        }

        private Point calculateIntersectionPoint(Point from, Point to, int circleRadius) {
            double dx = to.x - from.x;
            double dy = to.y - from.y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            int intersectionX = from.x + (int) ((dx / distance) * (distance - circleRadius * 0.7));
            int intersectionY = from.y + (int) ((dy / distance) * (distance - circleRadius * 0.7));

            return new Point(intersectionX, intersectionY);
        }

        private void drawArrow(Graphics g, int x1, int y1, int x2, int y2) {
            Graphics2D g2d = (Graphics2D) g.create();

            int arrowLength = 10;

            double angle = Math.atan2(y2 - y1, x2 - x1);

            Point intersection = calculateIntersectionPoint(new Point(x1, y1), new Point(x2, y2), 20);

            int x3 = (int) (intersection.x - arrowLength * Math.cos(angle - Math.PI / 6));
            int y3 = (int) (intersection.y - arrowLength * Math.sin(angle - Math.PI / 6));
            int x4 = (int) (intersection.x - arrowLength * Math.cos(angle + Math.PI / 6));
            int y4 = (int) (intersection.y - arrowLength * Math.sin(angle + Math.PI / 6));

            g2d.drawLine(x1, y1, intersection.x, intersection.y);

            Polygon arrowhead = new Polygon();
            arrowhead.addPoint(intersection.x, intersection.y);
            arrowhead.addPoint(x3, y3);
            arrowhead.addPoint(x4, y4);
            g2d.fill(arrowhead);

            g2d.dispose();
        }
    }

    public static class AutomatonLayout {

        public Map<String, Point> generateLayout(Set<String> states) {
            Map<String, Point> statePositions = new HashMap<>();

            int radius = 100;
            int centerX = 300;
            int centerY = 200;

            double angle = 0;
            double angleIncrement = 2 * Math.PI / states.size();

            for (String state : states) {
                if (state != null) {
                    int x = (int) (centerX + radius * Math.cos(angle));
                    int y = (int) (centerY + radius * Math.sin(angle));
                    statePositions.put(state, new Point(x, y));
                    angle += angleIncrement;
                }
            }

            return statePositions;
        }

    }
}