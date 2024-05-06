import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends JPanel implements ActionListener {

    // Constants
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;
    private static final int PADDLE_Y_OFFSET = 50;
    private static final int NBRICKS_PER_ROW = 10;
    private static final int NBRICK_ROWS = 10;
    private static final int BRICK_SEP = 4;
    private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
    private static final int BRICK_HEIGHT = 8;
    private static final int BALL_RADIUS = 10;
    private static final int BRICK_Y_OFFSET = 70;
    private static final int NTURNS = 3;
    private static final int DELAY = 10;

    // Game objects
    private int turnsLeft = NTURNS;
    private int brickCount = NBRICK_ROWS * NBRICKS_PER_ROW;
    private JLabel turnLabel;
    private Timer timer;
    private int ballX, ballY;
    private int ballVx, ballVy;
    private JLabel ball;
    private JLabel paddle;

    // Brick colors
    private Color[] brickColors = {
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.CYAN
    };

    public Breakout() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(null);
        setBackground(Color.BLACK);

        // Create paddle
        paddle = new JLabel();
        paddle.setOpaque(true);
        paddle.setBackground(Color.WHITE);
        paddle.setBounds((WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET, PADDLE_WIDTH, PADDLE_HEIGHT);
        add(paddle);

        // Create ball
        ball = new JLabel();
        ball.setOpaque(true);
        ball.setBackground(Color.WHITE);
        ball.setBounds(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
        add(ball);
        ballX = WIDTH / 2 - BALL_RADIUS;
        ballY = HEIGHT / 2 - BALL_RADIUS;
        ballVx = 2;
        ballVy = -2;

        // Create turn label
        turnLabel = new JLabel("Turns left: " + turnsLeft);
        turnLabel.setForeground(Color.WHITE);
        turnLabel.setBounds(WIDTH - 100, 10, 100, 20);
        add(turnLabel);

        // Start the game timer
        timer = new Timer(DELAY, this);
        timer.start();

        // Draw bricks
        int x = (WIDTH - (NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP))) / 2;
        int y = BRICK_Y_OFFSET;
        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                JLabel brick = new JLabel();
                brick.setOpaque(true);
                brick.setBackground(brickColors[i / 2]);
                brick.setBounds(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                add(brick);
                x += BRICK_WIDTH + BRICK_SEP;
            }
            y += BRICK_HEIGHT + BRICK_SEP;
            x = (WIDTH - (NBRICKS_PER_ROW * (BRICK_WIDTH + BRICK_SEP))) / 2;
        }

        // Make sure the panel is focusable for key events
        setFocusable(true);

        // Add key listener for paddle movement
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_LEFT) {
                    movePaddle(-5); // Move left
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    movePaddle(5); // Move right
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddle.getX(), paddle.getY(), PADDLE_WIDTH, PADDLE_HEIGHT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move the ball
        moveBall();

        // Redraw the scene
        repaint();
    }

    private void moveBall() {
        // Update ball position
        ballX += ballVx;
        ballY += ballVy;

        // Ball collision with walls
        if (ballX <= 0 || ballX >= WIDTH - 2 * BALL_RADIUS) {
            ballVx = -ballVx;
        }
        if (ballY <= 0) {
            ballVy = -ballVy;
        }

        // Ball collision with paddle
        Rectangle ballRect = new Rectangle(ballX, ballY, 2 * BALL_RADIUS, 2 * BALL_RADIUS);
        Rectangle paddleRect = new Rectangle(paddle.getX(), paddle.getY(), PADDLE_WIDTH, PADDLE_HEIGHT);
        if (ballRect.intersects(paddleRect)) {
            ballVy = -ballVy;
        }

        // Ball collision with bricks
        for (Component component : getComponents()) {
            if (component instanceof JLabel && !component.equals(ball) && !component.equals(paddle)) {
                Rectangle brickRect = component.getBounds();
                if (ballRect.intersects(brickRect) && component.isVisible()) {
                    ballVy = -ballVy;
                    component.setVisible(false); // Destroy the brick
                    brickCount--;
                    break;
                }
            }
        }

        // Ball lost
        if (ballY >= HEIGHT - 2 * BALL_RADIUS) {
            turnsLeft--;
            if (turnsLeft == 0) {
                gameOver("Game Over!");
            } else {
                turnLabel.setText("Turns left: " + turnsLeft);
                ballX = WIDTH / 2 - BALL_RADIUS;
                ballY = HEIGHT / 2 - BALL_RADIUS;
                ballVx = 2;
                ballVy = -2;
            }
        }

        // Check for win
        if (brickCount == 0) {
            gameOver("You Win!");
        }

        ball.setLocation(ballX, ballY);
    }

    private void movePaddle(int dx) {
        int newPaddleX = paddle.getX() + dx;
        if (newPaddleX >= 0 && newPaddleX <= WIDTH - PADDLE_WIDTH) {
            paddle.setLocation(newPaddleX, paddle.getY());
        }
    }

    private void gameOver(String message) {
        timer.stop();
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Breakout");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(new Breakout());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
