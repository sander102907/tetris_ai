package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Tetris extends JPanel implements Runnable {
    static final Random rand = new Random();
    private int height = 950;
    private int rows = 20;
    private int columns = 10;
    private int currentRow;
    private int currentColumn;
    private Tetronimo nextTetronimo;
    Thread gravityThread;

    public Tetris() {
        setPreferredSize(new Dimension(height/2, height));
        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(Color.black);
        setFocusable(true);
        requestFocusInWindow();

        getTetronimo();
        start();


        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        currentColumn--;
                        break;

                    case KeyEvent.VK_RIGHT:
                        currentColumn++;
                }
                repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void getTetronimo() {
        Tetronimo[] tetronimos = Tetronimo.values();
        nextTetronimo = tetronimos[rand.nextInt(tetronimos.length)];
        currentRow = 0;
        currentColumn = 5;
    }

    private void start() {
        gravityThread = new Thread(this);
        gravityThread.start();
    }

    @Override
    public void run() {
        while (Thread.currentThread() == gravityThread) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println(e);
                return;
            }

            currentRow += 1;
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawTetronimo(g);

    }

    private void drawTetronimo(Graphics2D g) {
        for (int[] coords: nextTetronimo.pos) {
            drawRect(g, coords);
        }
    }

    private void drawRect(Graphics2D g, int[] coords) {
        g.setColor(Color.red);

        int rectSize = height/rows;

        g.fillRect((currentColumn + coords[0]) * rectSize, (currentRow + coords[1]) * rectSize, rectSize, rectSize);
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.white);
        g.drawRect((currentColumn + coords[0]) * rectSize, (currentRow + coords[1]) * rectSize, rectSize, rectSize);
    }
}
