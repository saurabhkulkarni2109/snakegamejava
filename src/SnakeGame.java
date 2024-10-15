import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int DOT_SIZE = 20;
    private static final int ALL_DOTS = 300;
    private static final int RAND_POS = 29;
    private static final int DELAY = 140;

    private final ArrayList<Point> snake = new ArrayList<>();
    private Point apple;
    private boolean running = false;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;
    private int score = 0;

    private Timer timer;

    public SnakeGame() {
        setBackground(Color.black);
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        initGame();
        addKeyListener(new TAdapter());
    }

    private void initGame() {
        running = true;
        inGame = true;
        score = 0;
        snake.clear();
        snake.add(new Point(100, 100));
        snake.add(new Point(80, 100));
        snake.add(new Point(60, 100));

        spawnApple();
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void spawnApple() {
        int x = (new Random().nextInt(RAND_POS)) * DOT_SIZE;
        int y = (new Random().nextInt(RAND_POS)) * DOT_SIZE;
        apple = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (inGame) {
            g.setColor(Color.red);
            g.fillRect(apple.x, apple.y, DOT_SIZE, DOT_SIZE);

            g.setColor(Color.green);
            for (Point point : snake) {
                g.fillRect(point.x, point.y, DOT_SIZE, DOT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", Font.BOLD, 14));
            g.drawString("Score: " + score, 10, 20);

            Toolkit.getDefaultToolkit().sync();
        } else {
            showGameOver(g);
        }
    }

    private void showGameOver(Graphics g) {
        String msg = "Game Over. Score: " + score;
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (WIDTH - metr.stringWidth(msg)) / 2, HEIGHT / 2);

        // Show an option pane for game over
        int choice = JOptionPane.showOptionDialog(
                this,
                "Game Over. Score: " + score + "\nWould you like to restart?",
                "Game Over",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Restart", "Exit"},
                "Restart");

        if (choice == JOptionPane.YES_OPTION) {
            initGame();
        } else {
            System.exit(0);
        }
    }

    private void checkCollision() {
        // Check collision with self
        for (int i = snake.size() - 1; i > 0; i--) {
            if (snake.get(0).equals(snake.get(i))) {
                inGame = false;
            }
        }

        // Check collision with walls
        if (snake.get(0).x >= WIDTH || snake.get(0).x < 0 || snake.get(0).y >= HEIGHT || snake.get(0).y < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
        }
    }

    private void checkApple() {
        if (snake.get(0).equals(apple)) {
            score++;
            Point lastSegment = snake.get(snake.size() - 1);
            snake.add(new Point(lastSegment));
            spawnApple();
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead = (Point) head.clone();

        if (left) newHead.translate(-DOT_SIZE, 0);
        if (right) newHead.translate(DOT_SIZE, 0);
        if (up) newHead.translate(0, -DOT_SIZE);
        if (down) newHead.translate(0, DOT_SIZE);

        snake.add(0, newHead);
        snake.remove(snake.size() - 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollision();
            checkApple();
        }
        repaint();
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !right) {
                left = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_RIGHT && !left) {
                right = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_UP && !down) {
                up = true;
                right = false;
                left = false;
            }

            if (key == KeyEvent.VK_DOWN && !up) {
                down = true;
                right = false;
                left = false;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
