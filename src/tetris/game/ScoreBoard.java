package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.util.TimerTask;

import static tetris.game.Config.*;
import static tetris.game.Draw.drawRect;


public class ScoreBoard extends JPanel {
    private int score;
    private int level;
    private int lines;
    private int time;

    private Tetronimo previewTetronimo;
    JLabel scoreLabel;
    JLabel levelLabel;
    JLabel linesLabel;
    JLabel timeLabel;

    public ScoreBoard() {
        setPreferredSize(new Dimension(scoreBoardWidth, height));
        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(Color.black);
        setLayout(null);

        score = 0;
        scoreLabel = new JLabel("SCORE:" + score);
        scoreLabel.setForeground(Color.white);
        scoreLabel.setBounds(scoreBoardMargin, 16 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        add(scoreLabel);

        level = 0;
        levelLabel = new JLabel("LEVEL:" + level);
        levelLabel.setForeground(Color.white);
        levelLabel.setBounds(scoreBoardMargin, 17 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        add(levelLabel);

        linesLabel = new JLabel("LINES: 0");
        linesLabel.setForeground(Color.white);
        linesLabel.setBounds(scoreBoardMargin, 18 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        add(linesLabel);

        timeLabel = new JLabel("TIME: 0");
        timeLabel.setForeground(Color.white);
        timeLabel.setBounds(scoreBoardMargin, 19 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        add(timeLabel);
    }

    public void addScore(int update) {
        score += update;
        scoreLabel.setText("SCORE: " + score);
    }

    public void addTime() {
        time += 1;
        timeLabel.setText("TIME: " + time);
    }

    public void addLine() {
        lines += 1;
        linesLabel.setText("LINES: " + lines);

        level = lines/10;
        levelLabel.setText("LEVEL: " + level);
    }

    public void drawPreviewTetronimo(Tetronimo tetronimo) {
        previewTetronimo = tetronimo;
        repaint();
    }

    public int getLevel() {
        return level;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int[] coords: previewTetronimo.pos) {
            Color color = tetronimoColors[previewTetronimo.ordinal()];
            Color borderColor = tetronimoBorderColors[previewTetronimo.ordinal()];

            if (previewTetronimo == Tetronimo.I_Tetronimo || previewTetronimo == Tetronimo.O_Tetronimo) {
                drawRect((Graphics2D) g, coords[0] + 3, coords[1] + 1, color, borderColor);
            } else {
                drawRect((Graphics2D) g, coords[0] + 3.5, coords[1] + 1, color, borderColor);
            }
        }
    }
}

class GameTimer extends TimerTask {
    private ScoreBoard scoreBoard;

    public GameTimer(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public void run() {
        scoreBoard.addTime();
    }
}