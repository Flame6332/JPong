import java.awt.Color;
import java.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Rectangle;
import java.awt.Circle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JOptionPane;

public class Pong extends JFrame {
  private final static int WIDTH = 700, HEIGHT = 450;
  private PongPanel panel; 
  
  public Pong() {
    setSize(WIDTH, HEIGHT);
    setTitle("JPong");
    setBackground(Color.WHITE);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    panel = new PongPanel(this);
    add(panel);
  }
  
  public PongPanel getPanel() {
    return panel;
  }
  
  public static void main(String[] args) {
    new Pong();
  }

}


public class PongPanel extends JPanel implements ActionListener, KeyListener {
  private Pong game;
  private Ball ball;
  private Paddle player1, player2;
  private int width, height;
  
  private updateDimensions() {
    width = game.getWidth;
    height = game.getHeight;  
  }
  
  public PongPanel (Pong game) {
    setBackground(Color.WHITE);
    this.game = game;
    updateDimensions();
    
    ball = new Ball(game, 30, 2);
    
    int paddleWidth = 10; 
    int paddleHeight = 60;
    int maxSpeed = 1;
    
    player1 = new Paddle(1, game, KeyEvent.VK_UP, KeyEvent.VK_DOWN,
      width * 0.9f, paddleWidth, paddleHeight, maxSpeed);
    player2 = new Paddle(2, game, KeyEvent.VK_W, KeyEvent.VK_S,
      width * 0.1f, paddleWidht, paddleHeight, maxSpeed);
    
    Timer timer = new Timer(5, this);
    timer.start();
    addKeyListener(this);
    setFocusable(true);
  }
  
  public void update() {
    updateDimensions();
    ball.update();
    player1.update();
    player2.update();
  }
  
  public Player getPlayer(int playerNum) {
    if (playerNum == 1) return player1;
    else return player2;
  }
  
  public void increaseScore(int playerNum) {
    if (playerNum == 1) player1.increaseScore();
    else player2.increaseScore();
  }
  
  public void actionPerformed(ActionEvent e) {
    update();
    repaint();
  }
  
  public void keyPressed(KeyEvent e) {
    player1.pressed(e.getKeyCode());
    player2.pressed(e.getKeyCode());
  }
  public void keyReleased(KeyEvent e) {
    player1.released(e.getKeyCode());
    player2.released(e.getKeyCode());
  }
  public void keyTyped(keyEvent e) {
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    g.drawString(player2.score + "  :  " + player1.score, 
      width / 2, height * 0.1f);
    ball.paint(g);
    player1.paint(g);
    player2.paint(g);
  }
  
}


public class Ball {
  private int size;
  private Pong game;
  private int x, y, xSpeed, ySpeed;
  
  public Ball(Pong game, int size, int speed) {
    this.game = game;
    this. size = size;
    xSpeed = speed;
    ySpeed = speed;
    resetBall();
  }
  
  public void resetBall() {
    x = game.getWidth / 2;
    y = Math.random() * game.getHeight();
  }
  
  public void update() {
    
    x += xSpeed;
    y += ySpeed;
  
    if (x < 0) {
      game.getPanel.increaseScore(1);
      resetBall();
      xSpeed *= -1;
    }
    else if (x > game.getWidth() - size) {
      game.getPanel.increaseScore(2);
      resetBall();
      xSpeed *= -1;
    }
    else if (y < 0 || y > game.getHeight - size) {
      ySpeed *= -1;
    }
    
    checkCollision();
    
  }
  
  public void checkCollision() {
    if (
      game.getPanel().getPlayer(1).getBounds().intersects(getBounds()) 
    ||
      game.getPanel().getPlayer(2).getBounds().intersects(getBounds())
    ) xSpeed *= -1;
  }
  
  public Rectangle getBounds() {
    return new Rectangle(x, y, size, size);
  }
  
    g.fillRect(x, y, size, size);
  }
  
}


public class Paddle {
  private int xSize, ySize;
  private Pong game;
  private int up, down;
  private int x, y, ySpeed;
  private int maxSpeed;
  
  public Paddle (Pong game, int up, int down, int x, 
      int xSize, int ySize, int maxSpeed) {
    this.game = game;
    this.x = x;
    y = game.getHeight / 2;
    this.up = up;
    this.down = down;
    this.xSize = xSize;
    this.ySize = ySize;
    this.maxSpeed = maxSpeed;
  }
  
  public void resetPaddle() {
    y = game.getHeight / 2;
  }
  
  public void update() {
    if (y > 0 && )
  }
  
}
