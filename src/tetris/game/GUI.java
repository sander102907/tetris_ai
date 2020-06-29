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

//        JPanel container = new JPanel(new GridBagLayout());
//        container.setPreferredSize(this.getSize());
//        container.setBackground(Color.black);

        Tetris tetris = new Tetris();
        getContentPane().add(tetris);

        ScoreBoard scoreBoard = new ScoreBoard();
        tetris.setScoreBoard(scoreBoard);

        getContentPane().add(scoreBoard);
//        getContentPane().add(container);
        setVisible(true);


        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.play("theme_song.wav", true);
    }



    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}
