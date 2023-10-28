import javax.swing.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 750;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 25;                                        // Size of the Units
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
    static int DELAY = 75;                                            // Speed of the Snake
    private int initialDelay = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    static int topScore = 0;
    int appleX; //Where the apple appears in X
    int appleY; //Where the apple appears in Y
    char direction = 'R';
    boolean running = false;
    Set<Color> usedColors = new HashSet<>();
    Graphics g;
    Color snakeColor = Color.green;
    Timer timer;
    Random random;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBounds(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        this.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g; // Store the Graphics Object
        draw(g);

        // Check for level change and draw the level text

        if (applesEaten > 0 && applesEaten % 3 == 0) {
            int level = (applesEaten / 3) + 1;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Ink Free", Font.BOLD, 30));
            g.drawString("Level: " + level, (SCREEN_WIDTH/2) - UNIT_SIZE, 30);
        }
    }
    public void draw(Graphics g) {

        if(running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);    // Making a X Grid Line
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);     // Making a Y Grid Line
            }
            g.setColor(Color.RED);                                                  // Giving red color to the Apple
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);                          // Giving an oval form to the Apple

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(snakeColor.darker());
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);                   // Fill the rectangle with the new color
                } else {
                    g.setColor(snakeColor);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);                   // Fill the rectangle with the new color
                }
            }
            g.setColor(Color.RED);
            g.setFont(new Font("Ink Free", Font.BOLD,30));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten) - 10, g.getFont().getSize() + 10);
        } else {
            gameOver(g);
        }
    }
    public void newApple() {
        boolean onSnake = true;
        while(onSnake) {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;       // Random X position of Apple
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;      // Random Y position of Apple

            // Check if the apple's position coincides with any part of the snake's body
            for (int i = 0; i < bodyParts; i++) {
                if (appleX == x[i] && appleY == y[i]) {
                    onSnake = true;
                    break;
                } else {
                    onSnake = false;
                }
            }
        }
    }
    public void move() {
        for(int i = bodyParts; i > 0 ;i--) {
            x[i] = x[i-1];                      // Here we are moving all coordinates by 1 spot in  X
            y[i] = y[i-1];                      // Here we are moving all coordinates by 1 spot in  Y
        }

        switch (direction) {
            case'U':
                y[0] = y[0] - UNIT_SIZE;        // This is the head of our Snake moving UP
                break;
            case'D':
                y[0] = y[0] + UNIT_SIZE;        // This is the head of our Snake moving DOWN
                break;
            case'L':
                x[0] = x[0] - UNIT_SIZE;        // This is the head of our Snake moving LEFT
                break;
            case'R':
                x[0] = x[0] + UNIT_SIZE;        // This is the head of our Snake moving RIGHT
                break;
        }
    }

    public void checkApple(Graphics g) {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++; // Checking if the head of the Snake is equal to the position of the Apple in X and Y
            applesEaten++;

            increaseDifficulty();
            newApple();

            // Change the snake's color after level change
            if (applesEaten > 0 && applesEaten % 3 == 0) {
                int level = applesEaten / 3;

                // Generate a new color not similar to black and not in usedColors set
                Color newColor = generateNewColor();

                // Add the new color to usedColors set
                usedColors.add(newColor);

                // Assign the new color to the snakeColor
                snakeColor = newColor;

                repaint();
            }
            // Updates the Top Score if the current is beaten
            if (applesEaten > topScore) {
                topScore = applesEaten;
            }

            // Checking if the applesEaten is a multiple of 3 and draw the level text
            if (applesEaten > 0 && applesEaten % 3 == 0) {
                // Calculating the current level based on applesEaten
                int level = applesEaten / 3;

                // Repaint the component to draw the level text
                repaint();
            }
        }
    }

    // Generate new color to the snake and colors that haven't been used before
    private Color generateNewColor() {
        Color newColor;
        do {
            newColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while (isColorSimilarToBlack(newColor) || usedColors.contains(newColor));
        return newColor;
    }

    // Check if the color is similar to black
    private boolean isColorSimilarToBlack(Color color) {
        int threshold = 50; // Adjust the threshold as needed
        return Math.abs(color.getRed() - Color.BLACK.getRed()) < threshold
                && Math.abs(color.getGreen() - Color.BLACK.getGreen()) < threshold
                && Math.abs(color.getBlue() - Color.BLACK.getBlue()) < threshold;
    }
    public void increaseDifficulty() {
        if (applesEaten > 0 && applesEaten % 3 == 0) {

            // Increase the game speed by reducing delay
            DELAY -= 10; // Decrease the delay by 10 every 3 apples eaten
            timer.setDelay(DELAY); // Update the timer delay
        }
    }
    public void checkCollisions() {
        for(int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {          // Checking if the head collided with the body
                running = false;
            }
        }
        if(x[0] < 0) {
            running = false;                                // Checking if the head touches left border
        }
        if(x[0] > SCREEN_WIDTH) {
            running = false;                                // Checking if the head touches right border
        }
        if(y[0] < 0) {
            running = false;                                // Checking if the head touches top border
        }
        if(y[0] > SCREEN_HEIGHT) {
            running = false;                                // Checking if the head touches bottom border
        }

        if(!running) {
            timer.stop();
        }
    }
    public void gameOver(Graphics g) {
        // Top Score Text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Top Score: " + topScore, (SCREEN_WIDTH - metrics1.stringWidth("Top Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 - 145);

        // Score Text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 50));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 - 90);

        // Game Over Text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD,75));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics3.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2); // To be in the center of the Screen

        // Restart Game Text
        g.setColor(Color.RED);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Press 'R' to restart", (SCREEN_WIDTH - metrics4.stringWidth("Press 'R' to restart")) / 2, SCREEN_HEIGHT / 2 + 50);
    }

    private void restartGame() {
        // Reset the game variables to their initial state
        bodyParts = 6;
        if(applesEaten > topScore) {
            topScore = applesEaten;
        }
        applesEaten = 0;
        direction = 'R';
        running = true;
        newApple();
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        timer.start();
        snakeColor = Color.GREEN;

        // Reset the speed of the snake to the initial value
        DELAY = initialDelay;
        timer.setDelay(DELAY); // Update the timer delay

        repaint();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple(g);
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{ //InnerClass to events on keyboard, so snake can change the direction
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;

                case KeyEvent.VK_R: // Restart the game when 'R' is pressed
                    restartGame();
                    break;
            }
        }
    }
}
