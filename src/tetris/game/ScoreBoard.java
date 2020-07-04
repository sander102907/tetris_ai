package tetris.game;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.TimerTask;

import static tetris.game.Config.*;
import static tetris.game.Draw.drawRect;


public class ScoreBoard extends JPanel implements Serializable {
    private static final long serialVersionUID = 1L;
    private int score;
    private int level;
    private int lines;
    private int time;
    private int highScore;

    private Tetronimo previewTetronimo;
    JLabel previewTetronimoLabel;
    JLabel highscoreLabel;
    JLabel scoreLabel;
    JLabel levelLabel;
    JLabel linesLabel;
    JLabel timeLabel;

    public ScoreBoard() {
        setOpaque(false);
        setPreferredSize(new Dimension(scoreBoardWidth, height));
//        setBorder(BorderFactory.createLineBorder(Color.white));
        setBackground(new Color(0, 0, 0, 200));

        setLayout(null);

        previewTetronimoLabel = new JLabel("NEXT:");
        previewTetronimoLabel.setForeground(Color.white);
        previewTetronimoLabel.setBounds(scoreBoardMargin + 20, 0, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        previewTetronimoLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(previewTetronimoLabel);

        highScore = GameFilesOperator.getHighscore();
        highscoreLabel = new JLabel("HIGHSCORE: " + highScore);
        highscoreLabel.setForeground(Color.white);
        highscoreLabel.setBounds((int) (0.75 * rectSize) + scoreBoardMargin, 15 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        highscoreLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(highscoreLabel);

        score = 0;
        scoreLabel = new JLabel("SCORE:" + score);
        scoreLabel.setForeground(Color.white);
        scoreLabel.setBounds((int) (0.75 * rectSize) + scoreBoardMargin, 16 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        scoreLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(scoreLabel);

        level = 0;
        levelLabel = new JLabel("LEVEL: " + level);
        levelLabel.setForeground(Color.white);
        levelLabel.setBounds((int) (0.75 * rectSize) + scoreBoardMargin, 17 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        levelLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(levelLabel);

        lines = 0;
        linesLabel = new JLabel("LINES: " + lines);
        linesLabel.setForeground(Color.white);
        linesLabel.setBounds((int) (0.75 * rectSize) + scoreBoardMargin, 18 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        linesLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(linesLabel);

        time = 0;
        timeLabel = new JLabel("TIME: " + time);
        timeLabel.setForeground(Color.white);
        timeLabel.setBounds((int) (0.75 * rectSize) + scoreBoardMargin, 19 * rectSize, scoreBoardWidth - 2 * scoreBoardMargin, rectSize);
        timeLabel.setFont(new Font("Century", Font.BOLD, 15));
        add(timeLabel);
    }

    public void addScore(int update) {
        score += update;
        scoreLabel.setText("SCORE: " + score);

        if (score > highScore) {
            highScore = score;
            highscoreLabel.setText("HIGHSCORE: " + highScore);
        }
    }

    public int getScore() {
        return score;
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

        g.setColor(new Color(0, 0, 0, 220));
        g.fillRect((int) (0.75 * rectSize), 0, (int) (4.5 * rectSize), 4 * rectSize);

        g.fillRect((int) (0.75 * rectSize), 15 * rectSize,  (int) (4.5 * rectSize), 5 * rectSize);

        for (int[] coords: previewTetronimo.pos) {
            Color color = tetronimoColors[previewTetronimo.tetronimoType.ordinal()];
            Color borderColor = tetronimoBorderColors[previewTetronimo.tetronimoType.ordinal()];

            if (previewTetronimo .tetronimoType == TetronimoType.I_Tetronimo ||
                    previewTetronimo.tetronimoType == TetronimoType.O_Tetronimo) {
                drawRect((Graphics2D) g, coords[0] + 2, coords[1] + 1, color, borderColor);
            } else {
                drawRect((Graphics2D) g, coords[0] + 2.5, coords[1] + 1, color, borderColor);
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