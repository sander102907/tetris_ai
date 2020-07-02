package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

    public GUI() {
        setTitle("Tetris");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Stop process on window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        setLocationRelativeTo(null);

        Image img = Toolkit.getDefaultToolkit().getImage("./resources/images/background.jpg");

        setContentPane(new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        });


        setFocusable(true);
        requestFocusInWindow();

        Tetris tetris2 = new Tetris();
        getContentPane().add(tetris2);

        ScoreBoard scoreBoard2 = new ScoreBoard();
        tetris2.setScoreBoard(scoreBoard2);

        getContentPane().add(scoreBoard2);

        Tetris tetris = new Tetris();
        getContentPane().add(tetris);

        ScoreBoard scoreBoard = new ScoreBoard();
        tetris.setScoreBoard(scoreBoard);

        getContentPane().add(scoreBoard);

        setVisible(true);

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    tetris.moveLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    tetris.moveRight();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    tetris.rotateTetronimo();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    tetris.moveDown();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    tetris.hardDrop();
                }

                else if (e.getKeyCode() == KeyEvent.VK_A) {
                    tetris2.moveLeft();
                } else if (e.getKeyCode() == KeyEvent.VK_D) {
                    tetris2.moveRight();
                } else if (e.getKeyCode() == KeyEvent.VK_W) {
                    tetris2.rotateTetronimo();
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    tetris2.moveDown();
                } else if (e.getKeyCode() == KeyEvent.VK_R) {
                    tetris2.hardDrop();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT ||
                    e.getKeyCode() == KeyEvent.VK_UP ||
                    e.getKeyCode() == KeyEvent.VK_DOWN ||
                    e.getKeyCode() == KeyEvent.VK_SPACE
                ) {
                    tetris.setSoftDrop(false);
                } else if (e.getKeyCode() == KeyEvent.VK_A ||
                        e.getKeyCode() == KeyEvent.VK_D ||
                        e.getKeyCode() == KeyEvent.VK_W ||
                        e.getKeyCode() == KeyEvent.VK_S ||
                        e.getKeyCode() == KeyEvent.VK_R
                ) {
                    tetris2.setSoftDrop(false);
                }
            }
        });

        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.play("theme_song.wav", true);
    }


    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}
