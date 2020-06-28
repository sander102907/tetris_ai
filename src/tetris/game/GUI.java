package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI {
    private JFrame mainFrame;

    public GUI() {
        prepareGUI();
    }

    private void prepareGUI(){
        mainFrame = new JFrame("Tetris");
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Stop process on window close
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        mainFrame.setLocationRelativeTo(null);

        JPanel container = new JPanel(new GridBagLayout());
        container.setPreferredSize(mainFrame.getSize());
        container.setBackground(Color.black);

        Tetris tetris = new Tetris();
        container.add(tetris);
        container.add(scorePanel(tetris.getPreferredSize().height));
        mainFrame.getContentPane().add(container);
        mainFrame.setVisible(true);
    }

    private JPanel scorePanel(int height) {
        JPanel scorePanel = new JPanel();
        scorePanel.setPreferredSize(new Dimension(200, height));
        scorePanel.setBorder(BorderFactory.createLineBorder(Color.white));
        scorePanel.setBackground(Color.black);
        JLabel scoreLabel = new JLabel("SCORE: 0");
        scoreLabel.setForeground(Color.white);
        scorePanel.add(scoreLabel);
        return scorePanel;
    }


    public static void main(String[] args) {
        GUI gui = new GUI();
    }
}
